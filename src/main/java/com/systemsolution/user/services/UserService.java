package com.systemsolution.user.services;



import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.systemsolution.model.Role;
import com.systemsolution.model.RoleName;
import com.systemsolution.model.User;
import com.systemsolution.repository.RoleRepository;
import com.systemsolution.repository.UserRepository;
import com.systemsolution.user.resource.UserRequest;
import com.systemsolution.user.resource.UserRoleResource;

import javax.persistence.EntityNotFoundException;


import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
public class UserService {

    private static final String USER_DOESNT_EXISTS = "User %s does not exists";
	private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
   
    public UserService(final UserRepository userRepository,
                       final PasswordEncoder passwordEncoder, final RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(final Long userId) {
    	return userRepository.findById(userId);
    }
    
    public User findByUsername(final String userName) {
    	return userRepository.findByUsername(userName)
    			.orElseThrow(() -> new EntityNotFoundException(String.format(USER_DOESNT_EXISTS,userName)));
    }

    public void deleteUser(final Long userId) {
        userRepository.deleteById(userId);
    }

    public void removeRole(final Long id, final String roleName) {
    	RoleName name = RoleName.valueOf(roleName);
    	Role role = roleRepository.findByName(name)
    			.orElseThrow(() -> new RuntimeException("ROL No existe."));
    	User user = findById(id);
    	Set<Role> roles = user.getRoles();
    	roles.remove(role);
    	user.setRoles(roles);
    	userRepository.save(user);
    }
    
    public void addRole(final Long id, final String roleName) {
    	RoleName name = RoleName.valueOf(roleName);
    	Role role = roleRepository.findByName(name)
    			.orElseThrow(() -> new RuntimeException("ROL No existe."));
    	User user = findById(id);
    	Set<Role> roles = user.getRoles();
    	roles.add(role);
    	user.setRoles(roles);
    	userRepository.save(user);
    }
    
    public Optional<User> updateUser(final Long userId, final UserRequest userRequest) throws RuntimeException {
        final User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("The user couldn't been found"));
        user.setName(userRequest.getName());
        final String encodedPassword = this.passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);
        
        if (userRepository.existsByUsername(userRequest.getName())) {
            throw new RuntimeException("username is already taken");
        }
        
        return Optional.of(userRepository.save(user));
    }
    

    public List<UserRoleResource> getRoles(final Long id){
    	return roleRepository.getRoles(id);
    }
    
    public User findById(final Long id) {
    	return userRepository.findById(id)
    			.orElseThrow(() -> new EntityNotFoundException(String.format(USER_DOESNT_EXISTS, id)));
    }
	public Optional<User> createUser(final UserRequest userRequest) {
        log.info("User requests is {}", userRequest);

        final String username = userRequest.getUsername();
        final String password = userRequest.getPassword();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("username is already taken");
        }

        final String encodedPassword = this.passwordEncoder.encode(password);
        final User user = User.builder()
                .name(userRequest.getName())
                .username(username)
                .password(encodedPassword)
                .build();

        return Optional.of(userRepository.save(user));
	}
}
