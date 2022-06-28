package com.jmunoz.tddautenticacion.user;

import com.jmunoz.tddautenticacion.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/api/1.0/users")
    public GenericResponse createUser(User user) {
        userService.save(user);
        return new GenericResponse("User saved");
    }
}
