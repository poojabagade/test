package com.ticketservice.model;

public class Seat {
//SeatHold on the seat 
SeatHold seatHold;
//Seat Location
Location location;
public SeatHold getSeatHold() {
	return seatHold;
}
public void setSeatHold(SeatHold seatHold) {
	this.seatHold = seatHold;
}
public Location getLocation() {
	return location;
}
public void setLocation(Location location) {
	this.location = location;
}
}
