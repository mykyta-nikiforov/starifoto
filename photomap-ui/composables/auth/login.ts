import type {AuthResponse, LoginRequest} from "~/dto/Auth";

export const useLogin = (request: LoginRequest) => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.post<AuthResponse>('/auth/login', request);
}
