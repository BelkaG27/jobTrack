package JobTrack.Exceptions;

public class StoredFileNotFoundException extends RuntimeException {
    public StoredFileNotFoundException(String message){
        super(message);
    }
}