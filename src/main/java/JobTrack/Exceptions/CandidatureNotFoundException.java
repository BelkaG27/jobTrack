package JobTrack.Exceptions;

public class CandidatureNotFoundException extends RuntimeException {
    public CandidatureNotFoundException(String message){
        super(message);
    }
}
