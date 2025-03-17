import type {AxiosPromise} from "axios";

export const useConfirmEmail = (token: string): AxiosPromise<void> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.post<void>(`/auth/email/confirm`, {token});
}
