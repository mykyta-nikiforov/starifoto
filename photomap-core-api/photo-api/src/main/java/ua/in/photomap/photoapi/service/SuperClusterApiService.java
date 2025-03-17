package ua.in.photomap.photoapi.service;


import ua.in.photomap.photoapi.dto.supercluster.LeafDTO;
import ua.in.photomap.photoapi.dto.supercluster.PropertiesDTO;

import java.util.List;

public interface SuperClusterApiService {

    List<LeafDTO<PropertiesDTO>> getClusterLeaves(int clusterId, int page, int size, String bbox, List<String> tags,
                                                  List<Long> yearRange);
}
