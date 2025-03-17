import {useAuthStore} from "~/store/authStore";
import {useWebSocketStore} from "~/store/webSocketStore";
import WebSockets from "~/websocket/WebSocket";

export const useLogout = async () => {
    useNuxtApp().$locally.removeItem('userLoggedIn');
    await useRouter().push('/login');
    const {$axiosUnauthenticated} = useNuxtApp();
    WebSockets.destroyClient();
    useWebSocketStore().setConnected(false);
    useAuthStore().clear();
    return $axiosUnauthenticated.post<void>('/auth/logout');
}
