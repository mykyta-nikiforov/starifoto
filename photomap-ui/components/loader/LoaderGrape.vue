<script setup lang="ts">
import {defineProps, ref} from 'vue';

const props = defineProps({
  message: {
    type: String,
    default: ''
  },
  textColour: {
    type: String,
    default: 'white'
  }
});

const messageStyles = ref({color: props.textColour})
</script>

<template>
  <div id="loader">
    <div class="grapes-row">
      <div class="grape grape1"></div>
      <div class="grape grape2"></div>
      <div class="grape grape3"></div>
    </div>
    <div class="grapes-row">
      <div class="grape grape4"></div>
      <div class="grape grape5"></div>
    </div>
    <div class="grapes-row">
      <div class="grape grape6"></div>
    </div>
    <div class="loader-text" :style="messageStyles">{{ props.message }}</div>
  </div>
</template>

<style scoped>
#loader {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  opacity: 1;
  transition: opacity 0.5s ease-in-out;
}

#loader.hide {
  opacity: 0;
  z-index: -999;
}

.grapes-row {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.grape {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #B64038;
  margin: 5px;
  animation: pulse 1s ease-in-out infinite;
}

.grape2 {
  animation-delay: 0.1s;
}

.grape3 {
  animation-delay: 0.2s;
}

.grape4 {
  animation-delay: 0.3s;
}

.grape5 {
  animation-delay: 0.4s;
}

.grape6 {
  animation-delay: 0.5s;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.loader-text {
  font-size: 20px;
  margin-top: 20px;
  color: white;
  user-select: none;
}
</style>