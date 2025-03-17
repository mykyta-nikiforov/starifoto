package ua.in.photomap.photoapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.common.photo.model.dto.CommonPhotoDetailsDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPhotoDTO extends CommonPhotoDetailsDTO {
    private PhotoStatus status;
}
