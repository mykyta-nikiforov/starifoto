package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.in.photomap.common.rest.toolkit.exception.InternalException;
import ua.in.photomap.photoapi.dto.supercluster.ClusterLeavesResponse;
import ua.in.photomap.photoapi.dto.supercluster.LeafDTO;
import ua.in.photomap.photoapi.dto.supercluster.PropertiesDTO;
import ua.in.photomap.photoapi.service.SuperClusterApiService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperClusterApiServiceImpl implements SuperClusterApiService {

    private final RestTemplate restTemplate;

    @Value("${photomap.supercluster-api.base-url}")
    private String superClusterApiBaseUrl;

    @Override
    public List<LeafDTO<PropertiesDTO>> getClusterLeaves(int clusterId, int page,
                                                         int size,
                                                         String bbox, List<String> tags, List<Long> yearRange) {
        String url = buildClusterLeavesUrl(clusterId, page, size, bbox, tags, yearRange);
        ClusterLeavesResponse<PropertiesDTO> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<ClusterLeavesResponse<PropertiesDTO>>() {}).getBody();
        if (response == null) {
            throw new InternalException("User service did not respond");
        }
        return response.getLeaves();
    }

    private String buildClusterLeavesUrl(int clusterId, int page, int size, String bbox, List<String> tags, List<Long> yearRange) {
        String url = superClusterApiBaseUrl + "/cluster/" + clusterId
                + "/leaves?page=" + page + "&size=" + size + "&bbox=" + bbox;
        if (tags != null) {
            url += "&tags=" + String.join(",", tags);
        }
        if (yearRange != null && yearRange.size() == 2) {
            url += "&yearRange=" + yearRange.get(0) + "," + yearRange.get(1);
        }
        return url;
    }
}
