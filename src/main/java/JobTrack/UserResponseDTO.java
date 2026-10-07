package JobTrack;

public class UserResponseDTO {
    private int id;

    private String username;

    private String email;

    private String specialty;

    private int yearsOfExperience;

    private Role role; 

    private Boolean enabled;

    private Boolean suspended;

    public UserResponseDTO(int id, String username, String email, String specialty, int yearsOfExperience, Role role, Boolean enabled, Boolean suspended) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.specialty = specialty;
        this.yearsOfExperience = yearsOfExperience;
        this.role = role;
        this.enabled=enabled;
        this.suspended=suspended;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public static UserResponseDTO fromUser(User user){
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getSpecialty(), user.getYearsOfExperience(), user.getRole(),user.isEmailVerified(), user.getSuspended());
    }

    
}
