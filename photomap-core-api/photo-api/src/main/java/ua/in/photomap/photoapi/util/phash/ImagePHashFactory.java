package ua.in.photomap.photoapi.util.phash;

import org.springframework.stereotype.Component;

@Component
public class ImagePHashFactory {

    public ImagePHash getInstance() {
        return new ImagePHash();
    }
}