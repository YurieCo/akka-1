package org.study.akka.msg;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 6424734840010583562L;
	
	public Message(){}
	
	public Message(long d,String m){
		this.deliveryId = d ;
		this.message  = m  ;
	}
	
	private  long deliveryId ; 
	
	private  String message ;
	

	public long getDeliveryId() {
		return deliveryId;
	}

	public  String getMessage() {
		return message;
	}

	public  void setMessage(String m) {
		message = m ;
	}
	
	

}
