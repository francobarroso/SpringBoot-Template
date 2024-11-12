package com.project.template.domain.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    public boolean status;
    public T data;
    public String message;
}
