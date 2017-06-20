package org.study.akka.actor;

import org.study.akka.msg.Message;
import org.study.akka.msg.Reply;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

public class Frondend extends AbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	ActorRef backend = getContext()
					.actorOf(FromConfig
								.getInstance()
								.props() ,
		      "clusterBackendRouter");
	

	@Override
	public Receive createReceive() {
	    return receiveBuilder()
	  	      .match(String.class, s -> {	    	  
	  	    	  log.info("Producer Received Main message: {}", s);
	  	    	  Message msg = new Message(); 
	  	    	  msg.setMessage(" Main Send "+s+" to Producer.");
	  	    	  backend.tell(msg, getSelf());
	  	      })
	  	      .match(Message.class, s -> {	    	  
	  	    	  log.info("Producer Received a Message From Remote : {}", s.getMessage());
	  	    	  log.info("backend.path() : {}", backend.path());
	    	  
	  	    	 ;
	  	    	  
	  	    	  backend.tell(s, getSelf());
	  	      })
	  	      .match(Reply.class, s->{
	  	    	  log.info("Producer Received a ReplyType : {}", s.getReplyType().getName());	      	
	  	      })
	  	      .matchAny(o -> log.info("received unknown message"))
	  	      .build();
	}

}
