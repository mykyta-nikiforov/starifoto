<script setup lang="ts">

import {defineEmits, ref} from 'vue';

const emit = defineEmits(['close']);

const props = defineProps({
  message: {
    type: String,
    required: true
  },

  isError: {
    type: Boolean,
    default: true
  }
});

const isOpened = ref(true);

const close = () => {
  emit('close');
}

</script>

<template>
  <v-dialog
      v-model="isOpened"
      width="auto"
  >
    <v-card
        max-width="400"
        :prepend-icon="isError ? 'mdi-alert-circle-outline' : ''"
        :text="props.message"
        :title="isError ? 'Трапилася помилка' : ''"
    >
      <template v-slot:actions>
        <v-btn
            class="ms-auto"
            text="Ok"
            @click="close"
        ></v-btn>
      </template>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="less">

</style>