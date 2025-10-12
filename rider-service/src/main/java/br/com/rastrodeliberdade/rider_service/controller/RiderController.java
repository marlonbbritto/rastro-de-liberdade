package br.com.rastrodeliberdade.rider_service.controller;


import br.com.rastrodeliberdade.rider_service.dto.RiderInsertDto;
import br.com.rastrodeliberdade.rider_service.dto.RiderSummaryDto;
import br.com.rastrodeliberdade.rider_service.service.RiderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/rider")
public class RiderController {
    @Autowired
    private RiderService riderService;

    @Operation(summary = "Create a new Rider",
            description = "Endpoint to register a new Rider in the system")
    @ApiResponse(responseCode = "201", description = "Success to create a new Rider",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RiderSummaryDto.class)))
    @PostMapping
    public ResponseEntity<RiderSummaryDto> insert(@Valid @RequestBody RiderInsertDto riderInsertDto){
        RiderSummaryDto resultNewRiderDto = riderService.insertRider(riderInsertDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultNewRiderDto);

    }

    @Operation(summary = "Find All Riders registered",
            description = "Endpoint to find all Riders registered in the system")
    @ApiResponse(responseCode = "200", description = "Success to find all Riders",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RiderSummaryDto.class))))
    @GetMapping
    public  ResponseEntity<List<RiderSummaryDto>> findAll(){
        List<RiderSummaryDto> riderSummaryDtoList = riderService.findAllRider();

        return ResponseEntity.status(HttpStatus.OK).body(riderSummaryDtoList);
    }

    @Operation(summary = "Find Rider by Id",
            description = "Endpoint find Rider by Id in the system")
    @ApiResponse(responseCode = "200", description = "Success to find Rider",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RiderSummaryDto.class)))
    @GetMapping(value = "/{id}")
    public ResponseEntity<RiderSummaryDto> findById(@PathVariable UUID id){
        RiderSummaryDto resultRider = riderService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(resultRider);
    }

    @Operation(summary = "Find Rider by email",
            description = "Endpoint find Rider by email in the system")
    @ApiResponse(responseCode = "200", description = "Success to find Rider",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RiderSummaryDto.class)))
    @GetMapping(value = "/search/by-email")
    public ResponseEntity<RiderSummaryDto> findByEmail(@RequestParam String email){
        RiderSummaryDto resultRider = riderService.findByEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(resultRider);
    }
}
