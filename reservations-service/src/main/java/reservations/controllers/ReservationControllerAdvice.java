package reservations.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ReservationControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ExpiredJwtException.class, JwtException.class})
    public ResponseEntity catchUnauthorized(Exception ex) {
        ex.printStackTrace();
        String message = "Token's expired.";
        return new ResponseEntity<Object>(
                message, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity catchAccessDenied() {
        String message = "Access denied.";
        return new ResponseEntity<Object>(
                message, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
