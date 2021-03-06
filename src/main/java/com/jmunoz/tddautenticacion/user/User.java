package com.jmunoz.tddautenticacion.user;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Base.class)
    private long id;

    @NotNull(message = "{tdd-autenticacion.constraints.username.NotNull.message}")
    @Size(min = 4, max = 255)
    @UniqueUsername
    @JsonView(Views.Base.class)
    private String username;

    @NotNull
    @Size(min = 4, max = 255)
    @JsonView(Views.Base.class)
    private String displayName;

    @NotNull
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "{tdd-autenticacion.constraints.password.Pattern.message}")
    private String password;

    @JsonView(Views.Base.class)
    private String image;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
