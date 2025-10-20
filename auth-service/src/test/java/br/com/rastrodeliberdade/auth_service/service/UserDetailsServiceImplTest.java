package br.com.rastrodeliberdade.auth_service.service;

import br.com.rastrodeliberdade.auth_service.client.RiderServiceClient;
import br.com.rastrodeliberdade.auth_service.dto.RiderAuthDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private RiderServiceClient riderServiceClient;

    @InjectMocks
    private UserDetailsServiceImpl userDetailService;

    private String userEmail;
    private RiderAuthDto riderAuthDto;

    @BeforeEach
    void setUp() {
        userEmail = "rider@test.com";
        riderAuthDto = new RiderAuthDto(
                UUID.randomUUID(),
                userEmail,
                "hashed-password"
        );
    }


    @Test
    @DisplayName("Should return UserDetails when user is found by client")
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {

        when(riderServiceClient.findByEmail(userEmail)).thenReturn(Optional.of(riderAuthDto));


        UserDetails userDetails = userDetailService.loadUserByUsername(userEmail);


        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(riderAuthDto.email());
        assertThat(userDetails.getPassword()).isEqualTo(riderAuthDto.password());
    }


    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found by client")
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {

        when(riderServiceClient.findByEmail(userEmail)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> userDetailService.loadUserByUsername(userEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + userEmail);
    }
}