import type {AuthResponse, SignUpRequest} from "~/dto/Auth";

export const useSignUp = (request: SignUpRequest) => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.post<AuthResponse>('/auth/sign-up', request);
}
