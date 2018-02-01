package com.ticketservice.model;
import java.util.List;

import com.ticketservice.common.SeatStatus;

public class SeatHold  {
	
   //Unique SeatHold Id
	int id;
	
	// time since the seats are on hold in sec
	int holdSince;
	
	// Number of Seats on Hold in this seat hold object
	int numberOfSeatsHold;
	
	// Reservation code for seat 
	String reservationCode;

	//SeatHold Status AVAILABLE,HOLD,RESERVED
	SeatStatus status;
	
	//Seats in the SeatHold Object
	List <Seat> seats;
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getHoldSince() {
		return holdSince;
	}
	
	public void setHoldSince(int i) {
		this.holdSince = i;
	}
	
	public int getNumberOfSeatsHold() {
		return numberOfSeatsHold;
	}
	
	public void setNumberOfSeatsHold(int numberOfSeatsHold) {
		this.numberOfSeatsHold = numberOfSeatsHold;
	}
	
	public String getReservationCode() {
		return reservationCode;
	}
	
	public void setReservationCode(String reservationCode) {
		this.reservationCode = reservationCode;
	}
	
	public SeatStatus getStatus() {
		return status;
	}
	
	public void setStatus(SeatStatus status) {
		this.status = status;
	}
	
	public List<Seat> getSeats() {
		return seats;
	}
	
	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}
}
