<script setup lang="ts">
import CommonAuthView from "@/components/view/auth/CommonAuthView.vue";
import {handleSuccessAuth} from "@/utils/authUtils";
import {ref} from "vue";
import {ERROR_MESSAGES} from "@/constants/messages";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {useLogin} from "@/composables/auth/login";
import type {SignUpRequest} from "@/dto/Auth.js";
import {useAuthStore} from "../store/authStore";

const loading = ref(false);
const errorMessage = ref();

const onSubmitForm = (formValues: SignUpRequest) => {
  loading.value = true;
  const base64EncodedPassword = btoa(formValues.password);
  const request = {
    password: base64EncodedPassword,
    email: formValues.email
  }

  useLogin(request)
      .then((response) => {
        handleSuccessAuth(response.data)
      })
      .catch((error) => {
        if (error.response.status === 418) {
          errorMessage.value = ERROR_MESSAGES.UNCONFIRMED_EMAIL;
        } else {
          errorMessage.value = ERROR_MESSAGES.AUTHENTICATION_FAILED;
        }
      })
      .finally(() => {
        loading.value = false;
      })
}

definePageMeta
({
  middleware: [
    function () {
      if(useAuthStore().isLoggedIn) {
        return navigateTo('/user/' + useAuthStore().user?.id);
      }
    }
  ],
});

useSeoMeta
({
  description: 'Сторінка авторизації, логін'
});

useHead({
  title: 'Увійти',
});
</script>

<template>
  <GeneralViewWrapper ref="viewWrapper">
    <common-auth-view
        :page-title="'Увійти'"
        :is-login-page="true"
        @submitForm="onSubmitForm"
        :loading="loading"
        :error-message="errorMessage"
    >
      <template #form-footer>
        <div>Ще не зареєстровані?
          <RouterLink to="sign-up">Реєструйтеся</RouterLink>
        </div>
      </template>
    </common-auth-view>
  </GeneralViewWrapper>
</template>

<style lang="less">
</style>