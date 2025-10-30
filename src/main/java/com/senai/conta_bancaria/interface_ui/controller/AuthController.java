package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.AuthDTO;
import com.senai.conta_bancaria.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Autenticação do Usuário no Sistema")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @Operation(
            summary = "Autentica um usuário",
            description = "Realiza o login de um usuário (Gerente, Cliente, Admin) e retorna um token JWT.",
            requestBody = @RequestBody(
                    description = "Credenciais de login",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthDTO.LoginRequest.class),
                            examples = @ExampleObject(name = "Exemplo de Login", value = """
                                    {
                                      "email": "admin@senai.com",
                                      "senha": "admin123"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login bem-sucedido, token retornado.",
                            content = @Content(
                                    schema = @Schema(implementation = AuthDTO.TokenResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"Credenciais inválidas.\"")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário não encontrado.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"Usuário não encontrado.\"")
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.TokenResponse> login(@org.springframework.web.bind.annotation.RequestBody AuthDTO.LoginRequest req) {
        String token = auth.login(req);
        return ResponseEntity.ok(new AuthDTO.TokenResponse(token));
    }
}
