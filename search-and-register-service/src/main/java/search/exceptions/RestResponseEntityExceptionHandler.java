package search.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Toncho_Petrov on 12/2/2016.
 */

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends Exception {


    public RestResponseEntityExceptionHandler() {
    }

    public RestResponseEntityExceptionHandler(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity handleUnauthorized(ExpiredJwtException ex) {
        String message = "Token is expired.";
        return new ResponseEntity(
                message, HttpStatus.UNAUTHORIZED);
    }
}
