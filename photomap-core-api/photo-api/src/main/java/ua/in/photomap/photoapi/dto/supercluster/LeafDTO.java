package ua.in.photomap.photoapi.dto.supercluster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeafDTO<T> {

    private GeometryDTO geometry;
    private String type;
    private T properties;
}
