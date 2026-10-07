package JobTrack;
import jakarta.validation.constraints.NotNull;

public class SuspendedUpdateRequest {
    @NotNull 
    private Boolean newSuspended;

    public SuspendedUpdateRequest() {
    }

    public Boolean getNewSuspended() {
        return newSuspended;
    }

    public void setNewSuspended(Boolean newSuspended) {
        this.newSuspended = newSuspended;
    }
}




