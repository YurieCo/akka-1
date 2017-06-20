package org.study;
 
import java.util.Arrays;
import java.util.stream.Collectors;

import org.study.akka.actor.Frondend;
import org.study.akka.msg.Message;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;

public class Main {

	public static void main(String[] args) throws InterruptedException {		

	    final Config config = ConfigFactory.parseString(
	            "akka.cluster.roles = [frontend]").withFallback(
	            ConfigFactory.load("client"));

	        final ActorSystem system = ActorSystem.create("clustersystem", config);
	        system.log().info(
	            "Factorials will start when 2 backend members in the cluster.");
	        //#registerOnUp
	        Cluster.get(system).registerOnMemberUp(()->{
	        	ActorRef ref = system.actorOf(Props.create(Frondend.class),  "clusterFrontend");
	        	
	        	Arrays
	        		.asList(new String[]{"1","2","3"})
	        		.stream()
	        		.parallel()
	        		.map(a->{
			      	  	Message msg = new Message(); 
			      	  	msg.setMessage("This is msg From Remote . Hello ! Id = " + a);		      	  	
			        	ref.tell(msg, ActorRef.noSender()); 
			        	return a ;
		        	})
					.collect(Collectors.toList()) ;	
	        	

	        });
	    
	     
	}
}
