package com.book.bookEntity;


import com.book.common.BaseEntity;
import com.book.feedback.Feedback;
import com.book.history.BookTransactionHistory;
import com.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn; //international standard book number
    private String synopsis; // A little desc about the book like sad,education,love etc...
    private String bookCover; //basically it is  path of the cover picture where the image actually is
    private boolean archived;
    private boolean shareable;

    //we know that a book has its owner  he is the only who owns that book
     @ManyToOne
     @JoinColumn(name = "owner_id")
     private User owner;

     //we know that one book can have many feedbacks

     @OneToMany(mappedBy = "book")
     private List<Feedback> feedbacks;

     @OneToMany(mappedBy = "book")
     private List<BookTransactionHistory>bookTransactionHistories;

     //basically this method is used to calculate the rating of the book
     @Transient
    public Double getRate(){
         if(feedbacks==null || feedbacks.isEmpty()){
             return 0.0;
         }
         var rate = this.feedbacks.stream()
                 .mapToDouble(feedback -> feedback.getNote())
                 .average()
                 .orElse(0.0);

         //if the rating is 3.2 --> 3 || if 3.67 ==> 4
         double roundedRate = Math.round(rate)*10.0/10.0;
         return roundedRate;
     }



}
