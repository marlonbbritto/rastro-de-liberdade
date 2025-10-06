package br.com.rastrodeliberdade.rider_service.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause){
        super(message,cause);
    }
}
