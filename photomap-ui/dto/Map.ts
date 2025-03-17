import type {PhotoGeoJSONFeature} from "~/dto/Photo";

export interface ClusterLeavesResponse {
    leaves: PhotoGeoJSONFeature[];
}