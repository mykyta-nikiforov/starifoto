<script setup>

import {computed, defineEmits, nextTick, ref} from "vue";
import DropZone from "@/components/modal/photoform/DropZone.vue";
import deleteIcon from "@/assets/icons/cross.svg";
import {VFileInput} from "vuetify/components";

const props = defineProps({
  file: {
    type: [Object, null],
    required: true,
    default: null
  },
  inputId: {
    type: String,
    required: true
  },
  url: {
    type: String,
    required: false
  }
});

const emit = defineEmits(['file-changed', 'file-drop', 'clear-url']);
const dropzoneFile = ref(props.file);

const onInputChange = (event) => {
  onFileChange(event.target.files[0]);
};

const onFileChange = (file) => {
  dropzoneFile.value = file;
  if (rules.every(rule => rule(file) === true)) {
    emit('file-changed', file);
  }
}

const fileInputRef = ref(null);
const onDeleteClicked = () => {
  if (props.url) {
    emit('clear-url');
  } else {
    dropzoneFile.value = null;
    emit('file-changed', null);
    nextTick(() => {
      fileInputRef.value?.validate();
    });
  }
};

const getImageSrc = () => {
  return props.url || URL.createObjectURL(props.file);
};

const isImageSelected = computed(() => {
  return (props.file && props.file.type && props.file.type.startsWith('image/'))
      || props.url;
});

const fileMaxSize = 5 * 1024 * 1024;  // 5 MB
const rules = [
  v => !!v && v.size > 0 || 'Файл обов\'язковий',
  v => (v && v.type === 'image/jpeg') || 'Формат файлу не підтримується. Оберіть файл з розширенням .jpg або .jpeg',
  v => (v && v.size < fileMaxSize) || `Розмір файлу завеликий (максимум — ${fileMaxSize / 1024 / 1024} МБ)`
]

defineExpose({
  dropzoneFile
})
</script>

<template>
  <div>
    <DropZone
        v-if="!isImageSelected"
        :inputId="props.inputId"
        @drop="onFileChange"/>
    <div class="selected-image-container" v-else>
      <img :src="deleteIcon" class="clear-image-button" @click="onDeleteClicked" alt="Видалити"/>
      <img :src="getImageSrc()"
           class="selected-image"
           alt="Uploaded Image"
      />
    </div>
  </div>
  <v-file-input
      v-if="!props.url"
      ref="fileInputRef"
      :class="props.file ? 'file-selected' : ''"
      prepend-icon=""
      :model-value="dropzoneFile"
      :multiple="false"
      :id="inputId"
      :rules="rules"
      @change="onInputChange"
  />
  <p class="file-info" v-if="isImageSelected && props.file">
    <span>Файл: </span>
    <span>{{ props.file.name }}</span>
  </p>
</template>

<style scoped lang="less">
.photo-file-input {
  width: 100%;
  cursor: pointer;
}

:deep(.v-input__control) {
  display: none;
}

:deep(.v-messages__message) {
  margin-bottom: 0.5rem;
}

.file-selected {
  display: none;
}
</style>