package com.systemsolution.user.resource;


import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserResource {
    private Long id;
    private String name;
    private String username;
    private String email;
}
