import type {AxiosPromise} from "axios";

export const useUpdateIsEnabled = (userId: number, newValue: boolean): AxiosPromise<void> => {
    const {$axiosAuthenticated} = useNuxtApp();
    const payload = {isEnabled: newValue};
    return $axiosAuthenticated.patch<void>(`/user/${userId}/is-enabled`, payload);
}
