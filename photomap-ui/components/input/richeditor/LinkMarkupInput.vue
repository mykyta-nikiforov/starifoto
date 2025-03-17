<script setup lang="ts">
import {ref, watch} from 'vue';
import AddLinkModal from "@/components/input/richeditor/AddLinkModal.vue";

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
  showAddLinkButton: {
    type: Boolean,
    required: false,
    default: true
  },
  selectedText: {
    type: String,
    required: false
  },
  additionalRules: {
    type: Array,
    required: false,
    default: () => []
  },
  additionalAddLinkRules: {
    type: Array,
    required: false,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'blur', 'click', 'select', 'addLinkSuccess', 'addLinkClose']);

const text = ref(props.modelValue);
const addLinkModal = ref(null);

watch(text, (newValue) => {
  emit('update:modelValue', newValue);
});

watch(() => props.modelValue, (newValue) => {
  text.value = newValue;
});

const onAddLinkClicked = () => {
  addLinkModal.value.open(props.selectedText);
};
</script>

<template>
  <div class="simple-link-editor">
    <label :for="props.inputId" class="input-field-label">{{ props.labelText }}</label>
    <div class="reach-input-wrapper">
      <AddLinkModal ref="addLinkModal"
                    :additional-rules="props.additionalAddLinkRules"
                    @addLink="(url) => emit('addLinkSuccess', url)"
                    @close="emit('addLinkClose')"/>
      <v-textarea v-model="text"
                  :id="props.inputId"
                  class="reach-input-text"
                  no-resize
                  rows="2"
                  :placeholder="props.placeholder"
                  @select="emit('select', $event)"
                  @blur="emit('blur', $event)"
                  @click="emit('click', $event)"
                  :rules="props.additionalRules"/>
      <v-btn v-if="showAddLinkButton"
             icon="mdi-link"
             density="compact"
             class="link-button"
             @click="onAddLinkClicked"/>
    </div>
  </div>
</template>

<style scoped>
.simple-link-editor {
  display: flex;
  flex-direction: column;
}

.reach-input-wrapper {
  position: relative;
}

.reach-input-text {
  :deep(.v-field__input) {
    padding-right: 2.5rem;
  }
}

.link-button {
  position: absolute;
  top: 0.5rem;
  right: 0.7rem;
}
</style>
