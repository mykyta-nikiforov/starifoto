import * as StompJs from "@stomp/stompjs";
import {useAuthStore} from "@/store/authStore";
import {useWebSocketStore} from "@/store/webSocketStore";
import {getCsrfToken} from "@/utils/cookiesUtils";

const SUBSCRIPTIONS_IDS = {
    USER_TOPIC: "user-topic-sub"
}

const WebSockets = {
    client: null,
    async createStompJsClient(onConnect) {
        const csrfToken = await getCsrfToken();
        let stompJsClient = new StompJs.Client({
            brokerURL: this.getWebSocketBaseUrl(),
            heartbeatIncoming: 15000,
            heartbeatOutgoing: 15000,
            connectHeaders: {
                Authorization: `Bearer ${useAuthStore().accessToken}`,
                "X-XSRF-TOKEN": csrfToken
            },
            onConnect: onConnect,
            onWebSocketClose: () => {
                useWebSocketStore().setConnected(false);
            },
            onStompError: function () {
                console.log(new Date().toTimeString(), " Stomp error.");
            }
        });
        stompJsClient.activate();
        this.client = stompJsClient;
        return stompJsClient;
    },

    /** Get base URL for connecting to websocket */
    getWebSocketBaseUrl() {
        let websocketUrl = useRuntimeConfig().public.wsUrl;
        if (websocketUrl === undefined) {
            websocketUrl = "wss://" + window.location.host + "/ws";
        }
        return websocketUrl;
    },

    getWebSocketTopicRelativeUrl() {
        return "/topic";
    },

    subscribeToUserTopic(client, userId, onMessage) {
        client.subscribe(this.getWebSocketTopicRelativeUrl() + `/user/${userId}`,
            (message) => {
                onMessage(JSON.parse(message.body));
            },
            {id: SUBSCRIPTIONS_IDS.USER_TOPIC});
    },

    unsubscribeFromUserTopic() {
        this.client?.unsubscribe(SUBSCRIPTIONS_IDS.USER_TOPIC);
    },

    destroyClient() {
        if (this.client) {
            this.client.deactivate();
        }
    }
}

export default WebSockets;
