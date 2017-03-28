package search.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Hakan_Hyusein on 12/9/2016.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)  // 401
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
