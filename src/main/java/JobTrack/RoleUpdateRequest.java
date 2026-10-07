package JobTrack;

import jakarta.validation.constraints.NotNull;

public class RoleUpdateRequest {
    
    @NotNull 
    private Role newRole;

    public RoleUpdateRequest() {
    }

    public Role getNewRole() {
        return newRole;
    }

    public void setNewRole(Role newRole) {
        this.newRole = newRole;
    }



}
