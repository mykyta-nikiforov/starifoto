<script setup lang="ts">

defineProps({
  message: {
    type: String,
    required: true
  }
});

</script>

<template>
  <div class="no-content-container">
    <div>{{message}}</div>
  </div>
</template>

<style scoped lang="less">
.no-content-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  font-size: 1.5rem;
  color: #6B7280;
}
</style>