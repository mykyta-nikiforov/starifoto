<script setup lang="ts">

import {computed, onMounted, onUnmounted, ref, watch} from 'vue';
import PhotoMetadata from "@/components/view/photo/PhotoMetadata.vue";
import type {PhotoDetailsDTO} from "@/dto/Photo";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import PhotoPageHeader from "@/components/view/photo/PhotoPageHeader.vue";
import LoaderView from "@/components/loader/LoaderView.vue";
import {parseMarkupToHtml, removeLinksFromText} from "@/utils/markup";
import {usePhotoViewStore} from "@/store/photoViewStore";
import {useAuthStore} from "@/store/authStore";
import {CONFIRMATION_MESSAGES, TITLES} from "@/constants/messages";
import {useGetPhotoById} from "@/composables/photo/getById";
import PhotoComments from "@/components/view/photo/PhotoComments.vue";
import {useDeletePhoto} from "@/composables/photo/deletePhoto";
import ConfirmDialog from "@/components/modal/ConfirmDialog.vue";
import PhotoFormModal from "@/components/modal/photoform/PhotoFormModal.vue";
import VueEasyLightbox from 'vue-easy-lightbox';
import PhotoGeoMap from "../view/photo/PhotoGeoMap.vue";
import {UKRAINE_CENTER_COORDINATES} from "../../constants/map";

const route = useRoute();
const router = useRouter();

const photo = useState<PhotoDetailsDTO>('photo');
const loaderVisible = ref(false);
const loadingFailed = ref(false);
const photoViewStore = usePhotoViewStore();

const emit = defineEmits(['error']);

const routePhotoId = computed(() => {
  return Number(route.params.photoId);
});


onMounted(() => {
  if (!photo.value) {
    getPhotoById();
  }
  if (!photoViewStore.photoId) {
    photoViewStore.setPhotoId(routePhotoId.value);
  }
});

const displayLoader = (id?: number) => {
  if (!id) {
    loaderVisible.value = true;
  } else {
    setTimeout(() => {
      if (!photo.value && currentId.value === id && !loadingFailed.value) {
        loaderVisible.value = true;
      }
    }, 250);
  }
};

const currentId = computed(() => {
  return photoViewStore.photoId;
});

const getPhotoById = async () => {
  const id = routePhotoId.value;
  if (!!id) {
    displayLoader(id);
    await useGetPhotoById(id)
        .then((response) => photo.value = response.data)
        .catch(() => {
          emit('error', 'Не вдалося завантажити фото');
          loadingFailed.value = true;
        })
        .finally(() => loaderVisible.value = false);
  }
};

watch(() => route.params.photoId, (newId) => {
  if (newId) {
    clear();
    photoViewStore.setPhotoId(Number(newId));
    getPhotoById();
  }
});

const clear = () => {
  photo.value = null;
  loaderVisible.value = false;
  loadingFailed.value = false;
  lightboxVisible.value = false;
}

const onPhotoChanged = (newId: number) => {
  useRouter().push('/photo/' + newId);
};

const onPhotoClose = () => {
  router.push(photoViewStore.routeBack);
};

const photoViewContainerClass = computed(() => {
  return {
    'photo-view-container': true,
    'photo-view-container-hidden': !photo.value
  };
});

const mapContainerCoordinates = computed(() => {
  return {
    longitude: photo.value ? photo.value.coordinates.longitude : UKRAINE_CENTER_COORDINATES[0],
    latitude: photo.value ? photo.value.coordinates.latitude : UKRAINE_CENTER_COORDINATES[1]
  }
});

const descriptionAsHtml = computed(() => {
  if (photo.value) {
    return parseMarkupToHtml(photo.value.description);
  }
});

const imageUrl = computed(() => {
  return photoViewStore.showColorized && !!photo.value?.colorizedUrl ? photo.value.colorizedUrl : photo.value?.url;
});

const onColorizedSwitched = (newValue: boolean) => {
  photoViewStore.setShowColorized(newValue);
};

const viewWrapper = ref();
const onCommentDeleted = () => {
  viewWrapper.value.openSnackbar('Коментар видалений');
};

const isGalleryView = computed(() => {
  return !!photoViewStore.photosIds;
});

// Edit photo
const editModal = ref();
const editPhoto = (photo: PhotoDetailsDTO) => {
  if (editModal.value) {
    editModal.value.openModal(photo);
  }
};

const confirm = ref();
const onEditSuccess = () => {
  viewWrapper.value.openSnackbar('Фото відредаговано');
  getPhotoById();
};

const authStore = useAuthStore();
const arePhotoActionButtonsDisplayed = computed(() => {
  return authStore.isModeratorOrHigher || photo.value?.userId === authStore.user?.id;
});

const deleteIconClicked = (photoId: number) => {
  confirm.value.open(TITLES.DELETE_PHOTO, CONFIRMATION_MESSAGES.ARE_YOU_SURE, {color: '#B64038'})
      .then((confirm: any) => {
        if (confirm) {
          deletePhoto(photoId);
        }
      })
};

const deletePhoto = (photoId: number) => {
  useDeletePhoto(photoId)
      .then(() => {
        viewWrapper.value.openSnackbar('Фото видалено');
        // go back
        router.push(photoViewStore.routeBack);
        if (isGalleryView.value) {
          photoViewStore.setDeletedPhotoId(photoId);
        }
      })
      .catch((error) => {
        viewWrapper.value.openSystemMessageModal(error.response.data.message);
      });
};

const lightboxVisible = ref(false);

const comments = ref();
const enableKeyboardNavigation = computed(() => {
  return photo.value != null
      && comments.value != null
      && (editModal.value == null || !editModal.value.modalOpened) && !comments.value.commentInputFocused;
});

await callOnce(async () => {
  await getPhotoById();
  photoViewStore.setPhotoId(routePhotoId.value);
});

onUnmounted(() => {
  clear();
  photoViewStore.clearPhotoId();
});

useSeoMeta
({
  title: () => photo.value?.title,
  ogTitle: () => photo.value?.title,
  description: () => `${removeLinksFromText(photo.value?.description)}. Теги: ${photo.value?.tags.join(', ')}`,
  ogDescription: () => `${removeLinksFromText(photo.value?.description)}. Теги: ${photo.value?.tags.join(', ')}`,
  ogImage: () => photo.value?.url
});

const pageTitle = computed(() => {
  return photo.value?.title;
});

useHead({
  title: pageTitle,
});

onBeforeRouteUpdate((to, from, next) => {
  if (from.meta.backgroundViewParams) {
    to.meta.backgroundViewParams = from.meta.backgroundViewParams;
  }
  next();
});
</script>

<template>
  <div class="photo-view-wrapper">
    <GeneralViewWrapper ref="viewWrapper"
                        :display-footer="!isGalleryView">
      <template v-slot:custom-header v-if="isGalleryView">
        <PhotoPageHeader
            :enableKeyboardNavigation="enableKeyboardNavigation"
            :photosIds="photoViewStore.photosIds"
            @photoChanged="onPhotoChanged"
            @close="onPhotoClose"
        />
      </template>
      <div class="content-wrapper">
        <LoaderView
            :displayed="loaderVisible"
            :message="'Завантажуємо'"
            :background="'transparent'"
        />
        <div :class="photoViewContainerClass">
          <PhotoFormModal ref="editModal"
                          :is-edit="true"
                          @submit-success="onEditSuccess"/>
          <ConfirmDialog ref="confirm"></ConfirmDialog>
          <div v-if="photo" class="photo-file-container">
            <img class="photo"
                 :src="imageUrl"
                 :alt="photo.title"
                 @click="lightboxVisible = true"
            />
            <client-only>
              <vue-easy-lightbox
                  :visible="lightboxVisible"
                  :index="photo.id"
                  :imgs="[imageUrl]"
                  :zoomScale="0.4"
                  :minZoom="0.8"
                  :rotateDisabled="true"
                  @hide="lightboxVisible = false"
              />
            </client-only>

          </div>
          <div class="photo-data-container">
            <div v-if="photo" class="photo-info-block"
                 :style="arePhotoActionButtonsDisplayed ? {paddingTop: '1rem'} : {}">
              <div class="photo-info-top-block">
                <div>
                  <div class="photo-title">{{ photo.title }}</div>
                  <div class="photo-description" v-html="descriptionAsHtml"></div>
                </div>
                <div v-if="arePhotoActionButtonsDisplayed" class="buttons-wrapper">
                  <v-btn class="delete-button" icon="mdi-delete" @click="deleteIconClicked(photo.id)"/>
                  <v-btn class="edit-button" icon="mdi-pencil" @click="editPhoto(photo)"/>
                </div>
              </div>
              <div class="photo-metadata">
                <PhotoMetadata :photo="photo"
                               @colorized-switched="onColorizedSwitched"/>
              </div>
            </div>
            <v-divider/>
            <client-only>
              <div class="geo-container">
                <label>На карті:</label>
                <PhotoGeoMap :latitude="mapContainerCoordinates.latitude"
                             :longitude="mapContainerCoordinates.longitude"/>
                <div class="general-map-link">
                  <small>
                    <NuxtLink :to="{name: 'map', query: {
                      lat: mapContainerCoordinates.latitude,
                      lng: mapContainerCoordinates.longitude
                    }}">
                      Відкрити на карті з усіма фото
                    </NuxtLink>
                  </small>
                </div>
              </div>
            </client-only>
            <client-only>
              <v-divider/>
              <PhotoComments
                  v-if="photo"
                  ref="comments"
                  :photoId="photo.id"
                  @deleted="onCommentDeleted"/>
            </client-only>
          </div>
        </div>
      </div>
    </GeneralViewWrapper>
  </div>
</template>

<style scoped lang="less">
.photo-view-container-hidden {
  opacity: 0;
  height: 50vh;
}

.photo-view-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;

  @media (min-width: 768px) {
    flex-direction: row;
  }

  .photo-file-container, .photo-data-container {
    width: 100%;

    @media (min-width: 768px) {
      width: 50%;
    }
  }
}

.content-wrapper {
  flex: 1; /* Take up all available space */
  display: flex; /* Enable flex layout for children */
  flex-direction: column; /* Stack children vertically */
  position: relative; /* Set relative for LoaderView positioning */
  min-height: 100%;

  :deep(.v-overlay__scrim) {
    background: none;
  }
}

.photo-file-container {
  position: relative;
  flex-shrink: 0;
}

.photo-info-top-block {
  display: flex;
  flex-direction: column-reverse;

  .buttons-wrapper {
    flex-direction: row-reverse;
    margin-bottom: 1rem;
  }

  @media (min-width: 1024px) {
    flex-direction: row;
    justify-content: space-between;
  }
}

.photo-data-container {
  max-width: 728px;
  word-break: break-word;

  .photo-info-block {

    @media (min-width: 576px) {
      padding: 1rem;
    }

    @media (min-width: 768px) {
      padding: 2rem;
    }

    .photo-title {
      font-weight: 500;
      font-size: 1.5rem;
      line-height: 2rem;
    }

    .photo-description {
      margin-top: 0.6rem;
    }

    .photo-metadata {
      margin-top: 2rem;
    }
  }

  .geo-container {
    padding: 1rem 0;
    display: flex;
    flex-direction: column;
    gap: 1rem;

    @media (min-width: 576px) {
      padding: 1rem 1rem;
    }

    @media (min-width: 768px) {
      padding: 1rem 2rem;
    }

    .general-map-link {
      a, a:visited {
        color: black;
      }
    }
  }
}

.photo {
  width: 100%;
  height: auto;
}

.photo:hover {
  cursor: pointer;
}

.photo-view-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow-y: auto;
  z-index: 200;
  background-color: white;
}
</style>
