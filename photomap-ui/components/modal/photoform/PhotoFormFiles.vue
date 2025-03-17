<script setup lang="ts">
import {defineEmits, ref, watch} from 'vue';
import FileInput from "@/components/modal/photoform/FileInput.vue";

const props = defineProps({
  isEdit: {
    type: Boolean,
    required: false,
    default: false
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
    required: true
  },
});

const emit = defineEmits(['file-changed', 'colorized-file-changed', 'update-photo']);

const fileInputRef = ref();

const emitFileChanged = () => {
  emit('file-changed', fileInputRef.value.dropzoneFile);
};

const colorizedFileInputRef = ref();
const uploadColorized = ref(!!props.photo.colorizedUrl || !!props.colorizedFile);

watch(uploadColorized, (newValue) => {
  if (!newValue) {
    emit('colorized-file-changed', null);
  }
});

const onClearUrl = () => {
  emit('update-photo', {field: 'url', value: null});
};

const colorizedInputUrl = ref(props.photo.colorizedUrl);
const onClearColorizedUrl = () => {
  colorizedInputUrl.value = null;
  uploadColorized.value = false;
  emit('update-photo', {field: 'colorizedUrl', value: null});
};

const emitColorizedFileChanged = () => {
  emit('colorized-file-changed', colorizedFileInputRef.value.dropzoneFile);
};
</script>

<template>
  <div class="file-wrapper">
    <FileInput
        ref="fileInputRef"
        :input-id="'photo-form-file-input'"
        :file="props.file"
        :url="props.photo.url"
        @file-changed="emitFileChanged"
        @file-drop="emitFileChanged"
        @clear-url="onClearUrl"
    />
    <v-checkbox
        v-model="uploadColorized"
        label="Додати кольоризовану версію"
        v-if="!colorizedInputUrl"
    />
    <div v-else class="file-label">
      <label>Кольоризована версія:</label>
    </div>
    <FileInput
        v-if="uploadColorized"
        ref="colorizedFileInputRef"
        :input-id="'photo-form-colorized-file-input'"
        :file="props.colorizedFile"
        :url="colorizedInputUrl"
        @file-changed="emitColorizedFileChanged"
        @file-drop="emitColorizedFileChanged"
        @clear-url="onClearColorizedUrl"
    />
  </div>
</template>

<style scoped lang="less">
.file-wrapper {
  width: 100%;
  min-height: 200px;
  align-self: center;

  @media (min-width: 1024px) {
    width: 50%;
    align-self: auto;
  }
}

.file-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
}

.file-label {
  margin: 1rem 0 0.5rem;
}
</style>