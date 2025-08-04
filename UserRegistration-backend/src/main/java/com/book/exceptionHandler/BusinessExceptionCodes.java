package com.book.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

public enum BusinessExceptionCodes {

    NO_CODE(0, NOT_IMPLEMENTED,"No Code"),
    INCORRECT_CURRENT_PASSWORD(310,HttpStatus.BAD_REQUEST,"Current password is incorrect")
    ,NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST,"New password doesn't match")
    ,ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN,"Account Locked"),
    ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN,"Account is disabled"),
    BAD_CREDENTIALS(304,HttpStatus.UNAUTHORIZED,"Bad credentials")

        ;


    BusinessExceptionCodes(int code, HttpStatus httpStatus, String description){
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

}
