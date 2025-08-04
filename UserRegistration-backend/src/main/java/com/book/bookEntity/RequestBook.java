package com.book.bookEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//this is a record
public record RequestBook(

        Integer id,
        @NotEmpty(message = "100")
        @NotNull(message = "100")
                @NotBlank(message = "200")
        String title,
        @NotEmpty(message = "101")
        @NotNull(message = "101")
        String authorName,
        @NotEmpty(message = "102")
        @NotNull(message = "102")
        String isbn,
        @NotEmpty(message = "103")
        @NotNull(message = "103")
        String synopsis,

        boolean shareable
){
}
