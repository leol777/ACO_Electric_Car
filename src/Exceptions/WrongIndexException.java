package Exceptions;

public class WrongIndexException extends Exception{
    private String message;
    public WrongIndexException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
