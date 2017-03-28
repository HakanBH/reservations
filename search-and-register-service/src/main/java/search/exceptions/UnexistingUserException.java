package search.exceptions;

/**
 * Created by Trayan_Muchev on 9/13/2016.
 */
public class UnexistingUserException extends RuntimeException {

    public UnexistingUserException(String message) {
        super(message);
    }

}
