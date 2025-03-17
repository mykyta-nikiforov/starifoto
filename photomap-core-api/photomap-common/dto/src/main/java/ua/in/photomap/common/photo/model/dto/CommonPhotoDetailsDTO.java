package ua.in.photomap.common.photo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonPhotoDetailsDTO {
    private Long id;
    private String title;
    private String description;
    private String source;
    private String author;
    private YearRangeDTO yearRange;
    private Integer licenseId;
    private String licenseName;
    private List<String> tags;
    private String url;
    private String colorizedUrl;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CoordinatesDTO coordinates;
}
