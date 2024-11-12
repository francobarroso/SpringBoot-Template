package com.project.template.controllers;

import com.project.template.domain.dto.ResponseDTO;
import com.project.template.domain.dto.UserDTO;
import com.project.template.domain.entities.UserEntity;
import com.project.template.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseDTO<?> register(@RequestBody UserDTO user){
        var response = new ResponseDTO<UserDTO>();
        try {
            response.setStatus(true);
            response.setData(userService.register(user));
        }catch (Exception ex){
            response.setStatus(false);
            response.setMessage(ex.getMessage());
        }

        return response;
    }

    @GetMapping("/secured")
    @PreAuthorize("hasRole('ADMIN')")
    public String secured(){
        return "Secured endpoint";
    }
}
