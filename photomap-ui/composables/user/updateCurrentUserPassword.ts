import type {AxiosPromise} from "axios";
import type {UpdateUserPasswordRequest} from "~/dto/user/Settings";

export const useUpdateCurrentUserPassword = (request: UpdateUserPasswordRequest): AxiosPromise<void> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.patch<void>('/user/me/password', request);
}
