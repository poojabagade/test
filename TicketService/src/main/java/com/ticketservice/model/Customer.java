package com.ticketservice.model;
import java.util.List;

public class Customer {
//Customer email address unique key
String customerEmail;

//SeatHold by the customer
List<SeatHold> seatHolds;

public Customer(){
}

public Customer(String customerEmail){
	this.customerEmail = customerEmail;

}
public String getCustomerEmail() {
	return customerEmail;
}
public void setCustomerEmail(String customerEmail) {
	this.customerEmail = customerEmail;
}

public List<SeatHold> getSeatHolds() {
	return seatHolds;
}
public void setSeatHolds(List<SeatHold> seatHolds) {
	this.seatHolds = seatHolds;
}

}
