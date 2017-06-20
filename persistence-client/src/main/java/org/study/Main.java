package org.study;
 
 
import java.util.Arrays;
 
import org.study.akka.actor.PersistentActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;

public class Main {

	public static void main(String[] args) throws InterruptedException {		

		Config regularConfig =
				  ConfigFactory.load();
		
	    ActorSystem system = ActorSystem.create("persistence-client",regularConfig);
	    			;
	    //测试过程
	    //启动服务端但是屏蔽replay，启动客户端
	   //此时服务端会将信息持久化。然后服务端打开屏蔽重启，重启客户端
	    //他是只要恢复了，会将所有队列的消息都发一次
	    ActorRef persistentactor = system.actorOf(Props
	    								.create(PersistentActor.class)
	    							,"persistentactor") ;
		
//	    Arrays
//	    	.asList(new String[]{"hello 0.","hello 1.","hello 2."})
//	    	.stream()
//	    	.parallel()
//	    	.forEach(s->persistentactor.tell(s,ActorRef.noSender()));
	     
	    	
	    
	     
	}
}
