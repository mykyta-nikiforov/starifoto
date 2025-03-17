package ua.in.photomap.geojsongenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "photo-features")
@CompoundIndex(name = "yearStart_yearEnd", def = "{'properties.yearStart': 1, 'properties.yearEnd': 1}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoFeature {

    @Id
    private Long id;
    private GeoJsonPoint geometry;
    private String type = "Feature";
    private PhotoProperties properties;

}