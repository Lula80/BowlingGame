package yuliya.akkuzhyna.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import yuliya.akkuzhyna.exception.FrameClosedException;
import yuliya.akkuzhyna.exception.PlayerNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@org.springframework.web.bind.annotation.ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(PlayerNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FrameClosedException.class)
    public ResponseEntity<?> handleUserNotFoundException(FrameClosedException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage(), request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage(), request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @AllArgsConstructor
    @Data
    private static class ErrorDetails{
        private String message;
        private String details;
    }
}
