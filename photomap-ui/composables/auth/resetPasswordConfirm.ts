import type {AxiosPromise} from "axios";

export const useResetPassword = (email: string): AxiosPromise<void> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.post<void>(`/auth/password/reset`, {email});
}
