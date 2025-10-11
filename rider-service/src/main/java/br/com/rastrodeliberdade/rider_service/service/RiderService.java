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

import java.util.List;
import java.util.UUID;

@Service
public class RiderService {
    @Autowired
    private RiderRepository riderRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

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

//        newRider.setPassword(passwordEncoder.encode(riderInsertDto.password()));

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


}
