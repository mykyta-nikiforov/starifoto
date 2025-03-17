<script setup lang="ts">
import type {RouteLocationNormalizedLoaded} from "@/.nuxt/vue-router-stub.d";
import {useWebSocketStore} from "@/store/webSocketStore";
import {useAuthStore} from "@/store/authStore";
import {useLoaderStore} from "@/store/loaderStore";
import WebSocket from "@/websocket/WebSocket.js";
import {useSystemModalStore} from "@/store/systemModalStore";
import SystemMessageModal from "@/components/modal/SystemMessageModal.vue";
import {useSystemSnackbarStore} from "./store/systemSnackbarStore";
import {ref} from "vue";


const loaderStore = useLoaderStore();
const authStore = useAuthStore();
const webSocketStore = useWebSocketStore();
const isConnectingWebSocket = ref(false);

watch(() => authStore.isLoggedIn, (newValue) => {
  if (newValue && !isConnectingWebSocket.value && !webSocketStore.isConnected) {
    connectToWebSocket();
  }
});

onMounted(() => {
  if (authStore.isLoggedIn && !webSocketStore.isConnected) {
    connectToWebSocket();
  }
});

onUnmounted(() => {
  disconnectWebSocket();
});

const onConnectFunction = () => {
  webSocketStore.setConnected(true)
  isConnectingWebSocket.value = false;
};

const connectToWebSocket = async () => {
  const authStore = useAuthStore();
  if (authStore.isLoggedIn) {
    isConnectingWebSocket.value = true;
    await WebSocket.createStompJsClient(onConnectFunction);
  }
}

const disconnectWebSocket = () => {
  WebSocket.destroyClient();
};

const systemModalStore = useSystemModalStore();
const clearError = () => systemModalStore.clear();

const systemSnackbarStore = useSystemSnackbarStore();

const snackbar = ref(false);

const onSnackbarClose = () => {
  snackbar.value = false;
}

watch(() => systemSnackbarStore.message, (newValue) => {
  if (newValue) {
    snackbar.value = true;
  }
});

const getPageKey = (route: RouteLocationNormalizedLoaded) => {
  const componentName = route?.matched[0]?.components?.default.__name;
  if (componentName === 'MapView') {
    return 'map';
  } else if (componentName === 'UserPhotosView') {
    const userId = route.params.userId || route.meta.backgroundViewParams?.userId;
    return `user/${userId}`;
  } else {
    return route.fullPath;
  }
}

useHead
({
  meta: [
    {
      name: 'description',
      content: 'Старі фотографії України на карті, фільтрація за роками і тегами. Карта, старі фото, світлини'
    }
  ]
})
</script>

<template>
  <div>
    <v-snackbar
        v-model="snackbar">
      {{ systemSnackbarStore.message }}
      <template v-slot:actions>
        <v-btn
            icon="mdi-close"
            @click="onSnackbarClose"/>
      </template>
    </v-snackbar>
    <SystemMessageModal v-if="!!systemModalStore.message"
                        :message="systemModalStore.message"
                        :is-error="systemModalStore.isError"
                        @close="clearError"/>
    <NuxtPage :page-key="getPageKey"/>
    <NuxtPage name="modal"/>
    <div v-if="!loaderStore.isLoaded" class="loading-overlay">
      <LoaderGrape/>
    </div>
  </div>
</template>

<style>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: white;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>