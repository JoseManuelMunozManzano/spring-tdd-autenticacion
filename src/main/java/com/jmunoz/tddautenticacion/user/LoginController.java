package com.jmunoz.tddautenticacion.user;

import com.jmunoz.tddautenticacion.shared.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    UserService userService;

    @PostMapping("/api/1.0/login")
    User handleLogin(@CurrentUser CustomUserDetails customUserDetails) {
        return userService.findByUsername(customUserDetails.getUsername());
    }
}
