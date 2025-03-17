package ua.in.photomap.photoapi.dto.supercluster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailedPropertiesDTO {
    private Long photoId;
    private String title;
    private String url;
    private String colorizedUrl;
    private Short width;
    private Short height;
}
