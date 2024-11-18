package com.project.template.mock;

import com.project.template.domain.dto.UserDTO;
import com.project.template.domain.entities.Role;
import com.project.template.domain.entities.UserEntity;
import com.project.template.domain.entities.UserHistory;
import com.project.template.domain.enums.ERole;

import java.util.*;

public class UserDetailServiceProvider {

    public static UserEntity userMock(){
        return UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username("test")
                .password("password123")
                .roles(rolesMock())
                .build();
    }

    public static Set<Role> rolesMock(){
        return Set.of(
                Role.builder()
                        .id(1L)
                        .roleName(ERole.ADMIN)
                        .build(),
                Role.builder()
                        .id(1L)
                        .roleName(ERole.PRO)
                        .build()
        );
    }

    public static List<String> rolesString(){
        List<String> rolesString = new ArrayList<>();
        rolesMock()
                .forEach(role -> rolesString.add(role.getRoleName().name()));
        return rolesString;
    }

    public static UserDTO userDtoMock(){
        return UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .username("test")
                .password("password123")
                .roles(rolesMock())
                .build();
    }

    public static UserHistory userHistoryMock(){
        return UserHistory.builder()
                .user(userMock())
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }
}
