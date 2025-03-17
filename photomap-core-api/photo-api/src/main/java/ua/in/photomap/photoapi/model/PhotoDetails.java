package ua.in.photomap.photoapi.model;

import java.time.LocalDateTime;
import java.util.Set;

public interface PhotoDetails {
    Long getId();

    String getTitle();

    String getDescription();

    String getAuthor();

    String getSource();

    YearRange getYearRange();


    License getLicense();

    Long getUserId();

    Set<Tag> getTags();

    Coordinates getCoordinates();

    Set<ImageFile> getFiles();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
