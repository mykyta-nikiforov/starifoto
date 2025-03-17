import type {AxiosPromise} from "axios";
import type {UserDTO} from "~/dto/User";
import {PAGE_SIZE} from "~/constants/apiDefaults";
import type {PaginatedResponse} from "~/dto/PaginatedResponse";

export const useGetUsers = (page: number = 0, size: number = PAGE_SIZE): AxiosPromise<PaginatedResponse<UserDTO>> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.get<PaginatedResponse<UserDTO>>(`/user/all?page=${page}&size=${size}`);
}
