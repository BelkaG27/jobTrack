package JobTrack.Exceptions;

public class FileTypeAlreadyExistsException extends RuntimeException {
    public FileTypeAlreadyExistsException(String message){
        super(message);
    }
}
