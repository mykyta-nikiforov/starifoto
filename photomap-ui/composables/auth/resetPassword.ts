import type {AxiosPromise} from "axios";

export const useResetPasswordConfirm = (token: string, encryptedPassword: string): AxiosPromise<void> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.post<void>(`/auth/password/reset/confirm`, {token, password: encryptedPassword});
}
