package Exceptions;

public class OutOfEnergyException extends Exception{
    private String message;
    public OutOfEnergyException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
