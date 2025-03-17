<script setup lang="ts">

import {ref, watch} from "vue";

const props = defineProps({
  persistent: {
    type: Boolean,
    required: false,
    default: true
  },
  contentClassNames: {
    type: String,
    required: false,
    default: ''
  }
});

const emit = defineEmits(['close']);

const isOpened = ref(false);

watch(isOpened, (newValue) => {
  if (!newValue) {
    emit('close');
  }
});

const openModal = () => {
  isOpened.value = true;
};

const setIsOpen = (value: boolean) => {
  isOpened.value = value;
};

defineExpose({
  openModal,
  setIsOpen
});
</script>

<template>
  <v-dialog
      v-model="isOpened"
      max-width="1000"
      :persistent="props.persistent"
  >
    <template v-slot:default="{ isActive }">
      <v-card>
        <v-card-item>
          <slot name="header"></slot>
        </v-card-item>
        <v-divider></v-divider>
        <v-card-text :class="`${contentClassNames} modal-content`">
          <slot name="content"></slot>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <slot name="actions"></slot>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>

<style scoped lang="less">
.modal-content {
  max-height: 700px;
  height: auto;
  overflow-y: auto;
}

@media (max-height: 800px) {
  .modal-content {
    max-height: 80vh;
  }
}
</style>