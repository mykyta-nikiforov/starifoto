<script setup lang="ts">
import CreativeCommonsIcon from "@/assets/icons/licenses/cc_by_sa.svg";
import {useAuthStore} from "@/store/authStore";
import {useLogout} from "@/composables/auth/logout";

const authStore = useAuthStore();

</script>

<template>
  <div class="footer main-padding">
    <div class="footer-inner-container max-width-wrapper">
      <div class="links-container">
        <div class="links-section">
          <NuxtLink to="/">
            <div class="logo-text">
              старіфото.укр
            </div>
          </NuxtLink>
          <div class="akk-footer-column akk-footer-column-border">
            <NuxtLink to="/about">Про проєкт</NuxtLink>
            <NuxtLink to="/photos/1">Усі фото</NuxtLink>
            <NuxtLink to="/login" v-if="!authStore.isLoggedIn">Увійти</NuxtLink>
            <a href="/logout" @click.prevent="useLogout()" v-if="authStore.isLoggedIn">Вийти</a>
          </div>
        </div>
        <div class="links-section">
          <div class="akk-footer-column akk-footer-column-border">
            <NuxtLink to="/privacy">Політика конфіденційності</NuxtLink>
            <NuxtLink to="/disclaimer">Відмова від відповідальності</NuxtLink>
          </div>
          <div class="akk-footer-column">
            <a href="https://www.facebook.com/people/Аккерманіка/100091050579287/" target="_blank">Facebook</a>
            <a href="https://instagram.com/akkermanika" target="_blank">Instagram</a>
            <a href="https://www.patreon.com/akkermanika" target="_blank">Patreon</a>
          </div>
        </div>
      </div>
      <div class="footer-licence">
        <div class="licence-item cc-info">
          <img :src="CreativeCommonsIcon" alt="Creative Commons" class="licence-icon">
          <div class="licence-text">Вміст доступний на умовах <a
              href="https://creativecommons.org/licenses/by-sa/4.0/deed.uk"
              target="_blank">Creative Commons Attribution Share
            Alike</a>,
            якщо не вказано інше
          </div>
        </div>
        <div class="licence-item mediawiki-info">
          <img :src="CreativeCommonsIcon" alt="Creative Commons" class="licence-icon" style="opacity: 0;">
          <div class="licence-text">Дизайн розроблений
            <a href="https://sokolov.agency" target="_blank">Sokolov Agency</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
.footer {
  font-size: 14px;
  line-height: 20px;
  background-color: rgb(17, 24, 39);

  .footer-inner-container {
    display: flex;
    flex-direction: column;
    margin-left: auto;
    margin-right: auto;

    a,
    a:visited {
      color: #FFFFFF;
    }

    a {
      text-decoration: none;
    }

    a:hover {
      text-decoration: underline;
    }

    .links-container {
      display: flex;
      flex-direction: column;
      padding-top: 2rem;
      border-bottom: 1px solid rgb(107, 114, 128);

      @media (min-width: 1024px) {
        flex-direction: row;
        padding-top: 4rem;
        padding-bottom: 4rem;
      }

      .links-section {
        display: flex;
        flex-direction: column;
        width: 100%;
        justify-content: space-between;

        @media (min-width: 1024px) {
          flex-direction: row;
          width: 50%;
        }

        .logo-text {
          color: #FFD431;
          width: 50%;
          font-weight: bold;
          font-size: 1.25rem;
          line-height: 2rem;
        }
      }
    }

    .akk-footer-column {
      display: flex;
      flex-direction: column;
      justify-content: start;
      margin-top: 1.25rem;

      @media (min-width: 1024px) {
        width: 50%;
        margin-top: 0;
      }

      a {
        margin-bottom: 1rem;
      }
    }

    .akk-footer-column-border {
      border-bottom: 1px solid rgb(107, 114, 128);

      @media (min-width: 1024px) {
        border: none;
      }
    }

    .footer-licence {
      padding-top: 1rem;
      padding-bottom: 4rem;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;

      @media (min-width: 1024px) {
        padding-top: 4rem;
      }

      .licence-item {
        display: flex;
        flex-direction: row-reverse;
        justify-content: space-between;

        @media (min-width: 1024px) {
          flex-direction: row;
          justify-content: flex-start;
        }

        .licence-text {
          color: rgb(156, 163, 175);
        }

        .licence-icon {
          height: 1.5rem;
          width: auto;

          @media (min-width: 1024px) {
            margin-right: 1rem;
          }
        }
      }

      .cc-info {
        align-items: flex-start;
      }
    }
  }
}
</style>