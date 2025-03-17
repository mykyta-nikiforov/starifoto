package ua.in.photomap.photoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "image_file")
@Getter
@Setter
public class ImageFile extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "height")
    private Short height;

    @Column(name = "width")
    private Short width;

    @Column(name = "image_phash")
    private String imagePHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private ImageFileType fileType;
}
