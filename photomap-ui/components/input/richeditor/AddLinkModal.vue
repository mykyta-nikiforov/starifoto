<script setup lang="ts">
import {computed, ref} from 'vue';

const dialog = ref(false);
const selectedText = ref();
const url = ref();

const urlRules = [
  v => !!v || 'Посилання обов\'язкове',
  v => (v.startsWith('https://')) || 'Невірний формат посилання'
];

const props = defineProps({
  additionalRules: {
    type: Array,
    required: false,
    default: () => []
  }
});

const emit = defineEmits(['addLink', 'close']);

const openModal = (text: string) => {
  selectedText.value = text;
  dialog.value = true;
};

const addLink = () => {
  emit('addLink', url.value);
  clear();
};

const clear = () => {
  dialog.value = false;
  url.value = null;
  selectedText.value = null;
};

const onClose = () => {
  clear();
  emit('close');
};

const isValidUrl = computed(() => {
  return urlRules.every(rule => rule(url.value) === true)
      && props.additionalRules.every(rule => rule(url.value) === true);
});

defineExpose({
  open: openModal
});
</script>

<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <v-card>
      <v-card-title>Додати посилання</v-card-title>
      <v-card-text>
        <div v-if="selectedText" class="text-wrapper">
          <div>Виділений текст: <i>{{ selectedText }}</i></div>
          <div>Ви можете додати посилання на Вікіпедію</div>
        </div>
        <v-text-field
            label="URL"
            v-model="url"
            :rules="[...urlRules, ...additionalRules]"
            outlined
            dense></v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            class="ms-auto"
            color="ms-auto"
            text="Відмінити"
            @click="onClose"/>
        <v-btn
            :disabled="!isValidUrl"
            class="ms-auto"
            text="Додати"
            @click="addLink"/>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.text-wrapper {
  margin-bottom: 1rem;
}
</style>