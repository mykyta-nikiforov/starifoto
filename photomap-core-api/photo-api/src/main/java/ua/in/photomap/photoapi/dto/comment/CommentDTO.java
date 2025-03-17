package ua.in.photomap.photoapi.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String text;
    private Long userId;
    private String userName;
    private Long photoId;
    private LocalDateTime createdAt;
}
