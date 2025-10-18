package br.com.rastrodeliberdade.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=minha-chave-secreta-super-longa-para-testes-de-hs512-nao-usar-em-producao",
        "jwt.expiration=60000"
})
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String testJwtSecret;

    private Authentication authentication;
    private String userEmail = "teste@rastrodeliberdade.com";

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new User(
                userEmail,
                "qualquer-senha",
                Collections.emptyList());

        authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("Should generate a valid JWT when given a correct authentication")
    void generateToken_withValidAuthentication_shouldReturnValidJwt() {
        String token = tokenService.generateToken(authentication);

        assertThat(token).isNotNull().isNotBlank();

        SecretKey key = Keys.hmacShaKeyFor(testJwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("Should throw NullPointerException when authentication is null")
    void generateToken_withNullAuthentication_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            tokenService.generateToken(null);
        });
    }
}