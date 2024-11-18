package com.project.template.domain.dto;

import com.project.template.domain.entities.Role;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Set<Role> roles = new HashSet<>();
}
