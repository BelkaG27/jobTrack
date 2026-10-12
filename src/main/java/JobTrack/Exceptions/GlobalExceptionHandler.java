package JobTrack.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;



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

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshToken(InvalidRefreshTokenException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    } 

    @ExceptionHandler(ChangeRoleException.class)
    public ResponseEntity<String> handleChangeRole(ChangeRoleException exception){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    } 

    @ExceptionHandler(ChangeSuspendedException.class)
    public ResponseEntity<String> handleChangeSuspended(ChangeSuspendedException exception){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler (InvalidFileFormatException.class)
    public ResponseEntity<String> handleInvalidFileFormat(InvalidFileFormatException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler (FileEmptyException.class)
    public ResponseEntity<String> handleFileEmpty(FileEmptyException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler (InvalidPdfException.class)
    public ResponseEntity<String> handleInvalidPdf(InvalidPdfException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler (StoredFileNotFoundException.class)
    public ResponseEntity<String> handleStoredFileNotFound(StoredFileNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler (FileStorageException.class)
    public ResponseEntity<String> handleFileStorageException(FileStorageException exception){   
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler (FileTypeAlreadyExistsException.class)
    public ResponseEntity<String> handleFileTypeAlreadyExists(FileTypeAlreadyExistsException exception){   
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler (FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFound(FileNotFoundException exception){   
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(exception.getMessage());
    }

    @ExceptionHandler (MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingRequestParam(MissingServletRequestParameterException exception){
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(exception.getMessage());
    }

    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleargumentTypeMismatch(MethodArgumentTypeMismatchException exception){
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(exception.getMessage());
    }

    @ExceptionHandler (MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleargumentTypeMismatch(MaxUploadSizeExceededException exception){
        return ResponseEntity.status(HttpStatusCode.valueOf(413)).body(exception.getMessage());
    }

    @ExceptionHandler (IllegalArgumentException.class)
    public ResponseEntity<String> handleargumentTypeMismatch(IllegalArgumentException exception){
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(exception.getMessage());
    }

}
