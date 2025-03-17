<script setup lang="ts">
import {onMounted, onUnmounted, ref} from "vue";
import WebSocket from "@/websocket/WebSocket";
import {PAGE_SIZE} from "@/constants/apiDefaults";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {SNACKBAR_MESSAGES} from "@/constants/messages";
import {usePhotoViewStore} from "@/store/photoViewStore";
import UserList from "@/components/view/admin/UserList.vue";
import {useGetUsers} from "@/composables/user/getUsers";
import {useWebSocketStore} from "@/store/webSocketStore";
import {type UserDTO} from "@/dto/User";

const users = ref<UserDTO[]>([]);
const page = ref(1);
const totalPages = ref(0);

const getUsers = () => {
  useGetUsers(page.value - 1, PAGE_SIZE)
      .then((response) => {
        users.value = response.data.content;
        totalPages.value = response.data.totalPages;
      });
}

const updateUserPage = (newPage: number) => {
  if (newPage >= 0 && newPage <= totalPages.value) {
    page.value = newPage;
    getUsers();
    window.scrollTo({top: 0});
  }
}

onMounted(() => {
  getUsers();
})

const viewWrapper = ref();

const onEditSuccess = () => {
  viewWrapper.value.openSnackbar(SNACKBAR_MESSAGES.USER_SAVED);
  getUsers();
}

const onError = (error: string) => {
  viewWrapper.value.openSystemMessageModal(error);
}

const webSocketStore = useWebSocketStore();

onUnmounted(() => {
  if (webSocketStore.isConnected) {
    WebSocket.unsubscribeFromUserTopic();
  }
  usePhotoViewStore().clearPhotosIds();
});

definePageMeta({
  rolesPermitted: ['Admin']
});
</script>

<template>
  <client-only>
    <GeneralViewWrapper ref="viewWrapper">
      <div class="page-head-wrapper">
        <h1>Користувачі</h1>
      </div>
      <div>
        <UserList :users="users"
                  :page="page"
                  :total-pages="totalPages"
                  @page-changed="updateUserPage"
                  @edit-success="onEditSuccess"
                  @error="onError"
        />
      </div>
    </GeneralViewWrapper>
  </client-only>
</template>

<style lang="less" scoped>
.page-head-wrapper {
  padding: 0 1.5rem;
}
</style>