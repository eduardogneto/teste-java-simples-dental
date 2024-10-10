package com.api.simplesdental.dto.exception;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String message;

    public ErrorResponseDTO(String message) {
        this.message = message;
    }

}
