package com.eazybytes.loans.exception;

import com.eazybytes.loans.dto.ErrorResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException extends RuntimeException{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception exception,
                                                             WebRequest webRequest){
        ErrorResponseDto errorResponseDto=new ErrorResponseDto(webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException,
                                                          WebRequest webRequest){
        ErrorResponseDto errorResponseDto=new ErrorResponseDto(webRequest.getDescription(false),
                HttpStatus.NOT_FOUND,
                resourceNotFoundException.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponseDto);
    }

    @ExceptionHandler(LoanAlreadyExistsException.class)
    public ResponseEntity<Object> LoanAlreadyExistsException(LoanAlreadyExistsException loanAlreadyExistsException,
                                                                  WebRequest webRequest){
        ErrorResponseDto errorResponseDto=new ErrorResponseDto(webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                loanAlreadyExistsException.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }
}
