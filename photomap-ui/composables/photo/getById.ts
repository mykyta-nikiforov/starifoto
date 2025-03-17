import type {AxiosPromise} from "axios";
import type {PhotoDetailsDTO} from "~/dto/Photo";

export const useGetPhotoById = (photoId: number): AxiosPromise<PhotoDetailsDTO> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.get<PhotoDetailsDTO>(`/photo/${photoId}`);
}
