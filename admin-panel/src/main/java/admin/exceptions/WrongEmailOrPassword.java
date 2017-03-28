package admin.exceptions;

/**
 * Created by Trayan_Muchev on 7/29/2016.
 */
public class WrongEmailOrPassword extends Exception {
    public WrongEmailOrPassword() {
        super();
    }

    public WrongEmailOrPassword(String message) {
        super(message);
    }

    public WrongEmailOrPassword(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongEmailOrPassword(Throwable cause) {
        super(cause);
    }

    protected WrongEmailOrPassword(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
