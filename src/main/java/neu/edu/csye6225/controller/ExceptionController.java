package neu.edu.csye6225.controller;

import neu.edu.csye6225.service.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<Object> exception(UnauthorizedException exception) {
        return new ResponseEntity<>("Unauthorized User", HttpStatus.UNAUTHORIZED);
    }

}
