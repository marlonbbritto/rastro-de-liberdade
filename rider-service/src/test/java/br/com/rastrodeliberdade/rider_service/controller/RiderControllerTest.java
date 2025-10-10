package br.com.rastrodeliberdade.rider_service.controller;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import br.com.rastrodeliberdade.rider_service.exception.BusinessException;
import br.com.rastrodeliberdade.rider_service.mapper.RiderMapper;
import br.com.rastrodeliberdade.rider_service.service.RiderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import br.com.rastrodeliberdade.rider_service.config.SecurityConfig;
import org.springframework.context.annotation.Import;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(RiderController.class)
@Import({SecurityConfig.class})
public class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RiderMapper riderMapper;

    @MockitoBean
    private RiderService riderService;

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

        given(riderService.insertRider(any(RiderInsertDto.class))).willReturn(expectedRiderSummaryDto);

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
                "joao.silva@test.com",
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        given(riderService.insertRider(riderInsertDto)).willThrow(new BusinessException("Já existe um  usuário cadastrado com o e-mail: "+riderInsertDto.email()));

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
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        given(riderService.insertRider(riderInsertDto)).willThrow(new BusinessException("já existe um usuário cadastrado com o nickname: "+riderInsertDto.bikerNickname()));

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
        Rider fakeRider1 = Rider.builder()
                .id(UUID.randomUUID())
                .fullName("João Silva")
                .email("joao.silva@test.com")
                .bikerNickname("joao.silva")
                .password(passwordEncoder.encode("teste123@"))
                .city("Maringá")
                .state("Paraná")
                .build();

        Rider fakeRider2 = Rider.builder()
                .id(UUID.randomUUID())
                .fullName("Paulo Carvalho")
                .email("paulo.carvalho@test.com")
                .bikerNickname("paulo.carvalho")
                .password(passwordEncoder.encode("teste123@"))
                .city("Cascavel")
                .state("Paraná")
                .build();

        List<Rider> existingRiderList = List.of(fakeRider1,fakeRider2);

        RiderSummaryDto fakeDto1 = new RiderSummaryDto("joao.silva", "joao.silva@test.com", "Maringá", "Paraná");
        RiderSummaryDto fakeDto2 = new RiderSummaryDto("paulo.carvalho", "paulo.carvalho@test.com", "Cascavel", "Paraná");

        List<RiderSummaryDto> expectedRiderList = List.of(fakeDto1, fakeDto2);


        when(riderMapper.toSummaryDto(fakeRider1)).thenReturn(fakeDto1);
        when(riderMapper.toSummaryDto(fakeRider2)).thenReturn(fakeDto2);

        given(riderService.findAllRider()).willReturn(expectedRiderList);

        mockMvc.perform(get("/rider")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].bikerNickname").value(expectedRiderList.get(0).bikerNickname()))
                .andExpect(jsonPath("$.[0].email").value(expectedRiderList.get(0).email()))
                .andExpect(jsonPath("$.[0].city").value(expectedRiderList.get(0).city()))
                .andExpect(jsonPath("$.[0].state").value(expectedRiderList.get(0).state()));

        verify(riderService,times(1)).findAllRider();

    }
}
