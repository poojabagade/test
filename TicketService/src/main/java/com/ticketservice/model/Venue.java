package com.ticketservice.model;

import java.util.ArrayList;
import java.util.List;

public class Venue {
// total number of seats in the venue
int totalSeats;

// total number of seats in rows in the venue
int numRows;

// total number of seats in columns in the venue
int numCols;

// Customers in the venue
List<Customer> customers;

// This time will considered on the ticket, when the user puts the seat on
// hold
int holdTimeInSeconds;
	
// Number of seat in available status in the venue.
int seatsAvailable;
	
// Seats in the venue
List<Seat> seats;

public Venue() {
}

public Venue(int numRows, int numCols, int holdTimeInSeconds) {
		this.totalSeats = numRows * numCols;
		this.holdTimeInSeconds = holdTimeInSeconds;
		this.seatsAvailable = totalSeats;
		this.numRows = numRows;
		this.numCols = numCols;
		this.seats = new ArrayList<Seat>();
}

public int getTotalSeats() {
	return totalSeats;
}

public void setTotalSeats(int totalSeats) {
	this.totalSeats = totalSeats;
}

public int getNumRows() {
	return numRows;
}

public void setNumRows(int numRows) {
	this.numRows = numRows;
}

public int getNumCols() {
	return numCols;
}

public void setNumCols(int numCols) {
	this.numCols = numCols;
}

public int getHoldTimeInSeconds() {
	return holdTimeInSeconds;
}

public void setHoldTimeInSeconds(int holdTimeInSeconds) {
	this.holdTimeInSeconds = holdTimeInSeconds;
}

public List<Customer> getCustomers() {
	return customers;
}

public void setCustomers(List<Customer> customers) {
	this.customers = customers;
}

public int getSeatsAvailable() {
	return seatsAvailable;
}

public void setSeatsAvailable(int seatsAvailable) {
	this.seatsAvailable = seatsAvailable;
}

public List<Seat> getSeats() {
	return seats;
}

public void setSeats(List<Seat> seats) {
	this.seats = seats;
}
}
