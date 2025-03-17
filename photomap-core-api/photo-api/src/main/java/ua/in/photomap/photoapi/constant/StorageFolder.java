package ua.in.photomap.photoapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageFolder {
    PHOTOS("photo/"),
    THUMBNAILS("thumb/");

    private final String path;
}
