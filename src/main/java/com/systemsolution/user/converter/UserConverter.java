package com.systemsolution.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.systemsolution.model.User;
import com.systemsolution.user.resource.UserResource;

@Service
public class UserConverter implements Converter<User, UserResource> {

    @Override
    public UserResource convert(final User user) {
        return UserResource.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}
