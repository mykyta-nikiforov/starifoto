<script setup lang="ts">
import {ref, watch} from 'vue';
import LinkMarkupInput from "@/components/input/richeditor/LinkMarkupInput.vue";
import {convertUrlToMarkdownLink} from "@/utils/markup";

const props = defineProps({
  modelValue: {
    type: String,
    required: true
  },
  inputId: {
    type: String,
    required: true
  },
  labelText: {
    type: String,
    required: true
  },
  placeholder: {
    type: String,
    required: false,
    default: ''
  },
  additionalInputRules: {
    type: Array,
    required: false,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue']);

const text = ref(props.modelValue);
const showAddLinkButton = ref(false);
const selectedText = ref();
const startIndex = ref();
const endIndex = ref();

watch(text, (newValue) => {
  emit('update:modelValue', newValue);
});

const handleSelect = (event: Event) => {
  const {value, selectionStart, selectionEnd} = event.target;
  selectedText.value = value.substring(selectionStart, selectionEnd);
  startIndex.value = selectionStart;
  endIndex.value = selectionEnd;
  showAddLinkButton.value = selectedText.value.length > 0;
};

const handleDeselection = (e) => {
  // If not clicking on the link button, then delete the selection
  if (!(e.relatedTarget && e.relatedTarget.classList.contains('link-button'))) {
    deleteSelection();
  }
};

const handleClick = (event: Event) => {
  if (event.target?.selectionStart === event.target.selectionEnd) {
    deleteSelection();
  }
};

const onAddLinkSuccess = (link: string) => {
  if (!link) return;
  const beforeText = text.value.substring(0, startIndex.value);
  const afterText = text.value.substring(endIndex.value);
  const hyperlink = convertUrlToMarkdownLink(link, selectedText.value);
  text.value = beforeText + hyperlink + afterText;
  deleteSelection();
};

const deleteSelection = () => {
  showAddLinkButton.value = false;
  selectedText.value = null;
  startIndex.value = null;
  endIndex.value = null;
};

const addLinkAvailableUrls = [
  v => (v.startsWith('https://') && (v.startsWith('https://akkermanika.org') || v.startsWith('https://uk.wikipedia.org')))
      || 'На цей сайт не можна додати посилання'
];
</script>

<template>
  <LinkMarkupInput
      :model-value="text"
      :input-id="props.inputId"
      :label-text="props.labelText"
      :placeholder="props.placeholder"
      :show-add-link-button="showAddLinkButton"
      :selected-text="selectedText"
      :additional-rules="props.additionalInputRules"
      :additional-add-link-rules="addLinkAvailableUrls"
      @update:model-value="text = $event"
      @addLinkSuccess="onAddLinkSuccess"
      @addLinkClose="deleteSelection"
      @select="handleSelect"
      @blur="handleDeselection"
      @click="handleClick"
  />
</template>