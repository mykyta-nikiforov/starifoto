<script setup lang="ts">

import {computed, ref, watch} from "vue";
import LinkMarkupInput from "@/components/input/richeditor/LinkMarkupInput.vue";
import {VALID_URL} from "@/constants/regex";
import {VALIDATION_RULES} from "@/constants/validationRules";
import {convertUrlToMarkdownLink} from "@/utils/markup";

const props = defineProps({
  modelValue: {
    type: String,
    required: true
  }
});

const sourceValue = ref(props.modelValue);

const emit = defineEmits(['update:modelValue']);

watch(sourceValue, (newValue) => {
  emit('update:modelValue', newValue);
});

function isStartWithHttp(value: string): boolean {
  return value.startsWith('http');
}

const isHttpLink = computed(() => {
  return isStartWithHttp(sourceValue.value);
});

const isSourceEntered = computed(() => {
  return sourceValue.value.length > 0;
});

const onLinkAdded = (link: string) => {
  sourceValue.value = convertUrlToMarkdownLink(link, sourceValue.value);
};

const isLinkAdded = computed(() => {
  return /\[.*]\(.*\)/.test(sourceValue.value);
});

const sourceRules = [
  VALIDATION_RULES.REQUIRED,
  VALIDATION_RULES.MAX_CHARS_2048,
  (v: string) => !isStartWithHttp(v) ? true : VALID_URL.test(v) || 'Введіть повну адресу'
];

</script>

<template>
  <LinkMarkupInput v-model="sourceValue"
                   input-id="source"
                   label-text="Джерело"
                   placeholder="Наприклад, назва книги або посилання: https://uk.wikipedia.org/..."
                   :show-add-link-button="isSourceEntered && !isHttpLink && !isLinkAdded"
                   :additionalRules="sourceRules"
                   @addLinkSuccess="onLinkAdded"
  />
</template>