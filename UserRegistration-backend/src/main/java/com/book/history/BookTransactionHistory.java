package com.book.history;


import com.book.bookEntity.Book;
import com.book.common.BaseEntity;
import com.book.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class BookTransactionHistory extends BaseEntity{

    // user relationship a user can make many transations

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    //Book relationship a book can have many transactions

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    //basically the book transaction history is acting as the layer between the user and the book
    //to check that the book is returned and return approved by the owner or not

    private boolean returned;
    private boolean returnApproved;


}
