package com.senai.conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDTO {
    @Schema(description = "Requisição de login")
    public record LoginRequest(
            @Schema(description = "Email do usuário", example = "usuario@email.com")
            String email,
            @Schema(description = "Senha do usuário", example = "senha123")
            String senha
    ) {
    }

    @Schema(description = "Resposta com o token JWT")
    public record TokenResponse(
            @Schema(description = "Token JWT gerado", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBzZW5haS5jb20iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3MzAyNjU0MjUsImV4cCI6MTczMDI2NTcyNX0.7Qy8qgYmD3fioe-u4j8e-8-gY")
            String token
    ) {
    }
}