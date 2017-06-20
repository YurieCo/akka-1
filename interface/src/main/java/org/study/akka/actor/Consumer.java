package org.study.akka.actor;

import org.study.akka.msg.Message;
import org.study.akka.msg.Reply;
import org.study.akka.msg.Reply.ReplyType;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Consumer extends AbstractActor {	

	  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  
	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	      .match(Message.class, s -> {	    	  
	    	  log.info("Consumer Received message: {}", s.getMessage());
	    	  log.info("Consumer Received deliveryId: {}", s.getDeliveryId());
	    	  Reply reply = new Reply() ;
	    	  reply.setMessage(" this is Consumer reply for receive msg : "+s.getMessage());
	    	  reply.setReplyType(ReplyType.SUCCESS);
	    	  reply.setDeliveryId(s.getDeliveryId());
	    	  getSender().tell(reply, getSelf());
	      })
	      .matchAny(o -> log.info("received unknown message"))
	      .build();
	  } 
	  

}
