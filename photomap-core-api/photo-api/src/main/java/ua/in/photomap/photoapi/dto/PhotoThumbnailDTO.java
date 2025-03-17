package ua.in.photomap.photoapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoThumbnailDTO {
    private Long photoId;
    private String iconThumbUrl;
} 