package io.oneinch.sdk.exception;

public class OneInchException extends Exception {
    
    public OneInchException(String message) {
        super(message);
    }
    
    public OneInchException(String message, Throwable cause) {
        super(message, cause);
    }
}