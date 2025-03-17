package ua.in.photomap.photoapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoSitemapDataDTO {
    private Long id;
    private LocalDateTime updatedAt;
    private String url;
}
