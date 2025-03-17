package ua.in.photomap.photoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.in.photomap.photoapi.model.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.photo.id = :photoId " +
            "order by c.createdAt desc")
    Page<Comment> findAllByPhotoId(Long photoId, Pageable pageable);
    Optional<Comment> findByIdAndPhotoId(Long id, Long photoId);

    @Query("delete from Comment c where c.photo.id = :photoId")
    @Modifying(clearAutomatically = true)
    void deleteAllByPhotoId(Long photoId);
}
