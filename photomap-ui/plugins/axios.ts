import axios, {type AxiosInstance, type AxiosRequestConfig} from "axios";
import {useAuthStore} from "~/store/authStore";
import {useRefreshTokens} from "~/composables/auth/refreshTokens";
import {useLogout} from "~/composables/auth/logout";
import {getCsrfToken} from "~/utils/cookiesUtils";

export default defineNuxtPlugin(nuxtApp => {
    const config = useRuntimeConfig();
    const apiUrl = process.client ? config.public.browserBaseApiUrl : config.public.baseApiUrl;
    let axiosDefaultConfig : AxiosRequestConfig = {
        baseURL: `${apiUrl}/api`,
        timeout: Number(config.public.timeout),
        withCredentials: true
    };

    let axiosUnauthenticated: AxiosInstance = axios.create(axiosDefaultConfig);
    let axiosAuthenticated: AxiosInstance = axios.create(axiosDefaultConfig);

    addCsrfTokenInterceptor(axiosUnauthenticated);
    addCsrfTokenInterceptor(axiosAuthenticated);

    axiosAuthenticated.interceptors.response.use(
        response => response,
        error => {
            const originalRequest = error.config;
            if (!error.response) {
                return Promise.reject();
            }

            if (error.response.status === 403 && isUrlIgnoredByResponseInterceptor(originalRequest)) {
                return Promise.reject(error);
            }
            if (error.response && error.response.status === 403 && !useAuthStore().getIsTokenRefreshing) {
                useAuthStore().setIsRefreshingToken(true);
                useAuthStore().clear();
                return useRefreshTokens()
                    .then(() => axiosAuthenticated(originalRequest))
                    .catch((e: any) =>{
                        return useLogout().then(() => {
                            useRouter().push({name: 'login'});
                            return Promise.reject(e);
                        });
                    });
            }
            return Promise.reject(error);
        }
    );

    const clusterAxios = axios.create({
        baseURL: `${config.public.clusterApiRoot}`,
        timeout: Number(config.public.timeout),
    });


    return {
        provide: {
            axiosUnauthenticated: axiosUnauthenticated,
            axiosAuthenticated: axiosAuthenticated,
            clusterAxios: clusterAxios
        }
    }
});

function isUrlIgnoredByResponseInterceptor(originalRequest: any): boolean {
    let url = originalRequest.url;
    return url.includes('/auth/refresh') || url.includes('/auth/login') || url.includes('/auth/google');
}

function addCsrfTokenInterceptor(axiosInstance: AxiosInstance) {
    axiosInstance.interceptors.request.use(
        async (config) => {
            if (config.url && config.url.includes('/csrf')
                ||  config.method && config.method.toLowerCase() === 'get') {
                return config;
            }
            const csrfToken = await getCsrfToken();
            if (csrfToken) {
                config.headers['X-XSRF-TOKEN'] = csrfToken;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );
}