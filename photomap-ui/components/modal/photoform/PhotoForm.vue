<script setup lang="ts">
import {defineEmits} from 'vue';
import PhotoFormMetadata from "@/components/modal/photoform/PhotoFormMetadata.vue";
import PhotoFormFiles from "@/components/modal/photoform/PhotoFormFiles.vue";

const props = defineProps({
  isEdit: {
    type: Boolean,
    required: true
  },
  photo: {
    type: Object,
    required: true
  },
  file: {
    type: [Object, null],
    required: true
  },
  colorizedFile: {
    type: [Object, null],
    required: true,
  },
  areCoordinatesPresent: {
    type: Boolean,
    required: true
  },
  licenseHelperParams: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update-photo', 'file-changed', 'colorized-file-changed', 'open-step', 'license-helper-updated']);

const onPhotoUpdate = ({field, value}) => {
  emit('update-photo', {field, value});
};

const onFileChanged = (file) => {
  emit('file-changed', file);
};

const onColorizedFileChanged = (file) => {
  emit('colorized-file-changed', file);
};

const onLicenseHelperUpdated = (params) => {
  emit('license-helper-updated', params);
};

const openStep = (stepNumber: number) => {
  emit('open-step', stepNumber);
};
</script>

<template>
  <div class="photo-modal-content">
    <PhotoFormFiles :file="props.file"
                    :colorized-file="props.colorizedFile"
                    :is-edit="props.isEdit"
                    :photo="props.photo"
                    @file-changed="onFileChanged"
                    @colorized-file-changed="onColorizedFileChanged"
                    @update-photo="onPhotoUpdate"
    />
    <PhotoFormMetadata
        :is-edit="props.isEdit"
        :are-coordinates-present="props.areCoordinatesPresent"
        :photo="props.photo"
        :licenseHelperParams="props.licenseHelperParams"
        @open-step="openStep"
        @update-photo="onPhotoUpdate"
        @license-helper-updated="onLicenseHelperUpdated"
    />
  </div>
</template>

<style scoped lang="less">
.photo-modal-content {
  margin-top: 1rem;
  width: 100%;
  display: flex;
  padding-bottom: 2rem;
  gap: 2rem;
  overflow-x: hidden;

  @media (max-width: 1024px) {
    flex-direction: column;
  }
}
</style>