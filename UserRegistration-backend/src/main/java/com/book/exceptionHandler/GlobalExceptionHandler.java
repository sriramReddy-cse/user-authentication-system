package com.book.exceptionHandler;

import com.book.exception.OperationNotPermittedException;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalExceptionHandler{

    @ExceptionHandler(LockedException.class)
     public ResponseEntity<ExceptionResponse>exceptionHandler(LockedException lockedException){
         return ResponseEntity.
                 status(HttpStatus.UNAUTHORIZED).
                 body(
                         ExceptionResponse.builder()
                                 .businessErrorCode(BusinessExceptionCodes.ACCOUNT_LOCKED.getCode())
                                 .businessErrorDescription(BusinessExceptionCodes.ACCOUNT_LOCKED.getDescription())
                                 .error(lockedException.getMessage())
                                 .build()
                 );
     }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse>exceptionHandler(DisabledException exp){
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).
                body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessExceptionCodes.ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(BusinessExceptionCodes.ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse>exceptionHandler(BadCredentialsException exp){
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).
                body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessExceptionCodes.BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BusinessExceptionCodes.BAD_CREDENTIALS.getDescription())
                                .error(BusinessExceptionCodes.BAD_CREDENTIALS.getDescription())
                                .build()
                );
    }


    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse>exceptionHandler(MessagingException exp){
        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR).
                body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    //this writting will help us to understand the exception very well
    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse>exceptionHandler(OperationNotPermittedException exp){
        return ResponseEntity.
                status(BAD_REQUEST).
                body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse>exceptionHandler(MethodArgumentNotValidException exp){

        log.info("Came inside....");
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
              String errorMessage = error.getDefaultMessage();
              errors.add(errorMessage);
        });

        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).
                body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }


    //This can catch any exception from the controllers
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse>exceptionHandler(Exception exp){
        exp.printStackTrace();
        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR).
                body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal Server Error")
                                .error(exp.getMessage())
                                .build()
                );
    }
}
