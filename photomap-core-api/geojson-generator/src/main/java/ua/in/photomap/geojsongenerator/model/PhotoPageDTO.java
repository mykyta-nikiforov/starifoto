package ua.in.photomap.geojsongenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoPageDTO {
    private List<PhotoGeoJsonDataDTO> content;
    private Integer number;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
}
