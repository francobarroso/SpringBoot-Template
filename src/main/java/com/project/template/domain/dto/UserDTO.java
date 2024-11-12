package com.project.template.domain.dto;

import com.project.template.domain.entities.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Set<Role> roles = new HashSet<>();
}
