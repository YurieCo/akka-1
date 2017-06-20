package org.study.akka.actor;

import org.study.akka.msg.Message;
import org.study.akka.msg.Reply;

import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;

public class PersistentActor extends AbstractPersistentActorWithAtLeastOnceDelivery  {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	 
	ActorSelection remoteActor = getContext().actorSelection("akka.tcp://persistence-server@127.0.0.1:2552/user/consumer") ;
	
	@Override
	public String persistenceId() {		
		return "my-persistent-actor-id";
	}
	
	private void handleMsg(String c) {
		persistAsync(String.format("evt-%s-reply", c), e -> {		    	
		    deliver(remoteActor, deliveryId->new Message(deliveryId, e));
		});
	}
	
	private void handleMsgForRecover(String c) { 		
		//已经持久化的，或者收不到回复的，不需要再persist
		log.info(" handleMsgForRecover : {} " ,c);
		    deliver(remoteActor, deliveryId->new Message(deliveryId, String.format("evt-%s-reply", c))); 
	}
	
	private void handleReply(Reply reply){
		log.info("PersistentActor replay msg : {} " ,reply.getMessage() );
		log.info("PersistentActor replay repalyType : {} " ,reply.getReplyType().getName() );
		confirmDelivery(reply.getDeliveryId());
		//confirmDelivery之后删掉
		deleteMessages(reply.getDeliveryId()) ;
	}
	
	@Override
	public Receive createReceive() {
	    return receiveBuilder().
	    	      match(String.class, this::handleMsg).
	    	      match(Reply.class, this::handleReply).build();
	}

	@Override
	public Receive createReceiveRecover() {
	    return receiveBuilder().
	    	      match(String.class, this::handleMsgForRecover).
	    	      match(Reply.class, this::handleReply).build();
	}

}
