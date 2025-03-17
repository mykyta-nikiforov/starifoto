package ua.in.photomap.photoapi.service;

import org.springframework.data.domain.Page;
import ua.in.photomap.photoapi.dto.comment.CommentDTO;
import ua.in.photomap.photoapi.model.Photo;

public interface CommentService {

    Page<CommentDTO> findAllByPhotoId(Long photoId, int page, int size);

    void create(Photo photo, String text);

    void delete(Long commentId);

    void deleteAllByPhotoId(Long photoId);
}
