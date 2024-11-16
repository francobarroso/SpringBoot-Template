package com.project.template.domain.dto;

import lombok.NonNull;

public record AuthLoginRequest(@NonNull String username,
                               @NonNull String password) {
}