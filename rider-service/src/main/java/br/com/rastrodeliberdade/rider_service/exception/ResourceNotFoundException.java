package br.com.rastrodeliberdade.rider_service.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName,Object fieldValue){
        super(String.format("%s não encontrado com %s: '%s'",resourceName,fieldName,fieldValue));
    }

    public ResourceNotFoundException(String resourceName, UUID id){
        super(String.format("%s com ID %s não encontrado.", resourceName, id));
    }
}
