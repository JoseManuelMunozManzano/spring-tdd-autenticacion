package com.jmunoz.tddautenticacion;

import com.jmunoz.tddautenticacion.user.User;

public class TestUtil {

    public static User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        user.setImage("profile-image.png");

        return user;
    }
}
