package JobTrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import JobTrack.Exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Service 
public class UserService {
    
    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private RefreshTokenRepository refreshTokenRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public PageResponseDTO<UserResponseDTO> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAllUsers(pageable);
        return new PageResponseDTO<UserResponseDTO>(page.getNumber(),page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.map(UserResponseDTO::fromUser).toList());
    }

    @Transactional
    public UserResponseDTO updateUserRole(int id, Role newRole) {
        if(getCurrentUser().getId() == id){
            throw new ChangeRoleException("You cannot change your own role.");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRole(newRole);
        return UserResponseDTO.fromUser(user);
    }

    @Transactional
    public UserResponseDTO updateUserSuspended(int id, Boolean suspended) {
        if(getCurrentUser().getId() == id){
            throw new ChangeSuspendedException("You cannot change your own suspended status.");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setSuspended(suspended);
        userRepository.saveAndFlush(user);
        if(suspended){
            refreshTokenRepository.revokeAllByUser(user);
        }
        return UserResponseDTO.fromUser(user);
    }



}
