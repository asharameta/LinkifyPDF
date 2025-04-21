package supervisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PdfEntityNoContentException.class)
    public ResponseEntity<String> handlePdfNotFound(PdfEntityNoContentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NO_CONTENT);
    }
    @ExceptionHandler(PdfEntityNotFoundException.class)
    public ResponseEntity<String> handlePdfNotFound(PdfEntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SelectionEntityNotFoundException.class)
    public ResponseEntity<String> handlePdfNotFound(SelectionEntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
