package com.ticketservice.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates the user input
 *
 */
public class UIValidation {

/**
 * Validate customer email address has valid syntax
 * 
 * @param customerEmail
 * @throws TicketServiceException
 */
public static void validateCustomerEmail(String customerEmail) throws TicketServiceException {
    if (Utility.isNotNull(customerEmail)) {
        Pattern emailPattern = Pattern.compile("^[A-Z]+[0-9._]*[A-Z]*@[A-Z]+.[A-Z]{3}", Pattern.CASE_INSENSITIVE);
        Matcher match = emailPattern.matcher(customerEmail);
        if (!match.find()) {
            throw new TicketServiceException(MessageProperties.msgProp.getProperty("INVALID_EMAIL_ADDR"));
        }
    } else {
        throw new TicketServiceException(MessageProperties.msgProp.getProperty("INVALID_EMAIL_ADDR"));
    }

}

/**
 * Check if the totalSeats and holdtime entered by user are integers
 * 
 * @param totalSeats
 * @param holdTime
 * @return
 * @throws TicketServiceException
 */
public static boolean checkIfNumbers(String numRows, String numCols, String holdTime) {
    try {
        if (Utility.isNotNull(numRows) && Utility.isNotNull(holdTime)) {
            Pattern isNumber = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
            Matcher match = isNumber.matcher(numRows);
            if (match.find()) {
                isPositiveNumber(Integer.parseInt(numRows));
                match = isNumber.matcher(numCols);
                if (match.find()) {
                    isPositiveNumber(Integer.parseInt(numCols));
                    match = isNumber.matcher(holdTime);
                    if (match.find()) {
                        isPositiveNumber(Integer.parseInt(holdTime));
                        return true;
                    }
                }
            }
        }
        System.out.println(MessageProperties.msgProp.getProperty("INVALID_USER_INPUT"));
    } catch (TicketServiceException e) {

    }
    return false;
}

/**
 * Check that user input is positive number
 * 
 * @param number
 * @throws TicketServiceException
 */
public static void isPositiveNumber(int number) throws TicketServiceException {
    String strNumber = new Integer(number).toString();
    Pattern isNumber = Pattern.compile("^[0-9]+", Pattern.CASE_INSENSITIVE);
    Matcher match = isNumber.matcher(strNumber);
    if (!match.find()) {
        throw new TicketServiceException("");
    }
}

}
