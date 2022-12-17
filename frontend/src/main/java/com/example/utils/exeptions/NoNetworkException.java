package com.example.utils.exeptions;

public class NoNetworkException extends Exception {
    public NoNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
