package org.study.akka.actor;

import org.study.akka.msg.Message; 

import akka.actor.AbstractActor;  
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class LocalActor extends AbstractActor{
	  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  
	  ActorSelection remoteActor = getContext().actorSelection("akka.tcp://remote-server@127.0.0.1:2552/user/producer") ;
	  
	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	      .match(Message.class, s -> {	    	  
	    	  log.info("TestActor Received message: {}", s.getMessage());
	    	  remoteActor.tell(s, getSelf());
	      })
	      .matchAny(o -> log.info("received unknown message"))
	      .build();
	  } 
}
