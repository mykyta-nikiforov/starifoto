package ua.in.photomap.common.photo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhotoGeoJsonDataDTO {
    private CoordinatesDTO coordinates;
    private Long photoId;
    private String iconThumbUrl;
    private List<String> tags;
    private YearRangeDTO yearRange;
}
