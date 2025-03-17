package ua.in.photomap.photoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "photo")
@Getter
@Setter
@NamedEntityGraph(name = "photo-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("tags"),
                @NamedAttributeNode("files"),
                @NamedAttributeNode("license")})
@NamedEntityGraph(name = "photo-geojson-data-graph",
        attributeNodes = {
                @NamedAttributeNode("tags"),
                @NamedAttributeNode("files") // TODO need only ORIGINAL and THUMBNAIL
        })
public class Photo extends AuditModel implements PhotoDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length = 2048)
    private String description;

    @Column(name = "author")
    private String author;

    @Column(name = "source", length = 2048)
    private String source;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "year_start")),
            @AttributeOverride(name = "end", column = @Column(name = "year_end"))
    })
    private YearRange yearRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private License license;

    @Column(name = "user_id")
    private Long userId;

    @ManyToMany
    @JoinTable(
            name = "photo_tag",
            joinColumns = @JoinColumn(name = "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "longitude")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "is_approximate_location")),
    })
    private Coordinates coordinates;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "photo_image_file",
            joinColumns = @JoinColumn(name = "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "image_file_id")
    )
    private Set<ImageFile> files;

    public Optional<ImageFile> getFile() {
        if (CollectionUtils.isEmpty(files)) {
            return Optional.empty();
        }
        return files.stream()
                .filter(file -> file.getFileType() == ImageFileType.ORIGINAL)
                .findAny();
    }

    public Optional<ImageFile> getFileByType(ImageFileType fileType) {
        if (CollectionUtils.isEmpty(files)) {
            return Optional.empty();
        }
        return files.stream()
                .filter(file -> file.getFileType() == fileType)
                .findAny();
    }
}
