package com.jmunoz.tddautenticacion.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.jmunoz.tddautenticacion.shared.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    UserService userService;

    @PostMapping("/api/1.0/login")
    @JsonView(Views.Base.class)
    User handleLogin(@CurrentUser CustomUserDetails customUserDetails) {
        return userService.findByUsername(customUserDetails.getUsername());
    }
}
