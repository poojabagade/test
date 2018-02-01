package com.ticketservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ticketservice.common.TicketServiceException;
import com.ticketservice.common.BusinessValidation;
import com.ticketservice.common.SeatStatus;
import com.ticketservice.common.Utility;
import com.ticketservice.model.Customer;
import com.ticketservice.model.Location;
import com.ticketservice.model.Seat;
import com.ticketservice.model.SeatHold;
import com.ticketservice.model.Venue;

/**
 * Implementation class for Ticket Service
 * @author pbagade
 *
 */
public class TicketServiceImpl implements TicketService {
public Venue venue;

final String NEW_CUSTOMER = "new";

final String EXISTING_CUSTOMER = "exist";

/**
 * Default constructor
 */
public TicketServiceImpl() {
}

/**
 * Parameterized constructor
 * 
 * @param totalSeats:
 *            total number ofSeats at the venue
 * @param holdTimeInSeconds
 *            Hold time for seat at the venue
 */
public TicketServiceImpl(int numRows, int numCols, int holdTimeInSeconds) {
    venue = new Venue(numRows, numCols, holdTimeInSeconds);
}

/**
 * The number of seats in the venue that are neither held nor reserved
 *
 * @return the number of tickets available in the venue
 */
public int numSeatsAvailable() {

    // Release the hold on the seats if the hold timer has expired
    releaseHoldExpiredSeats();

    return venue.getSeatsAvailable();
}

/**
 * Find and hold the best available seats for a customer
 *
 * @param numSeats
 *            the number of seats to find and hold
 * @param customerEmail
 *            unique identifier for the customer
 * @return a SeatHold object identifying the specific seats and related
 *         information
 */
public SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws TicketServiceException {
    SeatHold seatHold = null;
    try {
        // Release the hold on the seats if the hold timer has expired
        releaseHoldExpiredSeats();

        // Validate if the number of seats by user is less than or equals to
        // number of seats available in the venue
        BusinessValidation.validateNumSeats(this.venue, numSeats);

        // Get customer from venue, if the customer does not exist a new
        // customer is added
        Customer customer = getCustomer(customerEmail, NEW_CUSTOMER);
        
        // Create the Seat Hold Object
        seatHold = new SeatHold();
        seatHold.setId((int) (Math.random() * 100));
        seatHold.setHoldSince((int) ((System.currentTimeMillis() / 1000) % 3600));
        seatHold.setNumberOfSeatsHold(numSeats);
        seatHold.setStatus(SeatStatus.HOLD);

        // Get SeatHolds from customer
        List<SeatHold> custSeatHolds = customer.getSeatHolds();

        if (custSeatHolds == null) {
            custSeatHolds = new ArrayList<SeatHold>();
        }

        // Add new SeatHold in customers SeatHolds
        custSeatHolds.add(seatHold);

        customer.setSeatHolds(custSeatHolds);

        // Update seats at the Venue with SeatHold object
        updateVenue(customer, numSeats, seatHold);
        int id = seatHold.getId();
        List<Seat> seats = this.venue.getSeats().stream().filter(s -> s.getSeatHold().getId() == id)
                .collect(Collectors.toList());

        // Add seats to SeatHold Object
        seatHold.setSeats(seats);
        
    } catch (TicketServiceException e) {
        throw e;
    }

    return seatHold;
}

/**
 * Commit seats held for a specific customer
 *
 * @param seatHoldId
 *            the seat hold identifier
 * @param customerEmail
 *            the email address of the customer to which the seat hold is
 *            assigned
 * @return a reservation confirmation code
 */
public String reserveSeats(int seatHoldId, String customerEmail) throws TicketServiceException {

    String reservationCode = null;

    try {
        // Get customer from venue, if the customer does not exist null is
        // returend
        Customer customer = getCustomer(customerEmail, EXISTING_CUSTOMER);

        // customer should not be null else error should be displayed
        BusinessValidation.customerExist(customer);

        // Get SeatHolds from customer
        List<SeatHold> seatHolds = customer.getSeatHolds();

        // Validate that customer has SeatHold
        BusinessValidation.seatHoldsExist(seatHolds);

        // Match the SeatHold Id with the customer SeatHolds
        SeatHold seatHold = null;
        for (int i = 0; i < seatHolds.size() && seatHold == null; i++) {
            if (seatHolds.get(i).getId() == seatHoldId) {
                seatHold = seatHolds.get(i);
            }
        }

        // Seat hold object for given id should exist,else error should
        // be displayed
        BusinessValidation.validateSeatHold(this.venue.getHoldTimeInSeconds(), seatHold);

        try {
            // Check if Hold Time has not Expired on the Seat
            BusinessValidation.checkHoldTime(this.venue.getHoldTimeInSeconds(), seatHold);

            // Reserve the SeatHold
            reservationCode = customerEmail.substring(0,2) + seatHoldId;
            seatHold.setReservationCode(reservationCode);
            seatHold.setStatus(SeatStatus.RESERVED);

        } catch (TicketServiceException e) {
            // Check if SeatHold has Status Hold
            if (seatHold.getStatus() == SeatStatus.HOLD) {
                // Release the Seats since Hold Time has Expired
                int seatAvailabe = this.venue.getSeatsAvailable() + seatHold.getNumberOfSeatsHold();
                this.venue.setSeatsAvailable(seatAvailabe);
                seatHold.setStatus(SeatStatus.AVAILABLE);
            }
            throw e;
        }

    } catch (TicketServiceException e) {
        throw e;
    }
    return reservationCode;
}

/**
 * Get Customer from the venue
 * 
 * @param customerEmail
 * @param action
 * @return customer if customer exist ,or return null if customer does not exist
 */
private Customer getCustomer(String customerEmail, String action) {
    List<Customer> customers = this.venue.getCustomers();
    Customer customer = null;

    // if customer is new customer and venue has no customer added
    // create customer list in venue and add new customer
    if (NEW_CUSTOMER.equalsIgnoreCase(action) && (customers == null || !Utility.listNotEmpty(customers))) {
        customers = new ArrayList<Customer>();
        customer = new Customer(customerEmail);
        customers.add(customer);
        this.venue.setCustomers(customers);
    } else {

        // if venue has customers, get customer from existing customer
        if (Utility.listNotEmpty(customers)) {
            for (int i = 0; customer == null && i < customers.size(); i++) {
                Customer customerInVenue = customers.get(i);
                if (customerInVenue.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
                    customer = customerInVenue;
                }
            }

            // if venue has customers, and customer does not exist , add new
            // customer
            if (customer == null && NEW_CUSTOMER.equalsIgnoreCase(action)) {
                customer = new Customer(customerEmail);
                customers.add(customer);
                this.venue.setCustomers(customers);
            }
        }
    }

    return customer;
}

/**
 * Update the Venue Oject with customer info and Seat info
 * 
 * @param customer
 * @param numSeats
 * @param seatHold
 * @throws TicketServiceException
 */
private void updateVenue(Customer customer, int numSeats, SeatHold seatHold) throws TicketServiceException {
    try {
     
        // change number of seats available
        int seatsAvailable = this.venue.getSeatsAvailable() - numSeats;
        this.venue.setSeatsAvailable(seatsAvailable);

        // Update or Seats Data at the Venue
        updateVenueSeats(customer, numSeats, seatHold);
    } catch (TicketServiceException e) {
        throw e;
    }
}

/**
 * Update or add seats to the venue based on the SeatHold status, For SeatHold
 * Status to Hold -change available seats on hold or if seats are not yet added
 * in venue add new Seats and put new seats on Hold. For SeatHold Status to
 * Reserved -Change seatHold status to Reserved.
 * 
 * @param customer
 * @param numSeats
 * @param seatHold
 */
private void updateVenueSeats(Customer customer, int numSeats, SeatHold seatHold) {
    try {
        List<Seat> seats = this.venue.getSeats();
        // update venue seats
        if (Utility.listNotEmpty(seats)) {
            if (Utility.isNotNull(seatHold) && seatHold.getStatus() == SeatStatus.HOLD) {
                List<Seat> availSeats = seats.stream().filter(s -> s.getSeatHold() == null)
                        .collect(Collectors.toList());

                if (BusinessValidation.seatsAreAvailable(availSeats)) {
                    int count = 0;
                    // fill existing Seats
                    for (int i = 0; i < seats.size() || count < numSeats; i++) {
                        if (!Utility.isNotNull(seats.get(i).getSeatHold())) {
                            seats.get(i).setSeatHold(seatHold);
                            count++;
                        }
                    }
                    // add new seats since venue is not yet full
                    if (count < numSeats) {
                        addNewSeats(seatHold, numSeats - count);
                    }
                } else {
                    addNewSeats(seatHold, numSeats);
                }

            } else if (Utility.isNotNull(seatHold) && seatHold.getStatus() == SeatStatus.RESERVED) {
                // get all holds seats at the venue
                List<Seat> holdSeats = new ArrayList<Seat>();

                // Get Seats on Hold at the venue
                for (Seat seat : seats) {
                    if (Utility.isNotNull(seat.getSeatHold()) && seat.getSeatHold().getStatus() == SeatStatus.HOLD) {
                        holdSeats.add(seat);
                    }
                }

                // check if hold seat exist
                BusinessValidation.seatsHoldExistforCustomer(holdSeats);
                int count = 0;
                for (int i = 0; i < seats.size() && count < numSeats; i++) {

                    // if customer seat hold object match the seat then set that
                    // seats with reserved seathold object
                    if (checkIfSeatsOnHold(seats.get(i), customer)) {
                        seats.get(i).setSeatHold(seatHold);
                        count++;
                    }
                }
            } else if (Utility.isNotNull(seatHold) && seatHold.getStatus() == SeatStatus.AVAILABLE) {
                // if seats are available the seatHold object should be deleted
                // from the seat
                for (int i = 0; i < seats.size(); i++) {
                    if (seats.get(i).getSeatHold().getId() == seatHold.getId()) {
                        seats.get(i).setSeatHold(null);
                    }
                }
            }
        } else { // if venue has no seats added
                 // add new seats since venue is not yet full
            if (seatHold.getStatus() == SeatStatus.HOLD) {
                addNewSeats(seatHold, numSeats);
            }
        }
    } catch (TicketServiceException e) {
        throw e;
    }
}

/**
 * Dynamically adds new Seats in the Venue
 * 
 * @param seatHold
 * @param numSeats
 */
private void addNewSeats(SeatHold seatHold, int numSeats) {
    List<Seat> seats = this.venue.getSeats();
    Seat seat = null;
    for (int i = 0; i < numSeats; i++) {
        seat = new Seat();
        seat.setSeatHold(seatHold);
        addLocation(seats, seat);
        seats.add(seat);
    }
}

/**
 * Dynamically adds location to the seat in the venue
 * 
 * @param seats
 * @param seat
 */
private void addLocation(List<Seat> seats, Seat seat) {
    int index = seats.size();
    int colNum = index / this.venue.getNumCols();
    int rowNum = index % this.venue.getNumRows();
    Location location = new Location();
    location.setColNumber(colNum);
    location.setRowNumber(rowNum);
    seat.setLocation(location);
}

/**
 * Check if the SeatHold object on the seat match the seat Hold object of
 * Customer
 * 
 * @param seat
 * @param cust
 * @return
 */
private boolean checkIfSeatsOnHold(Seat seat, Customer cust) {
    // Get SeatHold object on the seat
    SeatHold seatHold = seat.getSeatHold();
    SeatHold customerSeatHold = null;

    // Get Customers SeatHold Objects
    List<SeatHold> custSeatHolds = cust.getSeatHolds();

    // get customer seatHold object based on seatHold id match cust seat hold
    for (SeatHold custSeatHold : custSeatHolds) {
        if (custSeatHold.getId() == seatHold.getId()) {
            customerSeatHold = custSeatHold;
        }
    }
    if (seatHold.getStatus() == SeatStatus.HOLD && Utility.isNotNull(customerSeatHold)) {
        return true;
    }
    return false;
}

/**
 * Release the hold on the seats if the hold timer has expired
 */
private void releaseHoldExpiredSeats() {
    // Get All Customers from the venue
    List<Customer> customers = this.venue.getCustomers();
  
    List<SeatHold> seatHolds = null;
    int seatsAvailable = 0;
    List<Seat> seats = null;

    if (Utility.listNotEmpty(customers)) {
        
        for (Customer customer : customers) {

            // Get SeatHolds from Customer
            seatHolds = customer.getSeatHolds();
            for (SeatHold seatHold : seatHolds) {

                // Validate if the SeatHold has status Hold and hold timer has
                // expired
                if (seatHold.getStatus() == SeatStatus.HOLD
                        && BusinessValidation.checkHoldTimeExpired(this.venue.getHoldTimeInSeconds(), seatHold)) {

                    // Set the SeatHold status to Available
                    seatHold.setStatus(SeatStatus.AVAILABLE);
                    seatsAvailable = this.venue.getSeatsAvailable() + seatHold.getNumberOfSeatsHold();
                    this.venue.setSeatsAvailable(seatsAvailable);

                    // Remove the SeatHold object from seats
                    seats = this.venue.getSeats();
                    if (Utility.listNotEmpty(seats)) {
                        for (Seat seat : seats) {
                            if (Utility.isNotNull(seat.getSeatHold())
                                    && seat.getSeatHold().getId() == seatHold.getId()) {
                                seat.setSeatHold(null);
                            } // end of if seatHold object matches the id
                        } // end of for loop for seats
                    } // end of if seats list not empty
                }
            } // end of for loop seatHolds
        } // end of for loop customers
    } // end of if customer not empty
    
}

}
