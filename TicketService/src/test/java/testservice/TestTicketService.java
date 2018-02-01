package testservice;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ticketservice.common.SeatStatus;
import com.ticketservice.common.TicketServiceException;
import com.ticketservice.common.UIValidation;
import com.ticketservice.model.Customer;
import com.ticketservice.model.SeatHold;
import com.ticketservice.service.TicketServiceImpl;

public class TestTicketService {

TicketServiceImpl ticketServicetester = null;

@Rule
public ExpectedException thrown = ExpectedException.none();

@Before
public void loadData() throws Exception {
    ticketServicetester = new TicketServiceImpl(6, 4, 600);
}

@Test
public void testNumSeatsAvailable() {
    int numSeats = ticketServicetester.numSeatsAvailable();
    assertEquals(24, numSeats);
}

@Test
public void testFindAndHold() {
    SeatHold seatHold = ticketServicetester.findAndHoldSeats(4, "xyz@yahoo.com");

    assertNotNull(seatHold);
    assertEquals(seatHold.getNumberOfSeatsHold(), 4);
    assertNotNull(seatHold.getId());
}

@Test
public void testReserveSeats() {
    List<SeatHold> seatHolds = new ArrayList<SeatHold>();
    List<Customer> customers = new ArrayList<Customer>();
    // set data for SeatHold
    SeatHold seatHold = new SeatHold();
    seatHold.setId(44);
    seatHold.setStatus(SeatStatus.HOLD);
    seatHold.setHoldSince((int) ((System.currentTimeMillis() / 1000) % 3600));
    seatHolds.add(seatHold);

    // Set Customer
    Customer cust = new Customer();
    cust.setCustomerEmail("xyz@yahoo.com");
    cust.setSeatHolds(seatHolds);
    customers.add(cust);
    ticketServicetester.venue.setCustomers(customers);

    String reserveCode = ticketServicetester.reserveSeats(44, "xyz@yahoo.com");

    assertNotNull(reserveCode);
}

@Test(expected = TicketServiceException.class)
public void testReserveSeatsThrowExceptionForInValidCustomer()  {
    List<SeatHold> seatHolds = new ArrayList<SeatHold>();
    List<Customer> customers = new ArrayList<Customer>();
    thrown = ExpectedException.none();
    // Set Customer
    Customer cust = new Customer();
    cust.setCustomerEmail("xyz@yahoo.com");
    cust.setSeatHolds(seatHolds);
    customers.add(cust);
    ticketServicetester.venue.setCustomers(customers);
    ticketServicetester.reserveSeats(44, "xyz@yahoo");
    
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Customer does not Exist");
}

@Test(expected = TicketServiceException.class)
public void testReserveSeatsThrowExceptionForInValidSeatHold() {
    thrown = ExpectedException.none();
    List<SeatHold> seatHolds = new ArrayList<SeatHold>();
    List<Customer> customers = new ArrayList<Customer>();
    // set data for SeatHold
    SeatHold seatHold = new SeatHold();
    seatHold.setId(44);
    seatHold.setStatus(SeatStatus.HOLD);
    seatHold.setHoldSince((int) ((System.currentTimeMillis() / 1000) % 3600));
    seatHolds.add(seatHold);

    // Set Customer
    Customer cust = new Customer();
    cust.setCustomerEmail("xyz@yahoo.com");
    cust.setSeatHolds(seatHolds);
    customers.add(cust);
    ticketServicetester.venue.setCustomers(customers);
    ticketServicetester.reserveSeats(40, "xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Invalid SeatHold Id");

}

@Test(expected = TicketServiceException.class)
public void testReserveSeatsThrowExceptionForEmptySeatHold() {
    List<SeatHold> seatHolds = new ArrayList<SeatHold>();
    List<Customer> customers = new ArrayList<Customer>();
    // Set Customer
    Customer cust = new Customer();
    cust.setCustomerEmail("xyz@yahoo.com");
    cust.setSeatHolds(seatHolds);
    customers.add(cust);
    ticketServicetester.venue.setCustomers(customers);
    ticketServicetester.reserveSeats(40, "xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Customer does not have any seat holds");

}

@Test(expected = TicketServiceException.class)
public void testReserveSeatsForEmptyCustomersThrowException() {
    ticketServicetester.reserveSeats(40, "xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage(containsString("Invalid Customer"));

}

@Test(expected = TicketServiceException.class)
public void testReserveSeatsForBookedSeatsThrowException() {
    List<SeatHold> seatHolds = new ArrayList<SeatHold>();
    List<Customer> customers = new ArrayList<Customer>();
    // set data for SeatHold
    SeatHold seatHold = new SeatHold();
    seatHold.setId(44);
    seatHold.setStatus(SeatStatus.RESERVED);
    seatHold.setHoldSince((int) ((System.currentTimeMillis() / 1000) % 3600));
    seatHolds.add(seatHold);

    // Set Customer
    Customer cust = new Customer();
    cust.setCustomerEmail("xyz@yahoo.com");
    cust.setSeatHolds(seatHolds);
    customers.add(cust);
    ticketServicetester.venue.setCustomers(customers);
    ticketServicetester.reserveSeats(40, "xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage(containsString("SORRY !! Seats are already reserved by customer for Seat Hold Id :"));
}

@Test(expected = TicketServiceException.class)
public void testReserveSeatsForHoldTimeExpiredThrowException() {
    List<SeatHold> seatHolds = new ArrayList<SeatHold>();
    List<Customer> customers = new ArrayList<Customer>();
    // set data for SeatHold
    SeatHold seatHold = new SeatHold();
    seatHold.setId(44);
    seatHold.setStatus(SeatStatus.HOLD);
    seatHold.setHoldSince((int) ((System.currentTimeMillis() / 1000) % 3600) + 601);
    seatHolds.add(seatHold);

    // Set Customer
    Customer cust = new Customer();
    cust.setCustomerEmail("xyz@yahoo.com");
    cust.setSeatHolds(seatHolds);
    customers.add(cust);
    ticketServicetester.venue.setCustomers(customers);
    ticketServicetester.reserveSeats(40, "xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage(containsString("SORRY !! Ticket Hold time has expired"));
}

@Test(expected = TicketServiceException.class)
public void testFindAndHoldThrowExceptionForInValidId() {
    ticketServicetester.findAndHoldSeats(40, "xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Invalid SeatHold Id");

}

@Test(expected = TicketServiceException.class)
public void testValidateEmailAddressStartingWithNumber() {
    UIValidation.validateCustomerEmail("1xyz@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Invalid Customer email address");
}

@Test(expected = TicketServiceException.class)
public void testValidateEmailAddressContainsSpecialChar() {
    UIValidation.validateCustomerEmail("xyz*@yahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Invalid Customer email address");
}

@Test(expected = TicketServiceException.class)
public void testValidateEmailAddressInvalidSyntax() {
    UIValidation.validateCustomerEmail("xyz*@yahoo");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Invalid Customer email address");
}

@Test(expected = TicketServiceException.class)
public void testValidateEmailAddressInvalidSyntaxMissingAtChar() {
    UIValidation.validateCustomerEmail("xyzyahoo.com");
    thrown.expect(TicketServiceException.class);
    thrown.expectMessage("SORRY !! Invalid Customer email address");
}

}