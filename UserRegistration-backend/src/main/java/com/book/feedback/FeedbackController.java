package com.book.feedback;

import com.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedbacks")
@Tag(name = "Feedback", description = "APIs related to feedback management") // Tag for grouping feedback-related APIs
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "Save feedback", description = "Allows a user to submit feedback for a book")
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedBackRequest feedBackRequest,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.save(feedBackRequest, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    @Operation(summary = "Get all feedbacks for a book", description = "Fetches all feedbacks related to a specific book with pagination")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }
}
