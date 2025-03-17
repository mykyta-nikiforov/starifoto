package ua.in.photomap.photoapi.dto.supercluster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeometryDTO {
    private String type;
    private List<Double> coordinates;
}
