package in.bugra.shareyapi.exception;

public class EmailAlreadyExists extends RuntimeException{

    public EmailAlreadyExists(String message) {
        super(message);
    }
}
