import {appendQueryParams} from "~/utils/clusterApiUtils";
import {PAGE_SIZE} from "~/constants/apiDefaults";
import type {ClusterLeavesResponse} from "~/dto/Map";
import type {AxiosPromise} from "axios";

export const useFetchClusterLeaves = (bbox: number[], selectedClusterId: number, page: number = 0, size: number = PAGE_SIZE,
                                      tags: string[], yearRange: number[]): AxiosPromise<ClusterLeavesResponse> => {
    const {$axiosUnauthenticated} = useNuxtApp();

    let url = `/photo/map/cluster/${selectedClusterId}/leaves?page=${page}&size=${size}&bbox=${bbox.join(',')}`;
    url = appendQueryParams(tags, url, yearRange);
    return $axiosUnauthenticated.get(url);
}