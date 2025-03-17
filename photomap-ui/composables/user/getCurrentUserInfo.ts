import type {AxiosPromise} from "axios";
import type {AuthenticatedUser} from "~/dto/User";

export const useGetCurrentUserInfo = (): AxiosPromise<AuthenticatedUser> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.get<AuthenticatedUser>('/user/me');
}
