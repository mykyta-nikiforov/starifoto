import type {AxiosPromise} from "axios";
import type {UpdateUserPayload, UserDTO} from "~/dto/User";

export const useUpdateUser = (userId: number, request: UpdateUserPayload): AxiosPromise<UserDTO> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.put(`/user/${userId}`, request);
}
