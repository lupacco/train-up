package br.com.customer.exception;

public class UserEmailNotFoundException extends RuntimeException{
    public UserEmailNotFoundException(String message) { super(message);}
}
