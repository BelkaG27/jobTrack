package JobTrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired 
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public PageResponseDTO<UserResponseDTO> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    } 

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(@PathVariable int id,@Valid @RequestBody RoleUpdateRequest request) {
        UserResponseDTO updatedUser = userService.updateUserRole(id, request.getNewRole());
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/suspended")
    public ResponseEntity<UserResponseDTO> updateUserSuspended(@PathVariable int id,@Valid  @RequestBody SuspendedUpdateRequest request) {
        UserResponseDTO updatedUser = userService.updateUserSuspended(id, request.getNewSuspended());
        return ResponseEntity.ok(updatedUser);
    }
}