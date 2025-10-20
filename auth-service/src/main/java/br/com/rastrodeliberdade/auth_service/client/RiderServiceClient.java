package br.com.rastrodeliberdade.auth_service.client;

import br.com.rastrodeliberdade.auth_service.dto.RiderAuthDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class RiderServiceClient {
    private final RestTemplate restTemplate;
    private final String riderServiceBaseUrl;

    public RiderServiceClient(RestTemplate restTemplate, @Value("${rider.service.url}") String riderServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.riderServiceBaseUrl = riderServiceBaseUrl;
    }

    public Optional<RiderAuthDto> findByEmail(String email) {
        String url = UriComponentsBuilder.fromHttpUrl(riderServiceBaseUrl)
                .path("/rider/internal/by-email")
                .queryParam("email", email)
                .toUriString();

        try {
            RiderAuthDto rider = restTemplate.getForObject(url, RiderAuthDto.class);
            return Optional.ofNullable(rider);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

}
