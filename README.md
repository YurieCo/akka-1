#### Akka 例子


本工程是Akka的例子，Akka规模比较大，而且在中国JAVA资料较少。这里，列出Akka常用的remote，cluster，persistenc三部分例子。通过这三个例子，读者除了可以了解akka的远程，集群，持久化之外，还可以了解akka的route,log,dispatcher,at least once等知识点。


#### remote 
remote子工程包括client和server两部分。

1.server
server通过以下配置对外提供配置。相当于http服务容器启动一个web服务。
    
    remote {
	    enabled-transports = ["akka.remote.netty.tcp"]
	    netty.tcp {
	      hostname = "127.0.0.1"
	      port = 2552
	    }
	}
	
2.client	
client调用的时候通过以下语句进行远程调用

    ActorSelection remoteActor = getContext().actorSelection("akka.tcp://remote-server@127.0.0.1:2552/user/producer") ;
    
####dispatcher
dispatcher相当于一个线程池，定义角色通过哪种线程池往receiver发送消息。dispatcher是为了并行等问题（笼统来说）。dispatcher的配置如

     my-dispatcher {
		type = Dispatcher
		executor = "fork-join-executor"
		fork-join-executor {
			parallelism-min = 2
			parallelism-factor = 2.0
			parallelism-max = 2
		}
		throughput = 100
		mailbox-capacity = -1
		mailbox-type =""
     } 
     
dispatcher有许多种类型，这里不详细列，可以查看官网

配置某个角色用哪种dispatcher，有两种方式，一种是配置文件，另外一种是编码。
文件配置注意path的命名方式./producer代表该actor是在main里面启动的。/producer/consumer代表该actor是在producer里面启动的。
	
	//配置要在akka{}之外
    actor.deployment { 
	  /producer {
	    dispatcher = my-dispatcher
	  }
	  
	  /producer/consumer {
	    dispatcher = my-dispatcher
	    router = round-robin-pool
	    resizer {
	      lower-bound = 3
	      upper-bound = 15
	      messages-per-resize = 100
	    }
	  }		  
	}
	//生成actor，注意FromConfig.getInstance().props()，不然读取不了配置
	consumer=getContext().actorOf(FromConfig.getInstance().props(Props.create(Consumer.class)),"consumer");
	

####router
Router相当于一个objectpool。可以配置负载策略。负载策略查看官网。配置如下。配置意思是随机负载，最少3个actor，最多15个。读取配置的方式，和dispatcher是一样的。router是为了解决balance的问题。

    	/producer/consumer {
	    dispatcher = my-dispatcher
	    router = round-robin-pool
	    resizer {
	      lower-bound = 3
	      upper-bound = 15
	      messages-per-resize = 100
	    }
	  }	
	  
router分为pool和group两种.相关的类型，参考官网。pool表示routee。引用别人的一段话。

在Router-Pool模式中Router负责构建所有的Routee。如此所有Routee都是Router的直属子级Actor，可以实现Router对Routees的直接监管。由于这种直接的监管关系，Router-Pool又可以按运算负载自动增减Routee，能更有效地分配利用计算资源。Router-Group模式中的Routees由外界其它Actor产生，特点是能实现灵活的Routee构建和监控，可以用不同的监管策略来管理一个Router下的Routees，比如可以使用BackoffSupervisor。从另一方面来讲，Router-Group的缺点是Routees的构建和管理复杂化了
	  
####log

akka可以配置logback，但是logback格式打出来的信息，缺了router和dispatcher的信息。读者可以通过屏蔽logback的配置，比较console和file打出来的日志区别。
    
    loggers = ["akka.event.slf4j.Slf4jLogger"]
    loglevel = "INFO"
    
####cluster

cluster同样包括client和server两部分。

1.server
server通过include "application"的方式和client贡献application.conf的配置，同时又实现了服务端的个性化配置。以下公用配置，remote.netty.tcp.port通常会被启动参数覆盖。集群的leader会从seed-nodes里面选出。
      
    remote { 
	    netty.tcp {
	      hostname = "127.0.0.1"
	      port = 2552
	    }
	}  	
	cluster {
    	seed-nodes = [
      		"akka.tcp://clustersystem@127.0.0.1:2551",
      		"akka.tcp://clustersystem@127.0.0.1:2552"] 
    }

启动命令如

     java  -jar -Dakka.remote.netty.tcp.port=2552 D:\github\akka\cluster-server\target\cluster-server-0.0.1-SNAPSHOT.jar 
     


2.client
配置，定义了一个router,注意这是一个group.group是需要定义routees.paths。pool不需要。启动2个服务端，客户端发送3个消息，会自动负载到2个节点上去。

     akka.actor.deployment {
		  /clusterFrontend/clusterBackendRouter = {
		    router =  round-robin-group  
		    nr-of-instances = 100
		    routees.paths = ["/user/clusterBackend"]
		    cluster {
		      enabled = on
		      use-role = backend
		      allow-local-routees = off
		    }
		  }
		}
     
代码，在Frondend中，

     	ActorRef backend = getContext()
					.actorOf(FromConfig
								.getInstance()
								.props() ,
		      "clusterBackendRouter");
     
     
####persistence

持久化的配置。下面的配置的是snapshot和持久化的数据。持久化是用leveldb。pom引入两个包就可以使用了。很方便。

     persistence {
	    #journal.plugin = "akka.persistence.journal.inmem"
	    snapshot-store.plugin = "akka.persistence.snapshot-store.local"
	    snapshot-store.local.dir = "/app/snapshots"
	    
	    journal.plugin = "akka.persistence.journal.leveldb"  
    	journal.leveldb.dir = "/app/journal"  
    	journal.leveldb.native = false  
	    
	    at-least-once-delivery {
	    	redeliver-interval = 20000
	    	redelivery-burst-limit = 100
	    	max-unconfirmed-messages = 500
	    } 
	  }
	  
at-least-once-delivery和persistence必须配合着使用。当持久化的时候（如调用persistAsync或者persist），会产生一个deliveryId。这个deliveryId作为一次应答的标志。client回复的时候，需要回复这个deliveryId。此时，confirmDelivery(deliveryId)代表服务端收到客户端的请求并且处理完毕。客户端就不会再往服务端发送信息。、还需要调用deleteMessages(deliveryId),删掉之后，恢复的时候，就不会再从本地读取信息发送。否则，每次重启，他都会将没有deleteMessages的信息发送一次。

这样就可以实现at-least-once了。

