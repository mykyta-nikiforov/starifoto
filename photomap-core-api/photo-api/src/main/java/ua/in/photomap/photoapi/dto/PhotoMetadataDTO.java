package ua.in.photomap.photoapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.common.photo.model.dto.CoordinatesDTO;
import ua.in.photomap.common.photo.model.dto.YearRangeDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoMetadataDTO {
    private String title;
    private String description;
    private String author;
    private String source;
    private YearRangeDTO yearRange;
    private Integer licenseId;
    private List<String> tags;
    private CoordinatesDTO coordinates;
}
