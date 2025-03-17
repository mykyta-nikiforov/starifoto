import type {AxiosPromise} from "axios";
import type {PhotoDetailsDTO, PhotoDTO} from "~/dto/Photo";
import type {PaginatedResponse} from "~/dto/PaginatedResponse";

export const useGetPhotosPage = (page?: number, size?: number): AxiosPromise<PaginatedResponse<PhotoDetailsDTO>> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.get<PaginatedResponse<PhotoDetailsDTO>>(`/photo/all?page=${page}&size=${size}`);
}
