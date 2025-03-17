package ua.in.photomap.photoapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.photoapi.model.ImageFileType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoUpdateMetadataDTO extends PhotoMetadataDTO {
    private List<ImageFileType> changedImageTypes;
}
