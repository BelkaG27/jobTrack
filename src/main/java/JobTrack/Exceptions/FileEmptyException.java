package JobTrack.Exceptions;

public class FileEmptyException extends RuntimeException {
    public FileEmptyException(String message){
        super(message);
    }
}
