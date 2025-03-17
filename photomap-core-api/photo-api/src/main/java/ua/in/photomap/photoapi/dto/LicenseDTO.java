package ua.in.photomap.photoapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenseDTO {
    private Integer id;
    private String name;
    private String description;
    private String detailsUrl;
}
