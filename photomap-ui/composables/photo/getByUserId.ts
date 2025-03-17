import type {AxiosPromise} from "axios";
import type {PhotoDTO} from "~/dto/Photo";
import type {PaginatedResponse} from "~/dto/PaginatedResponse";

export const useGetPhotosByUserId = (userId: number, page?: number, size?: number): AxiosPromise<PaginatedResponse<PhotoDTO>> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.get<PaginatedResponse<PhotoDTO>>(`/photo/user/${userId}?page=${page}&size=${size}`);
}
