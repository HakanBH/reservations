package search.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Trayan_Muchev on 7/29/2016.
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
public class WrongEmailOrPassword extends RuntimeException {

    public WrongEmailOrPassword(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
