package ua.in.photomap.photoapi.service;

import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;

public interface KafkaService {
    void pushNewPhotoEvent(PhotoGeoJsonDataDTO photoDTO);

    void pushUpdatePhotoEvent(PhotoGeoJsonDataDTO photoDTO);

    void pushDeletePhotoEvent(Long photoId);
}
