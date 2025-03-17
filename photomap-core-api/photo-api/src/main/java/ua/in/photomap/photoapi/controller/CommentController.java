package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.in.photomap.common.rest.toolkit.annotation.PreAuthorizeUserOrHigher;
import ua.in.photomap.photoapi.dto.comment.CommentDTO;
import ua.in.photomap.photoapi.dto.comment.CreateCommentDTO;
import ua.in.photomap.photoapi.service.CommentService;
import ua.in.photomap.photoapi.service.PhotoManager;
import ua.in.photomap.photoapi.validator.CommentValidator;

@RestController
@RequestMapping("/api/photo/{photoId}/comment")
@RequiredArgsConstructor
@Validated
@Tag(name = "Comment API", description = "API to work with comments")
public class CommentController {
    private final CommentService commentService;
    private final PhotoManager photoManager;
    private final CommentValidator commentValidator;

    @GetMapping
    @Operation(summary = "Get comments for a photo.")
    public Page<CommentDTO> getComments(
            @Parameter(description = "Photo Id", example = "1", required = true)
            @PathVariable Long photoId,
            @Parameter(required = true) @RequestParam int page,
            @Parameter(required = true) @RequestParam int size) {
        return commentService.findAllByPhotoId(photoId, page, size);
    }

    @PostMapping
    @PreAuthorizeUserOrHigher
    @Operation(summary = "Create a comment for a photo.")
    public ResponseEntity<Void> createComment(
            @Parameter(description = "Photo Id", example = "1", required = true)
            @PathVariable Long photoId,
            @Parameter(description = "Comment data", required = true)
            @RequestBody CreateCommentDTO request) {
        photoManager.addComment(photoId, request.getText());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorizeUserOrHigher
    @Operation(summary = "Delete a comment for a photo.")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Photo Id", example = "1", required = true)
            @PathVariable @NotNull Long photoId,
            @Parameter(description = "Comment Id", example = "1", required = true)
            @PathVariable @NotNull Long commentId) {
        commentValidator.validateDeleteRequest(photoId, commentId);
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }
}
