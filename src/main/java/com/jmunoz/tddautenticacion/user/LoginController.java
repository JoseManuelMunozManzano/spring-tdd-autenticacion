package com.jmunoz.tddautenticacion.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    UserService userService;

    @PostMapping("/api/1.0/login")
    Map<String, Object> handleLogin(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(customUserDetails.getUsername());

        return Collections.singletonMap("id", user.getId());
    }
}
