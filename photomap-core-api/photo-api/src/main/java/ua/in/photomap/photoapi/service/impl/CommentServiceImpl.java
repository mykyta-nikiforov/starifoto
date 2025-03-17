package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.UserBasicDTO;
import ua.in.photomap.common.rest.toolkit.service.JwtAuthenticatedUserResolver;
import ua.in.photomap.photoapi.dto.comment.CommentDTO;
import ua.in.photomap.photoapi.model.Comment;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.repository.CommentRepository;
import ua.in.photomap.photoapi.service.CommentService;
import ua.in.photomap.photoapi.service.UserApiService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserApiService userApiService;

    @Override
    public Page<CommentDTO> findAllByPhotoId(Long photoId, int page, int size) {
        Page<Comment> comments = commentRepository.findAllByPhotoId(photoId, Pageable.ofSize(size).withPage(page));

        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .toList();
        Map<Long, String> users = userApiService.findAllBasicByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserBasicDTO::getId, UserBasicDTO::getName));
        List<CommentDTO> formattedComments = comments.stream()
                .map(comment -> convertToDTO(comment, users))
                .toList();
        return new PageImpl<>(formattedComments, PageRequest.of(page, size), comments.getTotalElements());
    }

    @Override
    public void create(Photo photo, String text) {
        Comment comment = new Comment();
        comment.setPhoto(photo);
        comment.setUserId(JwtAuthenticatedUserResolver.getCurrentUser().getId());
        comment.setText(text);
        commentRepository.save(comment);
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteAllByPhotoId(Long photoId) {
        commentRepository.deleteAllByPhotoId(photoId);
    }

    private CommentDTO convertToDTO(Comment comment, Map<Long, String> userNames) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setPhotoId(comment.getPhotoId());
        commentDTO.setUserName(userNames.get(comment.getUserId()));
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setText(comment.getText());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        return commentDTO;
    }
}
