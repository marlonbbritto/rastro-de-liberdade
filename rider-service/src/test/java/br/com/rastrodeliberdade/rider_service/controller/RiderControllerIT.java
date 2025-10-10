package br.com.rastrodeliberdade.rider_service.controller;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import br.com.rastrodeliberdade.rider_service.repository.RiderRepository;
import br.com.rastrodeliberdade.rider_service.service.RiderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

@Transactional
public class RiderControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RiderService riderService;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Rider existingRider;

    @BeforeEach
    void Setup(){
        Rider savedRider = Rider.builder()
                .fullName("Carlos Antonio")
                .email("carlos.antonio@test.com")
                .bikerNickname("carlos.antonio")
                .password(passwordEncoder.encode("12345mudar!"))
                .city("São Paulo")
                .state("São Paulo")
                .build();

        this.existingRider = riderRepository.save(savedRider);

    }

    @Test
    @DisplayName("Should return 201 Created and new inserted Rider when call insert and everything is ok")
    void insert_Return201CreatedAndNewRider_WhenEverythingIsOk() throws Exception{
        RiderInsertDto riderInsertDto = new RiderInsertDto(
                "João Silva",
                "joao.silva@test.com",
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        RiderSummaryDto expectedRiderSummaryDto = new RiderSummaryDto(
                riderInsertDto.bikerNickname(),
                riderInsertDto.email(),
                riderInsertDto.city(),
                riderInsertDto.state()
        );

        mockMvc.perform(post("/rider")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderInsertDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bikerNickname").value(riderInsertDto.bikerNickname()))
                .andExpect(jsonPath("$.email").value(riderInsertDto.email()))
                .andExpect(jsonPath("$.city").value(riderInsertDto.city()))
                .andExpect(jsonPath("$.state").value(riderInsertDto.state()));

    }

    @Test
    @DisplayName("Should return 400 Business Exception when call insert with existing email")
    void insert_Return400BusinessException_WhenEmailAlreadyExists() throws Exception{
        RiderInsertDto riderInsertDto = new RiderInsertDto(
                "João Silva",
                "carlos.antonio@test.com",
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        mockMvc.perform(post("/rider")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderInsertDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Erro na regra de negócio"))
                .andExpect(jsonPath("$.message").value("Já existe um  usuário cadastrado com o e-mail: "+riderInsertDto.email()));

    }

    @Test
    @DisplayName("Should return 400 Business Exception when call insert with existing rider nickname")
    void insert_Return400BusinessException_WhenNickNameAlreadyExists() throws Exception{
        RiderInsertDto riderInsertDto = new RiderInsertDto(
                "João Silva",
                "joao.silva@test.com",
                "carlos.antonio",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        mockMvc.perform(post("/rider")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderInsertDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Erro na regra de negócio"))
                .andExpect(jsonPath("$.message").value("já existe um usuário cadastrado com o nickname: "+riderInsertDto.bikerNickname()));

    }

    @Test
    @DisplayName("Should return 200 Ok and an list of riders when call findAll and everything is ok")
    void findAll_ReturnListOfRider_WhenEverythingIsOK() throws Exception{
        mockMvc.perform(get("/rider")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].bikerNickname").value(existingRider.getBikerNickname()))
                .andExpect(jsonPath("$.[0].email").value(existingRider.getEmail()))
                .andExpect(jsonPath("$.[0].city").value(existingRider.getCity()))
                .andExpect(jsonPath("$.[0].state").value(existingRider.getState()));
    }


}
