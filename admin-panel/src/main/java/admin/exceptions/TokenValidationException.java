package admin.exceptions;

/**
 * Created by Hakan_Hyusein on 12/9/2016.
 */
public class TokenValidationException extends Exception {
    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
