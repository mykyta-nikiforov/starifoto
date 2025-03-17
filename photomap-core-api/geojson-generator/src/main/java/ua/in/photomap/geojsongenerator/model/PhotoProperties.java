package ua.in.photomap.geojsongenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoProperties {
    private Long photoId;
    private String iconThumbUrl;
    @Indexed
    private List<String> tags;
    private Short yearStart;
    private Short yearEnd;
}
