package com.book.feedback;

import com.book.bookEntity.Book;
import com.book.bookEntity.BookRepository;
import com.book.common.PageResponse;
import com.book.exception.OperationNotPermittedException;
import com.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;


    public Integer save(FeedBackRequest request,
                        Authentication connectedUser) {

        //firstly we have to check the book is available in the database
        Book book = bookRepository.findById(request.bookId()).orElseThrow(
                () -> new EntityNotFoundException("Book is not found with this id"+request.bookId())
        );

        //check if the book is archived or not-sharable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot give feedback to an archived or non shareable book..");
        }

        User user = (User)connectedUser.getPrincipal();
        //the owner of the book cannot borrow the same book
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }

        Feedback feedback = feedbackMapper.toFeedBack(request);
        return feedbackRepository.save(feedback).getId();
    }


    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId,
                                                                 int page,
                                                                 int size,
                                                                 Authentication connectedUser)
    {
        Pageable pageable = PageRequest.of(page,size);
        User user = (User)connectedUser.getPrincipal();
        Page<Feedback>feedbacks = feedbackRepository.findAllByBookId(bookId,pageable);
        List<FeedbackResponse> feedbackResponse = feedbacks.stream()
                .map(feedback -> FeedbackMapper.toFeedbackResponse(feedback,user.getId()))
                .toList();
        return new PageResponse<FeedbackResponse>(
                feedbackResponse,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
