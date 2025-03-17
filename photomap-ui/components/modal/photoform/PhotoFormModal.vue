<script setup lang="ts">
import {computed, defineEmits, ref, toRaw} from 'vue';
import PhotoForm from "@/components/modal/photoform/PhotoForm.vue";
import LocationChooser from "./location/LocationChooser.vue";
import SystemMessageModal from "@/components/modal/SystemMessageModal.vue";
import ModalComponent from "@/components/modal/ModalComponent.vue";
import PhotoFormModalHeader from "@/components/modal/photoform/PhotoFormModalHeader.vue";
import PhotoFormModalFooter from "@/components/modal/photoform/PhotoFormModalFooter.vue";
import {VForm} from "vuetify/components";
import {useUpdatePhoto} from "@/composables/photo/updatePhoto";
import {useUploadPhoto} from "@/composables/photo/uploadPhoto";
import {type PhotoDTO} from "@/dto/Photo";
import SimilarPhotosModal from "./SimilarPhotosModal.vue";

const formDefaults = {
  id: 0,
  title: '',
  description: '',
  author: '',
  source: '',
  licenseId: 0,
  tags: Array<string>(),
  coordinates: {
    latitude: 0,
    longitude: 0,
    isApproximate: false
  },
  yearRange: {
    start: null,
    end: null
  },
  colorizedUrl: ''
};

const emit = defineEmits(['close', 'submit-success']);
const modal = ref();
const modalOpened = ref(false);
const activeStepNumber = ref(1);
const photoFormRef = ref();
const photo = ref(structuredClone(formDefaults));
const file = ref();
const colorizedFile = ref();
const similarPhotosFound = ref<PhotoDTO[]>([]);

const props = defineProps({
  isEdit: {
    type: Boolean,
    required: false,
    default: false
  },
  getMapCenter: {
    type: Function,
    required: false
  },
  getMapZoom: {
    type: Function,
    required: false
  }
});


const originalPhoto = ref();
const openModal = (newPhoto: PhotoDTO) => {
  if (newPhoto) {
    // @ts-ignore
    photo.value = structuredClone(toRaw(newPhoto));
    originalPhoto.value = structuredClone(toRaw(newPhoto));
  }
  modal.value.openModal();
  modalOpened.value = true;
};

const hasChanges = computed(() => {
  return !props.isEdit
      || JSON.stringify(photo.value) !== JSON.stringify(originalPhoto.value)
      || (file.value || colorizedFile.value);
});
const openStep = (stepNumber: number) => {
  activeStepNumber.value = stepNumber;
};

const footer = ref();

const closeModal = () => {
  modal.value.setIsOpen(false);
  modalOpened.value = false;
  emit('close');
  // Reset form after modal animation finishes
  setTimeout(() => {
    activeStepNumber.value = 1;
    photo.value = structuredClone(formDefaults);
    file.value = null;
    colorizedFile.value = null;
    licenseHelperParams.value = defaultLicenseHelperParams;
    clearButtonProcessing();
  }, 200);
};

const onUploadSuccess = () => {
  closeModal();
  emit('submit-success');
};

const onError = (error: any) => {
  errorMessage.value = `Деталі: «${error.message}».`;
  clearButtonProcessing();
};

const clearButtonProcessing = () => {
  footer.value?.setButtonProcessing(false);
};

const areCoordinatesPresent = computed(() => {
  const {latitude, longitude} = photo.value.coordinates;
  return latitude != null && longitude != null && latitude !== 0 && longitude !== 0
});

// @ts-ignore
const onPhotoUpdate = ({field, value}) => {
  // @ts-ignore
  photo.value[field] = value;
};

const onFileChanged = (newFile: File) => {
  file.value = newFile;
};

const onColorizedFileChanged = (newFile: File) => {
  colorizedFile.value = newFile;
};

const defaultLicenseHelperParams = {
  licenseType: null,
  copyrightStatus: null
}
const licenseHelperParams = ref(defaultLicenseHelperParams);

// @ts-ignore
const onLicenseHelperUpdated = (params) => {
  licenseHelperParams.value = params;
};

const errorMessage = ref();
const clearError = () => {
  errorMessage.value = null;
};

const trimString = (str: string) => {
  return str.trim();
};

const form = ref<typeof VForm>();
const isFormValid = ref(false);

const submitForm = async (params: {similarValidationSkip: boolean} = {similarValidationSkip: false}) => {
  await form.value?.validate();
  if (!isFormValid.value || !hasChanges.value) {
    return;
  }
  const photoCopy = structuredClone(toRaw(photo.value));

  const payload: any = {
    metadata: {
      title: trimString(photoCopy.title),
      description: trimString(photoCopy.description),
      source: trimString(photoCopy.source),
      author: trimString(photoCopy.author),
      licenseId: photoCopy.licenseId,
      tags: photoCopy.tags,
      coordinates: photoCopy.coordinates,
      yearRange: photoCopy.yearRange
    },
    file: file.value,
    colorizedFile: colorizedFile.value
  };
  let result;
  if (props.isEdit && photo.value.id) {
    payload.metadata.changedImageTypes = [];
    if (file.value) {
      payload.metadata.changedImageTypes.push('ORIGINAL');
    }
    if (colorizedFile.value || (!photo.value.colorizedUrl && originalPhoto.value.colorizedUrl)) {
      payload.metadata.changedImageTypes.push('COLORIZED');
    }
    result = useUpdatePhoto(photo.value.id, payload);
  } else {
    result = useUploadPhoto(payload, {
      similarValidationSkip: params.similarValidationSkip
    });
  }
  await result
      .then(() => onUploadSuccess())
      .catch((error) => {
        if (error.response?.status === 409) {
          similarPhotosFound.value = error.response.data;
          clearButtonProcessing();
        } else {
          onError(error)
        }
      });
};

const onDuplicatedPhotoClose = () => {
  similarPhotosFound.value = [];
};

const onDuplicatedPhotoContinue = () => {
  similarPhotosFound.value = [];
  submitForm({similarValidationSkip: true});
  footer.value?.setButtonProcessing(true);
};

const modelContentClassName = computed(() => {
  return activeStepNumber.value === 2 ? 'pa-0' : '';
});

defineExpose({
  openModal,
  closeModal,
  modalOpened
});
</script>

<template>
  <ModalComponent ref="modal" :contentClassNames="modelContentClassName" @close="closeModal">
    <template #header>
      <PhotoFormModalHeader
          :is-edit="props.isEdit"
          :active-step-number="activeStepNumber"
          @close="closeModal"
          @open-step="openStep"
      />
    </template>
    <template #content>
      <SystemMessageModal v-if="errorMessage"
                          :message="errorMessage"
                          @close="clearError"/>
      <SimilarPhotosModal
          v-if="similarPhotosFound.length !== 0"
          :photos="similarPhotosFound"
          @close="onDuplicatedPhotoClose"
          @continue="onDuplicatedPhotoContinue"
      />
      <div class="modal">
        <v-form ref="form" v-model="isFormValid">
          <PhotoForm
              v-if="activeStepNumber === 1"
              ref="photoFormRef"
              :is-edit="props.isEdit"
              :photo="photo"
              :file="file"
              :colorized-file="colorizedFile"
              :are-coordinates-present="areCoordinatesPresent"
              :license-helper-params="licenseHelperParams"
              @open-step="openStep"
              @file-changed="onFileChanged"
              @colorized-file-changed="onColorizedFileChanged"
              @update-photo="onPhotoUpdate"
              @license-helper-updated="onLicenseHelperUpdated"
          />
          <div v-if="activeStepNumber === 2"
               class="map-wrapper">
            <LocationChooser
                :isEdit="props.isEdit"
                :coordinates="photo.coordinates"
                :getMapCenter="props.getMapCenter"
                :getMapZoom="props.getMapZoom"
                :photoId="photo.id"
                @coordinates-changed="(lng, lat) => photo.coordinates = {...photo.coordinates, latitude: lat, longitude: lng}"
            />
          </div>
        </v-form>
      </div>
    </template>
    <template #actions>
      <PhotoFormModalFooter ref="footer"
                            :is-edit="props.isEdit"
                            :photo="photo"
                            :active-step-number="activeStepNumber"
                            :are-coordinates-present="areCoordinatesPresent"
                            :is-form-valid="isFormValid"
                            :has-changes="hasChanges"
                            @open-step="openStep"
                            @submit-clicked="submitForm"
                            @update-photo="onPhotoUpdate"
                            @error="onError"

      />
    </template>
  </ModalComponent>
</template>

<style scoped lang="less">
.modal {
  overflow-y: auto;
  width: 100%;
  height: 100%;
  min-height: 200px;
}

.map-wrapper {
  width: 100%;
  height: 100%;
}
</style>