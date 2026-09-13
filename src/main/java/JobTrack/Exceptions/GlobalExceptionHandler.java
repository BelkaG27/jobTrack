package JobTrack.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;



@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(CandidatureNotFoundException.class)
    public ResponseEntity<String> handleCandidatureNotFound(CandidatureNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlredyExists(UsernameAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlredyExists(EmailAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException exception){
        
        InvalidFormatException ife = getCause(exception, InvalidFormatException.class);
        // specifique pour le format de date invalide
        if(ife != null && ife.getTargetType()==LocalDate.class){
            return ResponseEntity.badRequest().body("le format de la date doit etre yyyy-MM-dd");
        }
        MismatchedInputException mie = getCause(exception, MismatchedInputException.class);
        // structure ou type incompatible
        if(mie != null){
            String champ = mie.getPath().isEmpty() ? "?" : mie.getPath().get(mie.getPath().size()-1).getPropertyName();
            return ResponseEntity.badRequest().body("le format du champ "+champ+" est invalide !");
        }

        return ResponseEntity.badRequest().body("le format est invalid");
    }

    private <T extends Throwable> T getCause(Throwable throwable, Class<T> type){
        Throwable cause = throwable;
        while(cause!=null){
            if(type.isInstance(cause)){
                return type.cast(cause);
            }
            cause=cause.getCause();
        }
        return null;
    } 
}
