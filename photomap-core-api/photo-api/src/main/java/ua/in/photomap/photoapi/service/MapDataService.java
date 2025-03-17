package ua.in.photomap.photoapi.service;

import ua.in.photomap.photoapi.dto.supercluster.DetailedPropertiesDTO;
import ua.in.photomap.photoapi.dto.supercluster.LeafDTO;

import java.util.List;

public interface MapDataService {

    void regenerateGeoJsonData();

    List<LeafDTO<DetailedPropertiesDTO>> getClusterLeaves(int clusterId, int page, int size, String bbox,
                                                          List<String> tags, List<Long> yearRange);
}
