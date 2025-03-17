import type {AxiosPromise} from "axios";
import type {UserProfileDTO} from "~/dto/User";

export const useGetProfileById = (userId: number): AxiosPromise<UserProfileDTO> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.get<UserProfileDTO>(`/user/${userId}`);
}
