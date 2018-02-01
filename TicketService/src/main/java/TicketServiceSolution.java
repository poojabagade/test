import java.util.List;
import java.util.Scanner;

import com.ticketservice.common.TicketServiceException;
import com.ticketservice.common.MessageProperties;
import com.ticketservice.common.UIValidation;
import com.ticketservice.common.Utility;
import com.ticketservice.model.Seat;
import com.ticketservice.model.SeatHold;
import com.ticketservice.service.TicketService;
import com.ticketservice.service.TicketServiceImpl;

/**
 *This the main class and also displays the menu to get user options
 * @author pbagade
 *
 */
public class TicketServiceSolution {

public static void main(String args[]) {
    TicketService ticketService = null;
    int option = 0;
    Scanner sc = new Scanner(System.in);

    // check if user entered values for total number of seats and Hold time
    // for seat and create TicketService with the user data
    // else create TicketService with totalSeats= 24 and Hold time for seat=600

    if (args.length >= 3 && UIValidation.checkIfNumbers(args[0], args[1], args[2])) {
        ticketService = new TicketServiceImpl(new Integer(args[0]), new Integer(args[1]), new Integer(args[2]));
    } else {
        ticketService = new TicketServiceImpl(6, 4, 600);
    }

    do {
        printMenu();
        try{
        option = sc.nextInt();
        switch (option) {
        case 1:
            numSeatsAvailable(ticketService);
            break;
        case 2:
            findAndHoldSeats(sc, ticketService);
            break;
        case 3:
            reserveSeats(sc, ticketService);
            break;
        case 4:
            displayExitMessage();
            break;
        default:
            System.out.println(MessageProperties.msgProp.getProperty("INPUT_DOES_NOT_EXIST"));
        }}catch(Exception e){
            System.out.println(MessageProperties.msgProp.getProperty("INPUT_DOES_NOT_EXIST")); 
            option=0;
            if(sc.hasNextLine()){
                sc.nextLine();
            }
        }
    } while (option != 4);
}

/**
 * Displays the Menu for user to choose options live Get number of Available
 * Seats,Find and Hold Seats and Reserve Seats
 */
private static void printMenu() {
    System.out.println(MessageProperties.msgProp.getProperty("MENU_HEADER_LINE"));
    System.out.format(MessageProperties.msgProp.getProperty("MENU_WELCOME"));
    System.out.println(MessageProperties.msgProp.getProperty("MENU_HEADER_LINE"));
    System.out.format(MessageProperties.msgProp.getProperty("MENU_OPTION_1"));
    System.out.format(MessageProperties.msgProp.getProperty("MENU_OPTION_2"));
    System.out.format(MessageProperties.msgProp.getProperty("MENU_OPTION_3"));
    System.out.format(MessageProperties.msgProp.getProperty("MENU_OPTION_4"));
    System.out.println(MessageProperties.msgProp.getProperty("MENU_HEADER_LINE"));
    System.out.format(MessageProperties.msgProp.getProperty("MENU_TO_PROCEED"));
}

private static void displayExitMessage() {
    System.out.println(MessageProperties.msgProp.getProperty("EXIT_MESSAGE"));
}

/**
 * This function gets the user input like number of seats to hold, customer
 * email address , calls the API findAndHoldSeats and display SeatHold data
 * 
 * @param sc
 *            Scanner Object
 * @param ticketService
 *            TicketService Implementation class
 */
private static void findAndHoldSeats(Scanner sc, TicketService ticketService) {
    try {
        // Get User Inputs
        System.out.print(MessageProperties.msgProp.getProperty("ENTER_NUMBER_SEAT"));
        int numSeats = getIntegerInput(sc);

        // Validate user Input
        UIValidation.isPositiveNumber(numSeats);

        // Get Customer Email Address
        String customerEmail = getCustomerEmail(sc);

        if (Utility.isNotNull(customerEmail)) {
            // Call the service method findAndHoldSeats
            SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);

            // Display Results
            displaySeatHold(seatHold);
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
}

/**
 * This function gets the user input,Calls the API reserve Seats method and
 * displays the reservation code
 * 
 * @param sc
 *            Scanner Object
 * @param ticketService
 *            TicketService Implementation class
 */
private static void reserveSeats(Scanner sc, TicketService ticketService) {
    try {
        // Get User Inputs
        System.out.print(MessageProperties.msgProp.getProperty("SEAT_HOLD_IDT"));

        int seatHoldId = getIntegerInput(sc);

        // Validate user Input
        UIValidation.isPositiveNumber(seatHoldId);

        // Get Customer Email Address
        String customerEmail = getCustomerEmail(sc).trim();

        UIValidation.validateCustomerEmail(customerEmail);

        // Call the service method reserveSeats
        String reservationCode = ticketService.reserveSeats(seatHoldId, customerEmail);

        // display results
        System.out.println(MessageProperties.msgProp.getProperty("RESERVE_CODE") + reservationCode);

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }

}

private static void numSeatsAvailable(TicketService ticketService) {
    System.out.println(MessageProperties.msgProp.getProperty("NUM_SEAT_AVAIL") + ticketService.numSeatsAvailable());
}

/**
 * gets customer email address from users and validates the user entered
 * customer email address
 * 
 * @param sc
 *            Scanner object
 * @return customer email address
 */
private static String getCustomerEmail(Scanner sc) {

    String customerEmail = "", userInput = "";
    do {
        System.out.print(MessageProperties.msgProp.getProperty("CUST_EMAIL_ADDR"));
        while (customerEmail.equals("")) {
            customerEmail = sc.nextLine();
        }
        // Validate if email address syntax is correct , email address does
        // not start with number, email address can have only few special
        // character like underscore and dot
        try {
            UIValidation.validateCustomerEmail(customerEmail);
            userInput = "N";
        } catch (TicketServiceException e) {
            System.out.println(e.getMessage());
            userInput = "";
            customerEmail = "";
            System.out.print(MessageProperties.msgProp.getProperty("VALID_CUST_EMAIL_ADDR"));
            while (userInput.equals("")) {
                userInput = sc.nextLine();
            }
        }

    } while (!userInput.equalsIgnoreCase("N"));
    return customerEmail;
}

/**
 * gets number as user input
 * 
 * @param sc
 *            Scanner object
 * @return user entered number
 */
private static int getIntegerInput(Scanner sc) {
    int value = -1;
    String userInput = "";
    do {
        try {
            value = sc.nextInt();
            // Set userInput to N , as user has entered valid numbers
            userInput = "N";
            // check for positive numbers
            UIValidation.isPositiveNumber(value);
        } catch (Exception e) {
            // if user enters any key except numbers , display error message
            // and check if user wants to continue entering valid number
            System.out.println("**"+MessageProperties.msgProp.getProperty("INVALID_NUMBER"));
            userInput = continueInput(sc);
        }

    } while (!userInput.equalsIgnoreCase("N"));
    return value;
}

/**
 * If users enters incorrect integer value for Seat Hold Id or Number of Seat
 * Hold ,this function is called to get correct number
 * 
 * @param sc
 *            - Scanner object
 * @return user entered value
 */
private static String continueInput(Scanner sc) {
    String userInput = "";

    // clear scanner
    if (sc.hasNextLine()) {
        sc.nextLine();
    }
    
    //Display Message Do you want to continue and enter valid number?
    System.out.print(MessageProperties.msgProp.getProperty("ENT_VALID_NUM"));
    while (userInput.equals("")) {
        userInput = sc.nextLine();
    }
   
    //if user enters y .Prompt user to enter valid number
    if (!userInput.equalsIgnoreCase("N")) {
        System.out.println(MessageProperties.msgProp.getProperty("VALID_INPUT"));
    }
    return userInput;
}

/**
 * This function displays the data in Seat Hold object like Seat Hold Id,Number
 * of Seat hold,Location of the seat,Status of the Seat
 * 
 * @param seatHold
 *            :SeatHold Object
 */
private static void displaySeatHold(SeatHold seatHold) {
    List<Seat> seats = seatHold.getSeats();

    int seatNum = 1;
    for (Seat seat : seats) {
        System.out.println(MessageProperties.msgProp.getProperty("SEAT") + seatNum + " "
                + MessageProperties.msgProp.getProperty("SEAT_LOCATION") + seat.getLocation().getColNumber() + ","
                + seat.getLocation().getRowNumber());
        seatNum++;
    }
    System.out.println(MessageProperties.msgProp.getProperty("SEAT_HOLD_ID") + seatHold.getId());
    System.out.println(MessageProperties.msgProp.getProperty("NUM_SEAT_HOLD") + seats.size());
    System.out.println(MessageProperties.msgProp.getProperty("SEAT_HOLD_STATUS") + seatHold.getStatus());
}
}
