package com.fallt.qafordevs_reactive.exception;

import com.fallt.qafordevs_reactive.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DeveloperWithEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmailException(Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .errorCode("DEVELOPER_DUPLICATE_EMAIL")
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public ResponseEntity<ErrorDto> handleDeveloperNotFoundException(Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .errorCode("DEVELOPER_NOT_FOUND")
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
