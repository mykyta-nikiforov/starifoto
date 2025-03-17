<script setup lang="ts">
import DotsLoader from "@/components/loader/DotsLoader.vue";
import {computed, defineEmits, ref, watch} from "vue";
import NoContent from "@/components/common/NoContent.vue";
import {CONFIRMATION_MESSAGES, TITLES} from "@/constants/messages";
import {usePhotoViewStore} from "@/store/photoViewStore";
import {useAuthStore} from "@/store/authStore";
import {type PhotoDTO} from "@/dto/Photo";
import ConfirmDialog from "@/components/modal/ConfirmDialog.vue";
import {useDeletePhoto} from "@/composables/photo/deletePhoto";
import PhotoFormModal from "@/components/modal/photoform/PhotoFormModal.vue";

const emit = defineEmits(['page-changed', 'edit-success', 'delete-success', 'error']);

const props = defineProps({
  photos: {
    type: Array<PhotoDTO>,
    required: true
  },
  page: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  isMyPage: {
    type: Boolean,
    required: true
  }
});

const pageSelected = ref(props.page);

const pageChanged = (newPage: number) => {
  emit('page-changed', newPage);
};

const editModal = ref();
const editPhoto = (photo: PhotoDTO) => {
  if (editModal.value) {
    editModal.value.openModal(photo);
  }
};

const deleteIconClicked = (photoId: number) => {
  confirm.value.open(TITLES.DELETE_PHOTO, CONFIRMATION_MESSAGES.ARE_YOU_SURE, {color: '#B64038'})
      .then((confirm: boolean) => {
        if (confirm) {
          deletePhoto(photoId);
        }
      })
};

const deletePhoto = (photoId: number) => {
  useDeletePhoto(photoId)
      .then(() => {
        emit('delete-success');
      })
      .catch((error) => {
        emit('error', error.response.data.message);
      });
};

const photosIds = computed(() => {
  return props.photos.map(photo => photo.id);
});

const photoViewStore = usePhotoViewStore();
watch(() => photosIds.value, (newVal) => {
  photoViewStore.setPhotosIds(newVal);
});

const onEditSuccess = () => {
  emit('edit-success');
};

const galleryContainerStyle = computed(() => {
  if (photoViewStore.photoId) {
    return {
      overflow: 'hidden',
      position: 'fixed'
    };
  } else return {};
});

const confirm = ref();

const authStore = useAuthStore();
const arePhotoActionButtonsDisplayed = computed(() => {
  return props.isMyPage || authStore.isModeratorOrHigher;
});

const showPagination = computed(() => props.totalPages > 1);
</script>

<template>
  <div class="user-photos-waterfall-container"
       :style="galleryContainerStyle"
  >
    <ConfirmDialog ref="confirm"></ConfirmDialog>
    <PhotoFormModal ref="editModal"
                    :is-edit="true"
                    @submit-success="onEditSuccess"/>
    <div class="user-gallery-photos-wrapper">
      <div v-for="photo in photos" :key="photo.id" class="user-gallery-photo-wrapper">
        <div v-if="arePhotoActionButtonsDisplayed" class="buttons-wrapper buttons-wrapper-absolute">
          <v-btn class="edit-button" icon="mdi-pencil" @click="editPhoto(photo)"/>
          <v-btn class="delete-button" icon="mdi-delete" @click="deleteIconClicked(photo.id)"/>
        </div>

        <div class="user-photo-wrapper" v-if="photo.url">
          <RouterLink :to="{ name: 'photo', params: { photoId: photo.id }}">
            <img :src="photo.url" :alt="photo.title"/>
          </RouterLink>
        </div>
        <div v-else class="loader-wrapper">
          <DotsLoader/>
        </div>
        <div class="photo-title">{{ photo.title }}</div>
      </div>
    </div>
    <div v-if="photos.length === 0">
      <NoContent :message="isMyPage ? 'Тут будуть фото, які ви завантажите' : 'Користувач ще не завантажив фото'"/>
    </div>
    <div class="pagination-wrapper">
      <v-pagination
          :length="totalPages"
          :class="!showPagination ? 'no-pagination' : ''"
          :start="1"
          v-model="pageSelected"
          @update:model-value="pageChanged"/>
    </div>
  </div>
</template>

<style scoped lang="less">
.user-photos-waterfall-container {
  width: 100%;
  padding: 16px 25px 0 25px;
}

.user-gallery-photos-wrapper {
  width: 100%;
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.user-gallery-photo-wrapper {
  width: 300px;
  height: 300px;
  margin: 1rem 0;
  position: relative;

  .buttons-wrapper {
    display: none;
  }

  img {
    width: 100%;
    height: 100%;
    min-height: 180px;
    border-radius: 5px;
    object-fit: cover;
  }

  .loader-wrapper {
    width: 100%;
    min-height: 200px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .photo-title {
    display: inline-block;
    font-family: e-Ukraine, sans-serif;
    font-weight: 200;
    color: #6B7280;
    font-size: 0.75rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 100%;
  }
}

.user-gallery-photo-wrapper:hover {
  .buttons-wrapper {
    display: flex;
    position: absolute;
    bottom: 0.5rem;
    right: 0.5rem;
  }
}

.user-photo-wrapper {
  width: 100%;
  height: 100%;

  img {
    color: black;
  }

  img:hover {
    cursor: pointer;
  }
}

.pagination-wrapper {
  margin-top: 1.5rem;
}
</style>