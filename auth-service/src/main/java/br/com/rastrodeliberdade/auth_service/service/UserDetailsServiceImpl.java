package br.com.rastrodeliberdade.auth_service.service;

import br.com.rastrodeliberdade.auth_service.client.RiderServiceClient;
import br.com.rastrodeliberdade.auth_service.dto.RiderAuthDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RiderServiceClient riderServiceClient;

    public UserDetailsServiceImpl(RiderServiceClient riderServiceClient) {
        this.riderServiceClient = riderServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return riderServiceClient.findByEmail(username)
                .map(this::mapToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    private UserDetails mapToUserDetails(RiderAuthDto riderDto) {
        return new User(
                riderDto.email(),
                riderDto.password(),
                Collections.emptyList()
        );
    }
}