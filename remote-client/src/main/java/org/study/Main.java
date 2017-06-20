package org.study;
 
import org.study.akka.actor.LocalActor;
import org.study.akka.msg.Message;
 
import akka.actor.ActorRef;
import akka.actor.ActorSystem; 
import akka.actor.Props; 

public class Main {

	public static void main(String[] args)  throws InterruptedException {
		ActorSystem system = ActorSystem.create("remoteTest");	
		ActorRef ref = system.actorOf(Props.create(LocalActor.class));
  	  	Message msg = new Message(); 
  	  	msg.setMessage("This is msg From Remote .Hello !");
  	  	ref.tell(msg, ActorRef.noSender());	    	     
	}
}
