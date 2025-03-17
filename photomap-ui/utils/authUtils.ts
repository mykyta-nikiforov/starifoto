import {useAuthStore} from '@/store/authStore';
import type {AuthResponse} from "@/dto/Auth";
import {useSystemModalStore} from "~/store/systemModalStore";

export function handleSuccessAuth(authResponse: AuthResponse) {
    const user = authResponse.user;
    const jwt = authResponse.accessToken;
    const expiresIn = authResponse.expiresIn;

    useAuthStore().setAuthInfo(jwt, expiresIn, user);
    useNuxtApp().$locally.setItem('userLoggedIn', 'true');
    useRouter().push(`/user/${user.id}`);
}
