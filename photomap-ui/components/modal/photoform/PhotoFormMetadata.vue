<script setup lang="ts">
import {computed, defineEmits, ref} from 'vue';
import TagInput from "@/components/modal/photoform/TagInput.vue";
import RichTextEditor from "@/components/input/richeditor/RichTextEditor.vue";
import SourceInput from "@/components/input/SourceInput.vue";
import {VALIDATION_RULES} from "@/constants/validationRules";
import YearInput from "@/components/modal/photoform/YearInput.vue";
import CoordinatesInput from "@/components/modal/photoform/CoordinatesInput.vue";
import LicenseInput from "@/components/modal/photoform/LicenseInput.vue";
import LicenseHelper from "@/components/modal/photoform/LicenseHelper.vue";

const props = defineProps({
  photo: {
    type: Object,
    required: true
  },
  areCoordinatesPresent: {
    type: Boolean,
    required: true
  },
  isEdit: {
    type: Boolean,
    required: true
  },
  licenseHelperParams: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['open-step', 'update-photo', 'license-helper-updated']);

const onTitleInput = (title: string) => {
  onPhotoUpdate({field: 'title', value: title});
};

const onRichDescriptionChanged = (newValue: string) => {
  onPhotoUpdate({field: 'description', value: newValue});
};

const requiresSourceInput = computed(() => {
  return props.isEdit || props.licenseHelperParams.licenseType === 'someone-else';
});

const onSourceChanged = (newValue: string) => {
  onPhotoUpdate({field: 'source', value: newValue});
};

const onAuthorChanged = (newValue: string) => {
  onPhotoUpdate({field: 'author', value: newValue});
};

const onPhotoUpdate = ({field, value}) => {
  emit('update-photo', {field, value});
};

const onTagsChanged = (tags) => {
  onPhotoUpdate({field: 'tags', value: tags});
};

const openStep = (stepNumber: number) => {
  emit('open-step', stepNumber);
};

const onLicenseChanged = (licenseId) => {
  emit('update-photo', {field: 'licenseId', value: licenseId})
};

const onLicenseHelperUpdated = (params) => {
  emit('license-helper-updated', params);
};

const title = ref(props.photo.title);
</script>

<template>
  <div class="photo-metadata">
    <div class="input-field">
      <label for="title" class="input-field-label">Назва</label>
      <v-text-field
          id="title"
          v-model="title"
          :rules="[VALIDATION_RULES.REQUIRED, VALIDATION_RULES.MAX_CHARS_255]"
          :validation-value="title"
          @input="onTitleInput($event.target.value)"
      />
    </div>
    <div class="input-field">
      <RichTextEditor
          :model-value="props.photo.description"
          @update:model-value="onRichDescriptionChanged"
          :additional-input-rules="[VALIDATION_RULES.REQUIRED, VALIDATION_RULES.MAX_CHARS_2048]"
          :labelText="'Опис'"
          :inputId="'description'"
          :placeholder="'Що зображено на фото?' "
      />
    </div>
    <div class="input-field">
      <RichTextEditor
          :model-value="props.photo.author"
          @update:model-value="onAuthorChanged"
          :additional-input-rules="[VALIDATION_RULES.MAX_CHARS_255]"
          :labelText="'Автор'"
          :inputId="'author'"
          :placeholder="'Ім\'я автора фото, якщо відоме' "
      />
    </div>
    <div class="input-field">
      <YearInput :year-range="props.photo.yearRange"
                 :is-edit="props.isEdit"
                 @update-photo="onPhotoUpdate"/>
    </div>
    <div class="input-field">
      <label for="tag-input" class="input-field-label">Теги</label>
      <TagInput id="tag-input"
                :tags="props.photo.tags"
                @tags-changed="onTagsChanged"
                :rules="[VALIDATION_RULES.REQUIRED_ARRAY]"/>
    </div>
    <div class="input-field">
      <LicenseInput
          v-if="props.isEdit"
          :model-value="props.photo.licenseId"
          @update:model-value="onLicenseChanged"/>
      <LicenseHelper
          v-else
          :licenseId="props.photo.licenseId"
          :licenseHelperParams="props.licenseHelperParams"
          @license-changed="onLicenseChanged"
          @params-updated="onLicenseHelperUpdated"
          @update-photo="onPhotoUpdate"
      />
    </div>
    <div v-if="requiresSourceInput"
         class="input-field">
      <SourceInput
          :model-value="props.photo.source"
          @update:model-value="onSourceChanged"/>
    </div>
    <div class="input-field">
      <label for="coordinates-input" class="input-field-label">Локація, зображена на фото</label>
      <CoordinatesInput :are-coordinates-present="props.areCoordinatesPresent"
                        :photo="props.photo"
                        @open-step="openStep"/>
    </div>
  </div>
</template>

<style scoped lang="less">
.photo-metadata {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  max-width: 452px;
  align-self: center;

  @media (min-width: 1024px) {
    width: 50%;
    align-self: auto;
  }
}

.input-field > textarea {
  resize: none;
  height: 100px;
  padding-top: 0.75rem
}

.open-map-button {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 1rem;
  border-radius: 8px;
  background-color: #F3F4F6;
  cursor: pointer;

  :deep(span) {
    color: #6B7280;
  }
}
</style>