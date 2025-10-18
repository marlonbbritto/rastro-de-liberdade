package br.com.rastrodeliberdade.rider_service.service;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import br.com.rastrodeliberdade.rider_service.exception.BusinessException;
import br.com.rastrodeliberdade.rider_service.exception.ResourceNotFoundException;
import br.com.rastrodeliberdade.rider_service.mapper.RiderMapper;
import br.com.rastrodeliberdade.rider_service.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RiderService {
    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RiderMapper riderMapper;

    public RiderSummaryDto insertRider(RiderInsertDto riderInsertDto){
        if(riderRepository.findByEmail(riderInsertDto.email()).isPresent()){
            throw  new BusinessException("Já existe um  usuário cadastrado com o e-mail: "+riderInsertDto.email());
        }

        if(riderRepository.findByBikerNickname(riderInsertDto.bikerNickname()).isPresent()){
            throw new BusinessException("já existe um usuário cadastrado com o nickname: "+riderInsertDto.bikerNickname());
        }

        Rider newRider = riderMapper.toRider(riderInsertDto);

        newRider.setPassword(passwordEncoder.encode(riderInsertDto.password()));

        Rider savedRider = riderRepository.save(newRider);

        return riderMapper.toSummaryDto(savedRider);
    }

    public List<RiderSummaryDto> findAllRider(){
        List<Rider> riders = riderRepository.findAll();

        return riders.stream()
                .map(riderMapper::toSummaryDto)
                .toList();
    }

    public RiderSummaryDto findById(UUID id){
        Rider rider = riderRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Rider", id));

        return riderMapper.toSummaryDto(rider);
    }

    public  RiderSummaryDto findByEmail(String email){
        Rider rider = riderRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Rider", "e-mail", email));

        return riderMapper.toSummaryDto(rider);
    }

    public List<RiderSummaryDto> findByState(String state){
        List<Rider> riders = riderRepository.findByState(state);

        return riders.stream()
                .map(riderMapper::toSummaryDto)
                .toList();
    }

    @Transactional
    public RiderSummaryDto updateRider(RiderInsertDto riderInsertDto, UUID idToUpdate){
        Rider riderToUpdate = riderRepository.findById(idToUpdate)
                .orElseThrow(()->new ResourceNotFoundException("Rider",idToUpdate));

        validateIfEmailAlreadyRegisteredAnotherRider(riderInsertDto.email(), riderToUpdate);
        validateIfBikerNickNameAlreadyRegisteredAnotherRider(riderInsertDto.bikerNickname(), riderToUpdate);

        riderMapper.updateRiderFromDto(riderInsertDto, riderToUpdate);

        if (riderInsertDto.password() != null && !riderInsertDto.password().isBlank()) {
            riderToUpdate.setPassword(passwordEncoder.encode(riderInsertDto.password()));
        }

        Rider updatedRider = riderRepository.save(riderToUpdate);

        return riderMapper.toSummaryDto(updatedRider);

    }

    private void validateIfEmailAlreadyRegisteredAnotherRider(String email, Rider rider){
        Optional<Rider> existingRider = riderRepository.findByEmail(email);
        if(existingRider.isPresent() && !(existingRider.get().getId().equals(rider.getId()))){
            throw new BusinessException("Já existe um usuario diferente cadastrado com o e-mail: "+email);
        }
    }

    private void validateIfBikerNickNameAlreadyRegisteredAnotherRider(String bikerNickName, Rider rider){
        Optional<Rider> existingRider = riderRepository.findByBikerNickname(bikerNickName);

        if(existingRider.isPresent() && !(existingRider.get().getId().equals(rider.getId()))){
            throw new BusinessException("Já existe um usuario diferente cadastrado com o nickname: "+bikerNickName);
        }
    }


}
