package com.systemsolution.commons.auditing;


import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.systemsolution.security.services.UserPrinciple;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("automatic");
        }

        final UserPrinciple userPrincipal =  (UserPrinciple) authentication.getPrincipal();
        final String username = userPrincipal.getUsername();

        return Optional.of(username);
    }
}
