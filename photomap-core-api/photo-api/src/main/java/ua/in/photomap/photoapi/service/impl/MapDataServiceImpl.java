package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.in.photomap.photoapi.dto.supercluster.DetailedPropertiesDTO;
import ua.in.photomap.photoapi.dto.supercluster.LeafDTO;
import ua.in.photomap.photoapi.dto.supercluster.PropertiesDTO;
import ua.in.photomap.photoapi.repository.PhotoRepository;
import ua.in.photomap.photoapi.service.SuperClusterApiService;
import ua.in.photomap.photoapi.service.MapDataService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapDataServiceImpl implements MapDataService {
    private final SuperClusterApiService superClusterApiService;
    private final PhotoRepository photoRepository;
    private final RestTemplate restTemplate;

    @Value("${photomap.geojson-generator.base-url}")
    private String geoJsonGeneratorBaseUrl;

    @Override
    public void regenerateGeoJsonData() {
        restTemplate.postForEntity(geoJsonGeneratorBaseUrl + "/internal/geojson/regenerate", null, Void.class);
    }

    @Override
    public List<LeafDTO<DetailedPropertiesDTO>> getClusterLeaves(int clusterId, int page, int size, String bbox,
                                                                 List<String> tags, List<Long> yearRange) {
        List<LeafDTO<PropertiesDTO>> clusterLeaves = superClusterApiService.getClusterLeaves(clusterId, page, size, bbox, tags, yearRange);
        List<Long> photoIds = clusterLeaves.stream()
                .map(LeafDTO::getProperties)
                .map(PropertiesDTO::getPhotoId)
                .toList();
        Map<Long, DetailedPropertiesDTO> photoDetailsMap = photoRepository.findAllGalleryDetailsByIds(photoIds)
                .stream()
                .collect(Collectors.toMap(DetailedPropertiesDTO::getPhotoId, Function.identity()));

        return clusterLeaves.stream()
                .map(leaf -> {
                    DetailedPropertiesDTO photoDetails = photoDetailsMap.get(leaf.getProperties().getPhotoId());
                    return new LeafDTO<>(leaf.getGeometry(), leaf.getType(), photoDetails);
                })
                .filter(leaf -> leaf.getProperties() != null)
                .collect(Collectors.toList());
    }
}
