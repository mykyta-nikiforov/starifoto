<script setup lang="ts">

import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";

definePageMeta({
  rolesPermitted: ['Admin']
});
</script>

<template>
  <client-only>
    <GeneralViewWrapper ref="viewWrapper">
      <div>
        <h1>Адмін-панель</h1>
        <nav class="nav-container">
          <NuxtLink to="photos/1" class="link">Усі фото</NuxtLink>
          <NuxtLink to="users" class="link">Користувачі</NuxtLink>
        </nav>
      </div>
    </GeneralViewWrapper>
  </client-only>
</template>

<style scoped lang="less">
.nav-container {
  display: flex;
  gap: 1rem;
}
</style>