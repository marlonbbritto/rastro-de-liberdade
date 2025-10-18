package br.com.rastrodeliberdade.rider_service.mapper;

import br.com.rastrodeliberdade.rider_service.domain.Rider;
import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RiderMapper {

    RiderSummaryDto toSummaryDto(Rider rider);

    @Mapping(target = "password", ignore = true)
    Rider toRider(RiderInsertDto dto);

    @Mapping(target = "password", ignore = true)
    Rider updateRiderFromDto(RiderInsertDto dto, @MappingTarget Rider entity);
}
