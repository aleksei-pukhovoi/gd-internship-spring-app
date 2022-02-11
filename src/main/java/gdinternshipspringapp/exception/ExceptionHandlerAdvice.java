package gdinternshipspringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<ServiceException> serviceExceptionHandler(ServiceException exception) {
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus()).body(exception);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ServiceException> generalHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ServiceException> methodArgumentHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(value = {ServletException.class})
    public ResponseEntity<ServiceException> clientErrorHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
}
