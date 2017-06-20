package org.study.akka.msg;

import java.io.Serializable;

public class Reply implements Serializable {
	
	private static final long serialVersionUID = -4326968809793525706L;
	
	private ReplyType replyType  ;
	
	private String message ;	

	private  long deliveryId ; 
	
	public Reply(){}
	
	public Reply(long d,String m,ReplyType r){
		this.replyType = r ;
		this.deliveryId = d ;
		this.message  = m  ;
	}
	
	
	public long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(long deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}



	public enum ReplyType {

	    SUCCESS(1, "成功" ),
	    FAIL(0, "失败" ) ;	

		private Integer code ;
	    private String name;
		
		ReplyType(Integer c,String n){
			this.code = c ;
			this.name = n ;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}

}
