package org.study;
 
 
import org.study.akka.actor.Producer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;

public class Main {

	public static void main(String[] args) throws InterruptedException {		

		Config regularConfig =
				  ConfigFactory.load();
		
	    ActorSystem system = ActorSystem.create("remote-server",regularConfig);
	    
	    //consumer和producer公用一个线程池forkjoin-dispatcher
	    //该配置见application.conf
	    //producer建立了一个router，通过pool的方式生成多个routee
	    //策略是round-robin-pool，最小三个.所以观察日志会发现有$b,$a,$c等
	    //router读取配置，需要FromConfig.getInstance
	    
//	    ActorRef consumer = 
//	    		system
//	    			.actorOf(Props
//    								.create(Consumer.class)
//    								.withDispatcher("forkjoin-dispatcher")    							//)
//    						,"consumer")  			;
	    	 
	 	system.actorOf(Props
	    								.create(Producer.class)
	    								.withDispatcher("my-dispatcher")
	    							,"producer") ;
		
//	    Arrays
//	    	.asList(new String[]{"hello 0.","hello 1.","hello 2."})
//	    	.stream()
//	    	.parallel()
//	    	.forEach(s->producer.tell(s,ActorRef.noSender()));
	     
	    	
	    
	     
	}
}
