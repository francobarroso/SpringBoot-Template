package com.project.template.domain.dto;

public record Response<T>(boolean status,
                          T data,
                          String message) {
}
