<template>
  <svg class="cross" viewBox="0 0 100 100" width="20" height="20">
    <line x1="10" y1="10" x2="90" y2="90" :stroke="color" stroke-width="6"/>
    <line x1="90" y1="10" x2="10" y2="90" :stroke="color" stroke-width="6"/>
  </svg>
</template>

<script setup>
defineProps({
  color: {
    type: String,
    default: '#111827'
  }
})
</script>