package org.study.akka.actor;

import org.study.akka.msg.Message;
import org.study.akka.msg.Reply; 

import akka.actor.AbstractActor; 
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

public class Producer extends AbstractActor {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	 
	
	private ActorRef consumer ;
	
	public Producer() {
	}
	
	Producer(ActorRef c){
		this.consumer = c ;
	}
	
	@Override
	public void preStart() throws Exception {
		if (consumer == null){
			consumer = 
					getContext()
			    			.actorOf(
			    				FromConfig.getInstance().props(Props.create(Consumer.class)),"consumer")		 ;
		}
	};

	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	      .match(String.class, s -> {	    	  
	    	  log.info("Producer Received Main message: {}", s);
	    	  Message msg = new Message(); 
	    	  msg.setMessage(" Main Send "+s+" to Producer.");
	    	  consumer.tell(msg, getSelf());
	      })
	      .match(Message.class, s -> {	    	  
	    	  log.info("Producer Received a Message From Remote : {}", s.getMessage());
	    	  consumer.tell(s, getSelf());
	      })
	      .match(Reply.class, s->{
	    	  log.info("Producer Received a ReplyType : {}", s.getReplyType().getName());	      	
	      })
	      .matchAny(o -> log.info("received unknown message"))
	      .build();
	  }

	  
}
