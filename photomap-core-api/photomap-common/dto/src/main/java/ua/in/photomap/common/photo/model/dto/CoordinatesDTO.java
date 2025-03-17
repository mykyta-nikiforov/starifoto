package ua.in.photomap.common.photo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesDTO {
    private Double latitude;
    private Double longitude;
    private Boolean isApproximate;
}
