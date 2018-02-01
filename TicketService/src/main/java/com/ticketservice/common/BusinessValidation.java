package com.ticketservice.common;

import java.util.List;

import com.ticketservice.model.Customer;
import com.ticketservice.model.SeatHold;
import com.ticketservice.model.Venue;

/**
 * Validates the Business Rules
 *
 */
public class BusinessValidation {

	/**
	 * Validates the number of Seats input by user against the total seats
	 * available at the venue
	 * 
	 * @param venue
	 * @param numSeats
	 * @throws TicketServiceException
	 */
	public static void validateNumSeats(Venue venue, int numSeats) throws TicketServiceException {
		if (venue.getSeatsAvailable() < numSeats) {
			throw new TicketServiceException(MessageProperties.msgProp.getProperty("INVALID_NUM_SEATS"));
		}

	}

	/**
	 * Checks if the Customer exist in venue
	 * 
	 * @param customer
	 * @throws TicketServiceException
	 */
	public static void customerExist(Customer customer) throws TicketServiceException {
		if (!Utility.isNotNull(customer)) {
			throw new TicketServiceException(MessageProperties.msgProp.getProperty("INVALID_CUST"));
		}
	}

	/**
	 * Validate that SeatHold has status HOLD
	 * 
	 * @param seatHold
	 * @throws TicketServiceException
	 */
	public static void validateSeatHold(int venueHoldTimeInSeconds,SeatHold seatHold) throws TicketServiceException {
		if (Utility.isNotNull(seatHold)) {
			if (seatHold.getStatus() == SeatStatus.RESERVED) {
				throw new TicketServiceException(
						MessageProperties.msgProp.getProperty("SEAT_RESERVED") + seatHold.getId());
			}else if(seatHold.getStatus() == SeatStatus.AVAILABLE && checkHoldTimeExpired(venueHoldTimeInSeconds,seatHold)){
				throw new TicketServiceException(MessageProperties.msgProp.getProperty("HOLD_TIME_EXP"));
			}
		} else {
			throw new TicketServiceException(MessageProperties.msgProp.getProperty("INVALID_SEATHOLD_ID"));

		}
	}

	/**
	 * Validate that seatHold exist for customer and throw exception if seat hold does not exist
	 * @param seatHolds
	 *            : List of SeatHolds
	 * @throws TicketServiceException
	 */
	public static void seatHoldsExist(List<?> seatHolds) throws TicketServiceException {
		if (!Utility.isNotNull(seatHolds) || !Utility.listNotEmpty(seatHolds)) {
			throw new TicketServiceException(MessageProperties.msgProp.getProperty("CUST_NO_SEATHOLD"));
		}

	}

	/**
	 * Check if the Seat hold has expired and throw exception if hold time has expired
	 * 
	 * @param venueHoldTimeInSeconds
	 * @param seatHold
	 * @throws TicketServiceException
	 */
	public static void checkHoldTime(int venueHoldTimeInSeconds, SeatHold seatHold) throws TicketServiceException {

		if (checkHoldTimeExpired(venueHoldTimeInSeconds, seatHold)) {
			throw new TicketServiceException(MessageProperties.msgProp.getProperty("HOLD_TIME_EXP"));
		}

	}

	/**
	 * Check if the Seat hold has expired 
	 * @param venueHoldTimeInSeconds
	 * @param seatHold
	 * @return boolean true = if hold time has expired or false = if hold time has not expired
	 */
	public static boolean checkHoldTimeExpired(int venueHoldTimeInSeconds, SeatHold seatHold) {

		int holdSeconds = seatHold.getHoldSince();
		int nowSeconds = (int) ((System.currentTimeMillis() / 1000) % 3600);

		int diffTime = nowSeconds - holdSeconds;
		if (diffTime > venueHoldTimeInSeconds) {
			return true;
		}
		return false;
	}

	/**
	 * Check if seats are available
	 * @param availSeats
	 * @return boolean true:if seats are available or false : if seats are not available
	 */
	public static boolean seatsAreAvailable(List<?> availSeats) {
		if (Utility.listNotEmpty(availSeats)) {
			return true;
		}
		return false;
	}
	/**
	 * Check if seats on hold exist
	 * @param availSeats
	 * @return boolean true:if seats are on hold or false : if seats are not on hold
	 */
	public static void seatsHoldExistforCustomer(List<?> availSeats) {
		if (!Utility.listNotEmpty(availSeats)) {
			throw new TicketServiceException(
					MessageProperties.msgProp.getProperty("SEATS_HOLD_NOT_EXIST_FOR_CUSTOMER"));
		}
	}

}
