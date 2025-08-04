package com.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepo extends JpaRepository<BookTransactionHistory,Integer> {

    //this query basically returns only the borrowed books by user based on his is
    @Query(
            """
                SELECT history
                FROM BookTransactionHistory AS history
                WHERE history.user.id = :userId
          """
    )

    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query(
            """
                SELECT history
                FROM BookTransactionHistory AS history
                WHERE history.book.owner.id = :userId
            """
    )
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);


    //basically this query will return the count greater than zero if the book is already borrowed
    @Query(
            """
            SELECT (count(*) >0) AS isBorrowed
            FROM BookTransactionHistory as bookTransactionHistory 
            WHERE
            bookTransactionHistory.user.id = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved=false
            """
    )
    boolean isAlreadyborrowedByUser(Integer bookId, Integer userId);

    @Query(
            """
      SELECT bookTransactionHistory
      FROM BookTransactionHistory bookTransactionHistory
      WHERE 
      bookTransactionHistory.user.id = :userId
      AND bookTransactionHistory.book.id = :bookId
      AND bookTransactionHistory.returned = false
          """
    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer id);

    @Query(
            """
            SELECT bookTransactionHistory
            from BookTransactionHistory bookTransactionHistory
            where bookTransactionHistory.book.owner.id = :userId
            and bookTransactionHistory.book.id = :bookId
            and bookTransactionHistory.returned = true
            and bookTransactionHistory.returnApproved=false
            """
    )
    Optional<BookTransactionHistory> findByBookOwnerIdAndUserId(Integer bookId, Integer id);
}
