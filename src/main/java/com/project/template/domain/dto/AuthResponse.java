package com.project.template.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "token"})
public record AuthResponse(String username,
                           String token) {
}
