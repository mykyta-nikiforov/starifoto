import type {AuthResponse} from "~/dto/Auth";
import {useAuthStore} from "~/store/authStore";
import {useSystemModalStore} from "~/store/systemModalStore";

export const useRefreshTokens = (onFailShowError: boolean = true) => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.post<AuthResponse>('/auth/refresh')
        .then(response => {
            useAuthStore().setIsRefreshingToken(false);
            const authResponse = response.data;
            if (authResponse.accessToken) {
                const user = authResponse.user;
                const jwt = authResponse.accessToken;
                const expiresIn = authResponse.expiresIn;
                useAuthStore().setAuthInfo(jwt, expiresIn, user);
                useNuxtApp().$locally.setItem('userLoggedIn', 'true');
            }
        })
        .catch((e) => {
            useAuthStore().setIsRefreshingToken(false);
            if (onFailShowError) {
                useSystemModalStore().setErrorMessage('Сесія закінчилась. Будь ласка, увійдіть знову', true);
            }
            return Promise.reject(e);
        });
}
