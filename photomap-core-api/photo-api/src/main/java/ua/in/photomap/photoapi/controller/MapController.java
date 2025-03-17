package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.in.photomap.photoapi.dto.supercluster.ClusterLeavesResponse;
import ua.in.photomap.photoapi.dto.supercluster.DetailedPropertiesDTO;
import ua.in.photomap.photoapi.dto.supercluster.LeafDTO;
import ua.in.photomap.photoapi.service.MapDataService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photo/map")
@Tag(name = "Map API", description = "API used by map.")
public class MapController {
    private final MapDataService mapDataService;

    @GetMapping("/cluster/{clusterId}/leaves")
    @Operation(summary = "Get leaves of a cluster. This is a proxy endpoint to Node.js supercluster backend. " +
            "Needed to provide additional data.")
    public ClusterLeavesResponse<DetailedPropertiesDTO> getClusterLeaves(
            @Parameter(description = "Cluster Id", example = "1", required = true) @PathVariable int clusterId,
            @Parameter(description = "Page number", example = "0", required = true) @RequestParam int page,
            @Parameter(description = "Page size", example = "24", required = true) @RequestParam int size,
            @Parameter(description = "Bounding box of a map", example = "22.137,44.379,40.227,52.379", required = true)
            @RequestParam String bbox,
            @Parameter(description = "Tags to filter by", example = "['люди', 'авто']")
            @RequestParam(required = false) List<String> tags,
            @Parameter(description = "Year range to filter by", example = "[2010, 2020]")
            @RequestParam(required = false) List<Long> yearRange) {
        List<LeafDTO<DetailedPropertiesDTO>> clusters = mapDataService.getClusterLeaves(clusterId, page, size, bbox, tags, yearRange);
        return new ClusterLeavesResponse<>(clusters);
    }
}
