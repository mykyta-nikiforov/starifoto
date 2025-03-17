<script setup lang="ts">
import LargeButton from "@/components/buttons/LargeButton.vue";
import {computed, onMounted, onUnmounted, ref, watch} from "vue";
import WebSocket from "@/websocket/WebSocket";
import {useAuthStore} from "@/store/authStore";
import {useWebSocketStore} from "@/store/webSocketStore";
import UserPhotosGallery from "@/components/user/UserPhotosGallery.vue";
import {PAGE_SIZE} from "@/constants/apiDefaults";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {SNACKBAR_MESSAGES} from "@/constants/messages";
import {usePhotoViewStore} from "@/store/photoViewStore";
import {useGetPhotosByUserId} from "@/composables/photo/getByUserId";
import {useGetProfileById} from "@/composables/user/getProfileById";
import {type PhotoDTO} from "@/dto/Photo";
import PhotoFormModal from "@/components/modal/photoform/PhotoFormModal.vue";
import userProfileIcon from "@/assets/icons/profile.svg";
import {formatToDate} from "../../utils/dateUtils";

const route = useRoute();
const authStore = useAuthStore();

const page = useState('user-photos-page', () => 1);
const userId = ref(Number(route.params.userId));

watch(() => route.params.userId, (newUserId) => {
  if (newUserId) {
    userId.value = Number(newUserId);
  }
});

const {data: user} = useAsyncData('user', async () => {
  const res = await useGetProfileById(userId.value);
  return res.data
});

const {
  data: userPhotosPage,
  refresh: refreshPhotos
} = useAsyncData('user-photos', async () => {
      const res = await useGetPhotosByUserId(userId.value, page.value - 1, PAGE_SIZE);
      return res.data;
    },
    {
      watch: [page]
    }
);
const photos = computed(() => userPhotosPage.value?.content);
const totalPages = computed(() => userPhotosPage.value?.totalPages);

const webSocketStore = useWebSocketStore();
watch(() => webSocketStore.isConnected, (newVal) => {
  if (newVal) {
    subscribeToWebSocketUserTopic();
  }
});

onMounted(async () => {
  if (!userPhotosPage.value) {
    refreshPhotos();
  }
  updateRouteBack();
  if (webSocketStore.isConnected) {
    subscribeToWebSocketUserTopic();
  }
  if (photos.value) {
    usePhotoViewStore().setPhotosIds(photos.value.map(photo => photo.id));
  }
});

const clear = () => {
  page.value = 1;
}

const getPhotos = async () => {
  refreshPhotos();
}

const updateRouteBack = () => {
  usePhotoViewStore().setRouteBack(`/user/${userId.value}`);
}

const updatePhotoPage = (newPage: number) => {
  if (newPage >= 0 && totalPages.value && newPage <= totalPages.value) {
    page.value = newPage;
    getPhotos();
    window.scrollTo({top: 0});
  }
}

watch(photos, (newPhotos) => {
  if (newPhotos) {
    usePhotoViewStore().setPhotosIds(newPhotos.map(photo => photo.id));
  }
});

onUnmounted(() => {
  if (webSocketStore.isConnected && isMyPage.value) {
    unsubscribeFromWebSocketUserTopic();
  }
  clear();
  usePhotoViewStore().clear();
});

const photoViewStore = usePhotoViewStore();
watch(() => photoViewStore.deletedPhotoId, (deletedPhotoId) => {
  if (deletedPhotoId) {
    refreshPhotos();
  }
});

const onMessage = (message: any) => {
  // TODO get userId from message
  if (!!userId.value) {
    getPhotos();
  }
}

const subscribeToWebSocketUserTopic = () => {
  if (isMyPage.value) {
    WebSocket.subscribeToUserTopic(WebSocket.client, userId.value, onMessage);
  }
}

const unsubscribeFromWebSocketUserTopic = () => {
  WebSocket.unsubscribeFromUserTopic();
}

const isMyPage = computed(() => userId.value === authStore.user?.id);

const modal = ref();
const openPhotoFormModal = () => {
  modal.value.openModal()
}

const viewWrapper = ref();

const onUploadSuccess = () => {
  getPhotos();
  viewWrapper.value.openSnackbar(SNACKBAR_MESSAGES.PHOTO_UPLOADED);
}

const onEditSuccess = () => {
  viewWrapper.value.openSnackbar(SNACKBAR_MESSAGES.PHOTO_EDITED);
  getPhotos();
}

const onDeleteSuccess = () => {
  viewWrapper.value.openSnackbar(SNACKBAR_MESSAGES.PHOTO_DELETED);
  getPhotos();
}

const onError = (error: string) => {
  viewWrapper.value.openSystemMessageModal(error);
}

const pageTitle = computed(() => {
  return user.value ? user.value.username : 'Користувач';
});

useHead({
  title: pageTitle
});
</script>

<template>
  <GeneralViewWrapper ref="viewWrapper" :max-width-wrapper="false">
    <div class="user-photos-view-container">
      <div class="page-head-wrapper" v-if="user && userPhotosPage">
        <div class="page-head-container max-width-wrapper">

          <div class="user-info">
            <div class="user-name-wrapper">
              <img :src="userProfileIcon"/>
              <h2 class="user-name">{{ user.username }}</h2>
            </div>
            <div class="user-stats-wrapper">
              <img :src="userProfileIcon"/>
              <div class="user-stats">
                <div><span>{{formatToDate(user.createdAt)}}</span> — дата реєстрації</div>
                <div><span>{{userPhotosPage.totalElements}}</span> — завантажено фото</div>
              </div>
            </div>
          </div>

          <div v-if="isMyPage">
            <div class="photomap-button-container">
              <LargeButton
                  :label="'Додати фото'"
                  @click="openPhotoFormModal"/>
            </div>
            <PhotoFormModal ref="modal"
                            :is-edit="false"
                            @submit-success="onUploadSuccess"/>
          </div>
        </div>
      </div>
      <div class="user-photos-view-content max-width-wrapper">
        <div v-if="!!photos && !!totalPages">
          <UserPhotosGallery :photos="photos"
                             :page="page"
                             :isMyPage="isMyPage"
                             :total-pages="totalPages"
                             @page-changed="updatePhotoPage"
                             @edit-success="onEditSuccess"
                             @delete-success="onDeleteSuccess"
                             @error="onError"
          />
        </div>
      </div>
    </div>
  </GeneralViewWrapper>
</template>

<style lang="less" scoped>
.page-head-wrapper {
  width: 100vw;
  background-color: #F9FAFB;
  border-bottom: 1px solid #D1D5DB;
  display: flex;
  justify-content: center;

  .page-head-container {
    padding: 0 1rem 1rem;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    gap: 1rem;

    @media (min-width: 600px) {
      flex-direction: row;
    }

    .user-info {


      .user-stats-wrapper,
      .user-name-wrapper {
        display: flex;
        flex-direction: row;
        gap: 1.25rem;
        align-items: center;

        img {
          width: 20px;
          height: 26px;
        }
      }

      .user-stats-wrapper {
        img {
          opacity: 0;
        }
      }

      .user-stats {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        font-size: 0.875rem;
        color: #6B7280;

        span {
          color: black;
          font-weight: 500;
        }
      }
    }

  }

}

.photomap-button-container {
  margin: 1rem 0 1rem 2.5rem;

  @media (min-width: 500px) {
    margin: 2rem 0 0 0;
  }
}


.user-photos-view-content {
  margin: 0 auto;
}

.user-photos-view-container {
  max-width: 100vw;
}
</style>