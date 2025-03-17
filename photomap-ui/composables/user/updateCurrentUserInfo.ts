import type {AxiosPromise} from "axios";
import type {UpdateUserInfoRequest} from "~/dto/user/Settings";

export const useUpdateCurrentUserInfo = (request: UpdateUserInfoRequest): AxiosPromise<void> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.patch<void>('/user/me/account', request);
}
