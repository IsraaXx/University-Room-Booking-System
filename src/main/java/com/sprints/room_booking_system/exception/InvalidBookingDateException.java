package com.sprints.room_booking_system.exception;

public class InvalidBookingDateException extends RuntimeException {
    
    public InvalidBookingDateException(String message) {
        super(message);
    }
    
    public InvalidBookingDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
