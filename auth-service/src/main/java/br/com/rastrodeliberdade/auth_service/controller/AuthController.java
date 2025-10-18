package br.com.rastrodeliberdade.auth_service.controller;

import br.com.rastrodeliberdade.auth_service.dto.LoginRequestDto;
import br.com.rastrodeliberdade.auth_service.dto.LoginResponseDto;
import br.com.rastrodeliberdade.auth_service.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "Authenticate a user",
            description = "Endpoint to authenticate a user with email and password, returning a JWT if successful.")
    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                loginRequestDto.email(),
                loginRequestDto.password());

        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken(auth);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
