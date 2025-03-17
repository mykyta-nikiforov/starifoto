<script setup lang="ts">

import LargeButton from "../../buttons/LargeButton.vue";
import PhotoFormModal from "../../modal/photoform/PhotoFormModal.vue";
import {ref} from "vue";
import {SNACKBAR_MESSAGES, TOOLTIP_MESSAGES} from "../../../constants/messages";
import {useSystemSnackbarStore} from "../../../store/systemSnackbarStore";
import {useAuthStore} from "../../../store/authStore";

defineProps({
  getMapCenter: {
    type: Function,
    required: true
  },
  getMapZoom: {
    type: Function,
    required: true
  }
});

const modal = ref();
const openPhotoFormModal = () => {
  if (!disabled.value) {
    modal.value.openModal()
  }
}

const onUploadSuccess = () => {
  useSystemSnackbarStore().setMessage(SNACKBAR_MESSAGES.PHOTO_UPLOADED);
}

const disabled = computed(() => !useAuthStore().isLoggedIn);
</script>

<template>
  <div class="add-photo-container">
    <LargeButton
        :label="'Додати фото'"
        :disabled="disabled"
        @click="openPhotoFormModal"/>
    <PhotoFormModal ref="modal"
                    :is-edit="false"
                    :get-map-center="getMapCenter"
                    :get-map-zoom="getMapZoom"
                    @submit-success="onUploadSuccess"/>
    <v-tooltip
        v-if="disabled"
        activator="parent"
        location="bottom"
        origin="top"
    >{{TOOLTIP_MESSAGES.NEED_AUTH_TO_ADD_PHOTO}}
    </v-tooltip>
  </div>
</template>

<style scoped lang="less">
.add-photo-container {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  right: 0;
  z-index: 50;
  display: flex;
  justify-content: center;

  @media (min-width: 768px) {
    top: 1rem;
    right: 3.5rem;
    left: auto;
    height: fit-content;
    transform: none;
  }
}
</style>