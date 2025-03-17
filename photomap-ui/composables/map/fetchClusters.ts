import {appendQueryParams} from "~/utils/clusterApiUtils";

export const useFetchClusters = (bbox: number[], zoom: number, tags: string[] = [], yearRange: number[] = []): Promise<any> => {
    const {$clusterAxios} = useNuxtApp();

    let url = `/cluster?bbox=${bbox.join(',')}&zoom=${zoom}`;
    url = appendQueryParams(tags, url, yearRange);
    return $clusterAxios.get(url);
}