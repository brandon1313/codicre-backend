package com.systemsolution.user.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.systemsolution.commons.wrapper.ApiResult;
import com.systemsolution.user.converter.UserConverter;
import com.systemsolution.user.resource.UserRequest;
import com.systemsolution.user.resource.UserResource;
import com.systemsolution.user.resource.UserRoleResource;
import com.systemsolution.user.services.UserService;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;
    

    public UserController(final UserService userService,
                          final UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('USERS','ADMIN')")
    public ApiResult<List<UserResource>> getAllUsers() {
        final List<UserResource> userProjections = userService.getAllUsers()
                .stream()
                .map(userConverter::convert)
                .collect(toList());

        return new ApiResult(userProjections, "Ok");
    }

    @GetMapping(value = "/{userId}")
    @ResponseStatus(OK)
    public ApiResult<UserResource> getUser(@PathVariable("userId") final Long userId) throws RuntimeException {
        final UserResource userResource = userService.getUserById(userId)
                .map(userConverter::convert)
                .orElseThrow(() -> new RuntimeException("The user couldn't been found"));

        return new ApiResult(userResource, "Ok");
    }
    
    @GetMapping(value = "/roles/{userId}")
    @ResponseStatus(OK)
    public ApiResult<List<UserRoleResource>> getRoles(@PathVariable("userId") final Long userId) throws RuntimeException {
        return new ApiResult(userService.getRoles(userId), "Ok");
    }

    @PostMapping
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('USERS_CREATE','ADMIN')")
    public ApiResult<UserResource> createUser(@RequestBody @Valid final UserRequest userRequest){
        final UserResource createdUser = userService.createUser(userRequest)
                .map(userConverter::convert)
                .orElseThrow(() -> new RuntimeException("The user couldn't been created"));

        return new ApiResult(createdUser, "Ok");
    }

    @PostMapping("/roles/administration/{userId}/{roleName}/{action}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('USERS_ROLES','ADMIN')")
    public ApiResult<String> addOrRemove(@PathVariable("userId") final Long userId,
    		@PathVariable("roleName") final String roleName, @PathVariable("action") final Boolean action ){
        if(action.booleanValue()) 
        	userService.addRole(userId, roleName);
        else
        	userService.removeRole(userId, roleName);
        
        return new ApiResult("Ok", "Ok");
    }
    
    @PutMapping(value = "/{userId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('USERS_UPDATE','ADMIN')")
    public ApiResult<UserResource> updateUser(
            @PathVariable("userId") final Long userId,
            @RequestBody @Valid final UserRequest userRequest){
        final UserResource updatedUser = userService.updateUser(userId, userRequest)
                .map(userConverter::convert)
                .orElseThrow(() -> new RuntimeException("The user couldn't been updated"));

        return new ApiResult(updatedUser, "Ok");
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('USERS_DELETE','ADMIN')")
    public ApiResult<UserResource> deleteUser(@PathVariable("userId") final Long userId) {
        userService.deleteUser(userId);
        return new ApiResult(null, "Ok");
    }
}
