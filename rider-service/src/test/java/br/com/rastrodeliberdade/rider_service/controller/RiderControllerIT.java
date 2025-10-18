package br.com.rastrodeliberdade.rider_service.controller;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import br.com.rastrodeliberdade.rider_service.exception.ResourceNotFoundException;
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

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                UUID.randomUUID(),
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
                .andExpect(jsonPath("$.id").isNotEmpty())
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
                .andExpect(jsonPath("$.[0].id").value(existingRider.getId().toString()))
                .andExpect(jsonPath("$.[0].bikerNickname").value(existingRider.getBikerNickname()))
                .andExpect(jsonPath("$.[0].email").value(existingRider.getEmail()))
                .andExpect(jsonPath("$.[0].city").value(existingRider.getCity()))
                .andExpect(jsonPath("$.[0].state").value(existingRider.getState()));
    }

    @Test
    @DisplayName("Should Return 200 Ok and RiderSummaryDto when call findById and everything is ok")
    void findById_Return200OkAndRiderSummary_WhenEverythingIsOK() throws Exception{
        UUID idToFind = existingRider.getId();

        mockMvc.perform(get("/rider/{id}",idToFind)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingRider.getId().toString()))
                .andExpect(jsonPath("$.bikerNickname").value(existingRider.getBikerNickname()))
                .andExpect(jsonPath("$.email").value(existingRider.getEmail()))
                .andExpect(jsonPath("$.city").value(existingRider.getCity()))
                .andExpect(jsonPath("$.state").value(existingRider.getState()));

    }

    @Test
    @DisplayName("Should Return 404 and Resource Not Found Exception when call findById with non existing id")
    void finById_ReturnResourceNotFoundException_WhenIdNonExisting() throws Exception{
        UUID idToFind = UUID.randomUUID();

        mockMvc.perform(get("/rider/{id}",idToFind)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Rider com ID "+idToFind+" não encontrado."));
    }

    @Test
    @DisplayName("Should Return 200 Ok and RiderSummaryDto when call findByEmail and everything is ok")
    void findByEmail_Return200OkAndRiderSummary_WhenEverythingIsOK() throws Exception{
        RiderSummaryDto expectedResultRiderSummary = new RiderSummaryDto(
                existingRider.getId(),
                existingRider.getBikerNickname(),
                existingRider.getEmail(),
                existingRider.getCity(),
                existingRider.getState());

        mockMvc.perform(get("/rider/search/by-email?email=carlos.antonio@test.com")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResultRiderSummary.id().toString()))
                .andExpect(jsonPath("$.bikerNickname").value(expectedResultRiderSummary.bikerNickname()))
                .andExpect(jsonPath("$.email").value(expectedResultRiderSummary.email()))
                .andExpect(jsonPath("$.city").value(expectedResultRiderSummary.city()))
                .andExpect(jsonPath("$.state").value(expectedResultRiderSummary.state()));

    }

    @Test
    @DisplayName("Should Return 404 and Resource Not Found Exception when call findByEmail with non existing email")
    void finByEmail_ReturnResourceNotFoundException_WhenEmailNonExisting() throws Exception{
        String emailToFind = "testeerro@erro.com";

        mockMvc.perform(get("/rider/search/by-email?email=testeerro@erro.com")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Rider não encontrado com e-mail: '"+emailToFind+"'"));

    }

    @Test
    @DisplayName("Should return 200 Ok and a list of riders when call findByState and everything is ok")
    void findByState_Return200OkAndListOfRiders_WhenEverythingIsOk() throws Exception {
        String stateToFind = "São Paulo";

        mockMvc.perform(get("/rider/search/by-state")
                        .param("state", stateToFind)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(existingRider.getId().toString()))
                .andExpect(jsonPath("$.[0].state").value(stateToFind));
    }

    @Test
    @DisplayName("Should return 200 Ok and an empty list when call findByState with a non-existing state")
    void findByState_Return200OkAndEmptyList_WhenStateNonExisting() throws Exception {
        String stateToFind = "Estado Inexistente";

        mockMvc.perform(get("/rider/search/by-state")
                        .param("state", stateToFind)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("Should return 200 OK and updated Rider when call update and everything is ok")
    void update_Return200OkAndUpdatedRider_WhenEverythingIsOk() throws Exception {
        UUID idToUpdate = existingRider.getId();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Carlos Antonio Updated",
                "carlos.updated@test.com",
                "carlos.antonio.updated",
                "newValidPassword123",
                "Rio de Janeiro",
                "RJ"
        );

        mockMvc.perform(put("/rider/{id}", idToUpdate)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idToUpdate.toString()))
                .andExpect(jsonPath("$.bikerNickname").value(riderUpdateDto.bikerNickname()))
                .andExpect(jsonPath("$.email").value(riderUpdateDto.email()))
                .andExpect(jsonPath("$.city").value(riderUpdateDto.city()));
    }

    @Test
    @DisplayName("Should return 404 Not Found when call update with a non-existing id")
    void update_Return404NotFound_WhenIdNonExisting() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Any Name",
                "any@email.com",
                "any.nick",
                "validPassword123",
                "Any City",
                "Any State"
        );

        mockMvc.perform(put("/rider/{id}", nonExistingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Rider com ID " + nonExistingId + " não encontrado."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when call update with email that belongs to another rider")
    void update_Return400BadRequest_WhenEmailBelongsToAnotherRider() throws Exception {
        Rider anotherRider = riderRepository.save(Rider.builder()
                .fullName("João Silva")
                .email("joao.silva@test.com")
                .bikerNickname("joao.silva")
                .password("testpass")
                .city("City")
                .state("State")
                .build());

        UUID idToUpdate = existingRider.getId();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Carlos Antonio",
                "joao.silva@test.com",
                "carlos.antonio.updated",
                "validPassword123",
                "São Paulo",
                "São Paulo"
        );

        mockMvc.perform(put("/rider/{id}", idToUpdate)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe um usuario diferente cadastrado com o e-mail: " + riderUpdateDto.email()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when call update with nickname that belongs to another rider")
    void update_Return400BadRequest_WhenNicknameBelongsToAnotherRider() throws Exception {
        Rider anotherRider = riderRepository.save(Rider.builder()
                .fullName("João Silva")
                .email("joao.silva@test.com")
                .bikerNickname("joao.silva")
                .password("testpass")
                .city("City")
                .state("State")
                .build());

        UUID idToUpdate = existingRider.getId();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Carlos Antonio",
                "carlos.updated@test.com",
                "joao.silva",
                "validPassword123",
                "São Paulo",
                "São Paulo"
        );

        mockMvc.perform(put("/rider/{id}", idToUpdate)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe um usuario diferente cadastrado com o nickname: " + riderUpdateDto.bikerNickname()));
    }

}
