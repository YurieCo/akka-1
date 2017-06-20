package org.study;

import org.study.akka.actor.Backend;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

	public static void main(String[] args) throws InterruptedException {		

//	    final String port = args.length > 0 ? args[0] : "0";
	    final Config config = ConfigFactory.parseString("akka.cluster.roles = [backend]").
	    		withFallback(ConfigFactory.load("server"));
	    
	    ActorSystem system = ActorSystem.create("clustersystem", config);
	  
	    system.actorOf(Props.create(Backend.class), "clusterBackend");
	    
	     
	}
}
