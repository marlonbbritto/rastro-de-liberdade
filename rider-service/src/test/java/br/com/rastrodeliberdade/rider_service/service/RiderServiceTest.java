package br.com.rastrodeliberdade.rider_service.service;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderAuthDto;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

        assertThat(resultInsertRider.id()).isEqualTo(expectedRiderSummaryDto.id());
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

    @Test
    @DisplayName("Should Return RiderSummaryDto when call findByEmail and everything is ok")
    void findByEmail_ReturnRiderSummary_WhenEverythingIsOK() throws Exception{
        RiderSummaryDto expectedResultRiderSummary = riderMapper.toSummaryDto(existingRider);

        String emailToFind = existingRider.getEmail();

        when(riderRepository.findByEmail(emailToFind)).thenReturn(Optional.ofNullable(existingRider));

        RiderSummaryDto resultRiderSummary = riderService.findByEmail(emailToFind);

        assertThat(resultRiderSummary).isEqualTo(expectedResultRiderSummary);

        verify(riderRepository,times(1)).findByEmail(emailToFind);

    }

    @Test
    @DisplayName("Should Return Resource Not Found Exception when call findByEmail with non existing email")
    void finByEmail_ReturnResourceNotFoundException_WhenEmailNonExisting() throws Exception{
        String emailToFind = "teste";

        when(riderRepository.findByEmail(emailToFind)).thenReturn(Optional.empty());

        assertThatThrownBy(()->riderService.findByEmail(emailToFind))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rider não encontrado com e-mail: '"+emailToFind+"'");

    }

    @Test
    @DisplayName("Should return a list of RiderSummaryDto when call findByState and everything is ok")
    void findByState_ReturnListOfRider_WhenEverythingIsOK() {
        String stateToFind = "Paraná";

        Rider fakeRider1 = Rider.builder()
                .id(UUID.randomUUID())
                .fullName("João Silva")
                .email("joao.silva@test.com")
                .bikerNickname("joao.silva")
                .state(stateToFind)
                .build();

        Rider fakeRider2 = Rider.builder()
                .id(UUID.randomUUID())
                .fullName("Maria Santos")
                .email("maria.santos@test.com")
                .bikerNickname("maria.santos")
                .state(stateToFind)
                .build();

        List<Rider> existingRiderList = List.of(fakeRider1, fakeRider2);
        List<RiderSummaryDto> expectedRiderList = existingRiderList.stream()
                .map(riderMapper::toSummaryDto)
                .toList();

        when(riderRepository.findByState(stateToFind)).thenReturn(existingRiderList);

        List<RiderSummaryDto> resultRiderList = riderService.findByState(stateToFind);

        assertThat(resultRiderList).isNotNull();
        assertThat(resultRiderList.size()).isEqualTo(2);
        assertThat(resultRiderList).isEqualTo(expectedRiderList);

        verify(riderRepository, times(1)).findByState(stateToFind);
    }

    @Test
    @DisplayName("Should return an empty list when call findByState with non-existing state")
    void findByState_ReturnEmptyList_WhenStateNonExisting() {
        String stateToFind = "Estado Inexistente";

        when(riderRepository.findByState(stateToFind)).thenReturn(List.of());

        List<RiderSummaryDto> resultRiderList = riderService.findByState(stateToFind);

        assertThat(resultRiderList).isNotNull();
        assertThat(resultRiderList.isEmpty()).isTrue();

        verify(riderRepository, times(1)).findByState(stateToFind);
    }

    @Test
    @DisplayName("Should return updated Rider when call updateRider and everything is ok")
    void updateRider_ReturnUpdatedRider_WhenEverythingIsOK() {
        UUID idToUpdate = existingRider.getId();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Marlon Britto Updated",
                "marlonb.updated@test.com",
                "marlon.britto.updated",
                "newpassword123",
                "Curitiba",
                "Paraná"
        );

        when(riderRepository.findById(idToUpdate)).thenReturn(Optional.of(existingRider));

        when(riderRepository.findByEmail(riderUpdateDto.email())).thenReturn(Optional.empty());
        when(riderRepository.findByBikerNickname(riderUpdateDto.bikerNickname())).thenReturn(Optional.empty());

        when(riderRepository.save(any(Rider.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RiderSummaryDto resultRiderSummary = riderService.updateRider(riderUpdateDto, idToUpdate);

        assertThat(resultRiderSummary).isNotNull();
        assertThat(resultRiderSummary.id()).isEqualTo(idToUpdate);
        assertThat(resultRiderSummary.bikerNickname()).isEqualTo(riderUpdateDto.bikerNickname());
        assertThat(resultRiderSummary.email()).isEqualTo(riderUpdateDto.email());
        assertThat(resultRiderSummary.city()).isEqualTo(riderUpdateDto.city());
        assertThat(resultRiderSummary.state()).isEqualTo(riderUpdateDto.state());

        verify(riderRepository, times(1)).findById(idToUpdate);
        verify(riderRepository, times(1)).findByEmail(riderUpdateDto.email());
        verify(riderRepository, times(1)).findByBikerNickname(riderUpdateDto.bikerNickname());
        verify(riderRepository, times(1)).save(any(Rider.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when call updateRider with non-existing id")
    void updateRider_ThrowResourceNotFoundException_WhenIdNonExisting() {
        UUID idToUpdate = UUID.randomUUID();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Any Name",
                "any@email.com",

                "any.nick",
                "password",
                "Any City",
                "Any State");

        when(riderRepository.findById(idToUpdate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riderService.updateRider(riderUpdateDto, idToUpdate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rider com ID " + idToUpdate + " não encontrado.");
    }

    @Test
    @DisplayName("Should throw BusinessException when call updateRider with email that belongs to another rider")
    void updateRider_ThrowBusinessException_WhenEmailBelongsToAnotherRider() {
        UUID idToUpdate = existingRider.getId();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Marlon B.",
                "another.user@test.com",
                "marlon.britto.updated",
                "pass",
                "City",
                "State");

        Rider anotherRiderWithEmail = Rider.builder()
                .id(UUID.randomUUID())
                .email(riderUpdateDto.email())
                .build();

        when(riderRepository.findById(idToUpdate)).thenReturn(Optional.of(existingRider));
        when(riderRepository.findByEmail(riderUpdateDto.email())).thenReturn(Optional.of(anotherRiderWithEmail));

        assertThatThrownBy(() -> riderService.updateRider(riderUpdateDto, idToUpdate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um usuario diferente cadastrado com o e-mail: " + riderUpdateDto.email());
    }

    @Test
    @DisplayName("Should throw BusinessException when call updateRider with nickname that belongs to another rider")
    void updateRider_ThrowBusinessException_WhenNicknameBelongsToAnotherRider() {
        UUID idToUpdate = existingRider.getId();
        RiderInsertDto riderUpdateDto = new RiderInsertDto(
                "Marlon B.",
                "marlon.updated@test.com",
                "another.user.nick",
                "pass",
                "City",
                "State");

        Rider anotherRiderWithNickname = Rider.builder()
                .id(UUID.randomUUID())
                .bikerNickname(riderUpdateDto.bikerNickname())
                .build();

        when(riderRepository.findById(idToUpdate)).thenReturn(Optional.of(existingRider));
        when(riderRepository.findByEmail(riderUpdateDto.email())).thenReturn(Optional.empty());
        when(riderRepository.findByBikerNickname(riderUpdateDto.bikerNickname())).thenReturn(Optional.of(anotherRiderWithNickname));

        assertThatThrownBy(() -> riderService.updateRider(riderUpdateDto, idToUpdate))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um usuario diferente cadastrado com o nickname: " + riderUpdateDto.bikerNickname());
    }

    @Test
    @DisplayName("Should complete successfully when call delete with an existing id")
    void delete_ShouldCompleteSuccessfully_WhenIdExists() {
        UUID idToDelete = existingRider.getId();
        when(riderRepository.findById(idToDelete)).thenReturn(Optional.of(existingRider));
        doNothing().when(riderRepository).delete(existingRider);

        assertDoesNotThrow(() -> riderService.delete(idToDelete));

        verify(riderRepository, times(1)).findById(idToDelete);
        verify(riderRepository, times(1)).delete(existingRider);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when call delete with a non-existing id")
    void delete_ThrowResourceNotFoundException_WhenIdNonExisting() {
        UUID idToDelete = UUID.randomUUID();
        when(riderRepository.findById(idToDelete)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riderService.delete(idToDelete))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rider com ID " + idToDelete + " não encontrado.");

        verify(riderRepository, times(1)).findById(idToDelete);
        verify(riderRepository, never()).delete(any(Rider.class));
    }

    @Test
    @DisplayName("Should return RiderAuthDto when findAuthDataByEmail is called with an existing email")
    void findAuthDataByEmail_withExistingEmail_shouldReturnRiderAuthDto() {
        String existingEmail = existingRider.getEmail();


        when(riderRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingRider));


        RiderAuthDto result = riderService.findAuthDataByEmail(existingEmail);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(existingRider.getId());
        assertThat(result.email()).isEqualTo(existingRider.getEmail());
        assertThat(result.password()).isEqualTo(existingRider.getPassword());

        verify(riderRepository, times(1)).findByEmail(existingEmail);

    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when findAuthDataByEmail is called with a non-existing email")
    void findAuthDataByEmail_withNonExistingEmail_shouldThrowResourceNotFoundException() {
        String nonExistingEmail = "ghost@test.com";
        when(riderRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riderService.findAuthDataByEmail(nonExistingEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rider não encontrado com e-mail: '" + nonExistingEmail + "'");
    }


}
