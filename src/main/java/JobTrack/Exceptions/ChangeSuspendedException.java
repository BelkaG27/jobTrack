package JobTrack.Exceptions;

public class ChangeSuspendedException extends RuntimeException {
    public ChangeSuspendedException(String message){
        super(message);
    }
}
