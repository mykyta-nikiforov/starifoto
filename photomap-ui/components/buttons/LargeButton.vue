<script setup>
import {defineEmits} from 'vue';

const emit = defineEmits(['click']);

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  },
  label: {
    type: String,
    required: true
  },
  width: {
    type: String,
    default: 'auto'
  },
  loading: {
    type: Boolean,
    default: false
  },
  color: {
    type: String,
    default: 'white'
  },
  backgroundColor: {
    type: String,
    default: 'black'
  }
});
const emitClick = () => {
  emit('click');
};
</script>

<template>

  <v-btn :class="`photomap-v-btn ${disabled ? 'disable-events' : ''}`"
         :style="{color: color, backgroundColor: backgroundColor}"
         :disabled="disabled"
         :width="width"
         :loading="loading"
         @click="emitClick">
    {{ label }}
  </v-btn>

</template>


<style lang="less" scoped>
.photomap-v-btn {
  font-family: 'e-Ukraine Head', sans-serif;
  font-style: normal;
  font-size: 16px;
  line-height: 24px;
  font-weight: 500;
  min-height: 56px;
  border-radius: 38px;
  text-transform: unset !important;
  padding: 0 1rem;

  :deep(.v-btn__content) {
    padding: 0 1rem;
  }
}

.disable-events {
  pointer-events: none
}

</style>
