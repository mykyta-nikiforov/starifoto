import type {AxiosPromise} from "axios";

export const useDeletePhoto = (photoId: number): AxiosPromise<void> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.delete('/photo/' + photoId);
}
