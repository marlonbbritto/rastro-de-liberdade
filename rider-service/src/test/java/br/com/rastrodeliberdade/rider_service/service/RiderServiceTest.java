package br.com.rastrodeliberdade.rider_service.service;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import br.com.rastrodeliberdade.rider_service.exception.BusinessException;
import br.com.rastrodeliberdade.rider_service.exception.ResourceNotFoundException;
import br.com.rastrodeliberdade.rider_service.mapper.RiderMapper;
import br.com.rastrodeliberdade.rider_service.repository.RiderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RiderServiceTest {
    @Autowired
    RiderService riderService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RiderMapper riderMapper;

    @MockitoBean
    RiderRepository riderRepository;

    private Rider existingRider;

    @BeforeEach
    void setup(){
        Rider savedRider = Rider.builder()
                .id(UUID.randomUUID())
                .fullName("Marlon Britto")
                .email("marlonb@test.com")
                .bikerNickname("marlon.britto")
                .password(passwordEncoder.encode("12345mudar!"))
                .city("Maringá")
                .state("Paraná")
                .build();

        this.existingRider = savedRider;
    }

    @Test
    @DisplayName("Should return new inserted Rider when call insert and everything is ok")
    void insert_ReturnNewRider_WhenEverythingIsOk() throws Exception{
        RiderInsertDto riderInsertDto = new RiderInsertDto(
                "João Silva",
                "joao.silva@test.com",
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        Rider fakeSavedRider = Rider.builder()
                .id(UUID.randomUUID())
                .fullName(riderInsertDto.fullName())
                .email(riderInsertDto.email())
                .bikerNickname(riderInsertDto.bikerNickname())
                .password(passwordEncoder.encode(riderInsertDto.password()))
                .city(riderInsertDto.city())
                .state(riderInsertDto.state())
                .build();

        RiderSummaryDto expectedRiderSummaryDto = riderMapper.toSummaryDto(fakeSavedRider);

        when(riderRepository.findByEmail(riderInsertDto.email())).thenReturn(Optional.empty());
        when(riderRepository.findByBikerNickname(riderInsertDto.bikerNickname())).thenReturn(Optional.empty());
        when(riderRepository.save(any(Rider.class))).thenReturn(fakeSavedRider);

        RiderSummaryDto resultInsertRider = riderService.insertRider(riderInsertDto);

        assertThat(resultInsertRider).isNotNull();

        assertThat(resultInsertRider.bikerNickname()).isEqualTo(expectedRiderSummaryDto.bikerNickname());
        assertThat(resultInsertRider.email()).isEqualTo(expectedRiderSummaryDto.email());
        assertThat(resultInsertRider.city()).isEqualTo(expectedRiderSummaryDto.city());
        assertThat(resultInsertRider.state()).isEqualTo(expectedRiderSummaryDto.state());

        verify(riderRepository,times(1)).findByEmail(riderInsertDto.email());
        verify(riderRepository,times(1)).findByBikerNickname(riderInsertDto.bikerNickname());
        verify(riderRepository,times(1)).save(any(Rider.class));

    }

    @Test
    @DisplayName("Should return Business Exception when call insert with existing email")
    void insert_ReturnBusinessException_WhenEmailAlreadyExists() throws Exception{
        RiderInsertDto riderInsertDto = new RiderInsertDto(
                "João Silva",
                "joao.silva@test.com",
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        Rider fakeSavedRider = Rider.builder()
                .id(UUID.randomUUID())
                .fullName(riderInsertDto.fullName())
                .email(riderInsertDto.email())
                .bikerNickname(riderInsertDto.bikerNickname())
                .password(passwordEncoder.encode(riderInsertDto.password()))
                .city(riderInsertDto.city())
                .state(riderInsertDto.state())
                .build();

        when(riderRepository.findByEmail(riderInsertDto.email())).thenReturn(Optional.of(fakeSavedRider));

        assertThatThrownBy(()->riderService.insertRider(riderInsertDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um  usuário cadastrado com o e-mail: "+riderInsertDto.email());

    }

    @Test
    @DisplayName("Should return Business Exception when call insert with existing rider nickname")
    void insert_ReturnBusinessException_WhenNickNameAlreadyExists() throws Exception{
        RiderInsertDto riderInsertDto = new RiderInsertDto(
                "João Silva",
                "joao.silva@test.com",
                "joao.silva",
                "12345mudar!",
                "São Paulo",
                "São Paulo"
        );

        Rider fakeSavedRider = Rider.builder()
                .id(UUID.randomUUID())
                .fullName(riderInsertDto.fullName())
                .email(riderInsertDto.email())
                .bikerNickname(riderInsertDto.bikerNickname())
                .password(passwordEncoder.encode(riderInsertDto.password()))
                .city(riderInsertDto.city())
                .state(riderInsertDto.state())
                .build();

        when(riderRepository.findByEmail(riderInsertDto.email())).thenReturn(Optional.empty());
        when(riderRepository.findByBikerNickname(riderInsertDto.bikerNickname())).thenReturn(Optional.of(fakeSavedRider));

        assertThatThrownBy(()->riderService.insertRider(riderInsertDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já existe um usuário cadastrado com o nickname: "+riderInsertDto.bikerNickname());

    }

    @Test
    @DisplayName("Should return an list of riders when call findAll and everything is ok")
    void findAll_ReturnListOfRider_WhenEverythingIsOK(){
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

        List<RiderSummaryDto> expectedRiderList = existingRiderList.stream()
                        .map(riderMapper::toSummaryDto)
                        .toList();

        when(riderRepository.findAll()).thenReturn(existingRiderList);

        List<RiderSummaryDto> resultRiderList = riderService.findAllRider();

        assertThat(resultRiderList).isEqualTo(expectedRiderList);

        verify(riderRepository,times(1)).findAll();

    }

    @Test
    @DisplayName("Should Return RiderSummaryDto when call findById and everything is ok")
    void findById_ReturnRiderSummary_WhenEverythingIsOK() throws Exception{
        RiderSummaryDto expectedResultRiderSummary = riderMapper.toSummaryDto(existingRider);

        UUID idToFind = existingRider.getId();

        when(riderRepository.findById(idToFind)).thenReturn(Optional.ofNullable(existingRider));

        RiderSummaryDto resultRiderSummary = riderService.findById(idToFind);

        assertThat(resultRiderSummary).isEqualTo(expectedResultRiderSummary);

        verify(riderRepository,times(1)).findById(idToFind);
    }

    @Test
    @DisplayName("Should Return Resource Not Found Exception when call findById with non existing id")
    void finById_ReturnResourceNotFoundException_WhenIdNonExisting() throws Exception{
        UUID idToFind = UUID.randomUUID();

        when(riderRepository.findById(idToFind)).thenReturn(Optional.empty());

        assertThatThrownBy(()->riderService.findById(idToFind))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rider com ID "+idToFind+" não encontrado.");

    }
}
