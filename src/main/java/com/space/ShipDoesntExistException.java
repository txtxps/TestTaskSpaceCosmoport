package com.space;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShipDoesntExistException extends RuntimeException {
    public ShipDoesntExistException(String message) {
        super(message);
    }
}
