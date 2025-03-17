package ua.in.photomap.geojsongenerator.converter;

import lombok.experimental.UtilityClass;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import ua.in.photomap.geojsongenerator.model.PhotoFeature;
import ua.in.photomap.geojsongenerator.model.PhotoProperties;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;

@UtilityClass
public class PhotoFeatureConverter {
    public static PhotoFeature convertToFeature(PhotoGeoJsonDataDTO photoDetailsDTO) {
        PhotoFeature result = new PhotoFeature();
        result.setId(photoDetailsDTO.getPhotoId());

        GeoJsonPoint geometry = new GeoJsonPoint(photoDetailsDTO.getCoordinates().getLongitude(),
                photoDetailsDTO.getCoordinates().getLatitude());
        result.setGeometry(geometry);

        PhotoProperties properties = new PhotoProperties();
        properties.setPhotoId(photoDetailsDTO.getPhotoId());
        properties.setIconThumbUrl(photoDetailsDTO.getIconThumbUrl());
        properties.setTags(photoDetailsDTO.getTags());
        properties.setYearStart(photoDetailsDTO.getYearRange().getStart());
        properties.setYearEnd(photoDetailsDTO.getYearRange().getEnd());
        result.setProperties(properties);
        return result;
    }
}
