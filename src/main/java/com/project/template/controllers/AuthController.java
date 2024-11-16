package com.project.template.controllers;

import com.project.template.domain.dto.AuthLoginRequest;
import com.project.template.domain.dto.AuthResponse;
import com.project.template.domain.dto.UserDTO;
import com.project.template.services.auth.UserDetailServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    UserDetailServiceImpl userDetailService;

    public AuthController(UserDetailServiceImpl userDetailService) {
        this.userDetailService = userDetailService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDTO user){
        return new ResponseEntity<>(this.userDetailService.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest userRequest){
        return new ResponseEntity<>(this.userDetailService.loginUser(userRequest), HttpStatus.OK);
    }
}
