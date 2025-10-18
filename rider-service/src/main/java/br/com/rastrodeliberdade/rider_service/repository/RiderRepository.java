package br.com.rastrodeliberdade.rider_service.repository;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID> {
    Optional<Rider> findByBikerNickname(String bikerNickname);

    List<Rider> findByState(String state);

    Optional<Rider> findByEmail(String email);

}
