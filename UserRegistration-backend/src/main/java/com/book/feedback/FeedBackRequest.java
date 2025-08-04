package com.book.feedback;


import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;

public record FeedBackRequest (

    @Positive(message = "200")
    @Min(value = 0,message = "201")
    @Max(value = 5,message = "20")
    double note,//it is same as rating 0-5

    @NotNull(message = "203")
    @NotEmpty(message = "203")
    @NotBlank(message = "203")
    String comment,

    @NotNull(message = "204")
    Integer bookId){

}
