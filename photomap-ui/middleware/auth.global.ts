import {useAuthStore} from "~/store/authStore";
import {useLoaderStore} from "~/store/loaderStore";
import {useRefreshTokens} from "~/composables/auth/refreshTokens";
import {useSystemModalStore} from "~/store/systemModalStore";

export default defineNuxtRouteMiddleware(async (to, from) => {
    const authStore = useAuthStore();
    const loaderStore = useLoaderStore();
    if (!authStore.isLoggedIn && useNuxtApp().$locally.getItem('userLoggedIn')) {
        await useRefreshTokens(false)
            .catch(() => {
                useNuxtApp().$locally.removeItem('userLoggedIn');
            });
    }
    loaderStore.setIsLoaded(true);
    if (import.meta.client) {
        const rolesPermitted: string[] = to.meta.rolesPermitted as string[];
        if (!rolesPermitted) {
            return;
        }
        const userRoles = authStore.user?.roles.map(role => role.name);
        if (rolesPermitted && (!userRoles || !userRoles.some(role => rolesPermitted.includes(role)))) {
            return navigateTo('/login');
        }
    }
});
