import type {AuthResponse, GoogleAuthRequest} from "~/dto/Auth";

export const useGoogleAuth = (credential: string) => {
    const {$axiosUnauthenticated} = useNuxtApp();
    const requestPayload: GoogleAuthRequest = {
        credential,
    };
    return $axiosUnauthenticated.post<AuthResponse>('/auth/google', requestPayload);
}
