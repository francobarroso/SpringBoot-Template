package com.project.template.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/secured")
    @PreAuthorize("hasRole('ADMIN')")
    public String secured(){
        return "Secured endpoint";
    }

    @GetMapping("/basic")
    @PreAuthorize("hasRole('BASIC')")
    public String basic(){
        return "Basic endpoint";
    }

    @GetMapping("/pro")
    @PreAuthorize("hasRole('PRO')")
    public String pro(){
        return "Pro endpoint";
    }
}
