package org.study;
 
 
import org.study.akka.actor.Consumer; 

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;

public class Main {

	public static void main(String[] args) throws InterruptedException {		

		Config regularConfig =
				  ConfigFactory.load();
		
	    ActorSystem system = ActorSystem.create("persistence-server",regularConfig);
	    
	     			;
	    //测试at-least-once-delivery
	    //Consumer 屏蔽响应的代码，PersistentActor收不到会不断发
	    //此时 ,打开屏蔽，重启服务，PersistentActor会打出收到回复的信息
	 	system.actorOf(FromConfig
	    						.getInstance()
	    						.props(Props
	    								.create(Consumer.class)
	    							),"consumer") ;
		 
	     
	    	
	    
	     
	}
}
