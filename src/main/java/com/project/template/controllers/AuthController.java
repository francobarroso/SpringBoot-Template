package com.project.template.controllers;

import com.project.template.domain.dto.AuthLoginRequest;
import com.project.template.domain.dto.AuthResponse;
import com.project.template.domain.dto.Response;
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
    public ResponseEntity<Response<AuthResponse>> register(@RequestBody UserDTO user){
        var response = this.userDetailService.register(user);
        return new ResponseEntity<>(response, response.data() == null ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<AuthResponse>> login(@RequestBody AuthLoginRequest userRequest){
        return new ResponseEntity<>(this.userDetailService.loginUser(userRequest), HttpStatus.OK);
    }
}
