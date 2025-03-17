<script setup lang="ts">
import {useAuthStore} from '@/store/authStore';
import {storeToRefs} from "pinia";
import {TITLES} from "@/constants/messages";
import {computed, ref} from "vue";
import Cross from "./Cross.vue";
import {useLogout} from "@/composables/auth/logout";

const authStore = useAuthStore();
const {user} = storeToRefs(authStore);

const isNavLinkDisplayed = ref(false);
const navLinksClass = computed(() => {
  return isNavLinkDisplayed.value ? 'nav-links-visible' : 'nav-links-hidden nav-links-visible';
});

</script>
<template>
  <header class="header">
    <nav class="max-width-wrapper">
      <div class="header-left-container">
        <div class="logo-and-hamburger-wrapper">
          <div class="logo-wrapper">
            <RouterLink to="/" class="logo-text">старіфото.укр</RouterLink>
          </div>
          <div class="hamburger-icon"
               @click="isNavLinkDisplayed = !isNavLinkDisplayed">
            <Cross v-if="isNavLinkDisplayed" color="#111827"></Cross>
            <svg v-else height="20" viewBox="0 0 100 80" width="20">
              <rect width="100" height="10"></rect>
              <rect y="30" width="100" height="10"></rect>
              <rect y="60" width="100" height="10"></rect>
            </svg>
          </div>
        </div>
      </div>
      <div :class="navLinksClass">
        <NuxtLink to="/about">Про проєкт</NuxtLink>
        <NuxtLink to="/admin" v-if="authStore.isModeratorOrHigher">{{ TITLES.ADMIN }}</NuxtLink>
        <NuxtLink v-if="user"
                  :to="{name: 'user', params: {userId: user.id}}">
          Мої фото
        </NuxtLink>
        <NuxtLink to="/settings" v-if="user">Налаштування</NuxtLink>
        <NuxtLink to="/login" v-if="!user">Увійти</NuxtLink>
        <client-only>
          <a href="/logout" @click.prevent="useLogout()" v-if="user">Вийти</a>
        </client-only>
      </div>
    </nav>
  </header>
</template>

<style lang="less" scoped>
.header {
  display: flex;
  min-height: inherit;

  font-family: e-Ukraine, sans-serif;
  font-weight: 300;
  font-size: 1rem;
  line-height: 1.5;

  width: 100%;
  flex-direction: column;
  justify-content: center;
  align-content: center;

  border-bottom-width: 1px;
  border-bottom-color: #d1d5db;
  border-bottom-style: solid;

  nav {
    width: 100%;
    height: 100%;

    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 0 auto;

    @media (min-width: 768px) {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }

    .header-left-container {
      display: flex;
      justify-content: start;
      min-height: 70px;
      //min-height: inherit;
      padding: 1rem;

      .logo-and-hamburger-wrapper {

        display: flex;
        flex-direction: row;
        justify-content: space-between;

        @media (max-width: 768px) {
          width: 100%;
          justify-content: space-between;
        }

        .hamburger-icon {
          display: flex;
          justify-content: center;
          align-items: center;
          cursor: pointer;
          padding: 0 1rem;

          -webkit-tap-highlight-color: transparent;
          -moz-tap-highlight-color: transparent;
          -ms-tap-highlight-color: transparent;
          tap-highlight-color: transparent;

          @media (min-width: 768px) {
            display: none;
          }
        }

        .logo-wrapper {

          display: flex;
          align-items: center;

          .logo-text,
          .logo-text:visited {
            display: block;
            color: #B64038;

            @media (min-width: 768px) {
              margin: auto 0;
            }
          }
        }

      }
    }

    .nav-links-visible {
      display: flex;
      flex-direction: column;
      justify-content: center;
      padding: 1.75rem 1rem;
      border-top: 1px solid rgb(209, 213, 219);

      @media (min-width: 768px) {
        flex-direction: row;
        gap: 0.5rem;
        justify-content: center;
        line-height: 1.5rem;
        padding-top: 0;
        padding-bottom: 0;
        border: none;
      }

      a {
        padding: 0.5rem 1rem;
      }
    }

    .nav-links-hidden {
      @media (max-width: 768px) {
        display: none;
      }
    }

    a, a:visited {
      color: #222;
      text-decoration: none;
    }

    a:hover {
      text-decoration: underline;
    }

  }
}
</style>