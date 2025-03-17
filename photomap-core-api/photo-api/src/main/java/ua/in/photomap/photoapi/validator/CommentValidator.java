package ua.in.photomap.photoapi.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;
import ua.in.photomap.common.rest.toolkit.model.JwtAuthenticatedUser;
import ua.in.photomap.common.rest.toolkit.service.JwtAuthenticatedUserResolver;
import ua.in.photomap.photoapi.model.Comment;
import ua.in.photomap.photoapi.repository.CommentRepository;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;

    public void validateDeleteRequest(Long photoId, Long commentId) {
        Comment comment = commentRepository.findByIdAndPhotoId(commentId, photoId)
                .orElseThrow(() -> new ValidationException("Comment not found"));
        JwtAuthenticatedUser currentUser = JwtAuthenticatedUserResolver.getCurrentUser();
        if (!currentUser.isModeratorOrAdmin() &&
                !comment.getUserId().equals(currentUser.getId())) {
            throw new ValidationException("You can delete only your own comments");
        }
    }
}