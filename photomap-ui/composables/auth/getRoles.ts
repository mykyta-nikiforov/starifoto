import type {RoleDTO} from "~/dto/User";
import type {AxiosPromise} from "axios";

export const useGetRoles = (): AxiosPromise<RoleDTO[]> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.get<RoleDTO[]>(`/user/role/all`);
}
