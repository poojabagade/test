package com.ticketservice.common;

public class TicketServiceException extends RuntimeException{


	private static final long serialVersionUID = 1L;

    public TicketServiceException(){
        super();
    }
    
    public TicketServiceException(String msg){
        super(msg);
    }
}
