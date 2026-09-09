package JobTrack.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;



@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(CandidatureNotFoundException.class)
    public ResponseEntity<String> handleCandidatureNotFound(CandidatureNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationErrors(MethodArgumentNotValidException exception){
        
        Map<String,String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error->
                    errors.put(error.getField(), error.getDefaultMessage())
                );
        
        return ResponseEntity.badRequest().body(errors);
    }
}
