<script setup lang="ts">
import CommonAuthView from "@/components/view/auth/CommonAuthView.vue";
import {ref} from "vue";
import {ERROR_MESSAGES} from "@/constants/messages";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {useSystemModalStore} from "@/store/systemModalStore";
import {useSignUp} from "@/composables/auth/signUp";
import type {SignUpRequest} from "@/dto/Auth";

const loading = ref(false);
const errorMessage = ref();
const systemModalStore = useSystemModalStore();
const router = useRouter();

const onSubmitForm = (formValues: SignUpRequest) => {
  loading.value = true;
  const base64EncodedPassword = btoa(formValues.password);
  const request = {
    username: formValues.username,
    password: base64EncodedPassword,
    email: formValues.email
  }

  useSignUp(request)
      .then(() => {
        systemModalStore.setMessage("Ви успішно зареєструвалися! Перевірте вашу пошту для підтвердження email-адреси.");
        router.push({name: 'login'});
      })
      .catch((error) => {
        if (error.response.status === 409) {
          errorMessage.value = ERROR_MESSAGES.USERNAME_ALREADY_EXISTS;
        } else {
          errorMessage.value = ERROR_MESSAGES.SIGN_UP_FAILED;
        }
      })
      .finally(() => {
        loading.value = false;
      });
}

useSeoMeta
({
  description: 'Сторінка реєстрації'
});

useHead({
  title: 'Зареєструватися',
});
</script>

<template>
  <GeneralViewWrapper ref="viewWrapper">
    <common-auth-view
        :page-title="'Зареєструватися'"
        :is-login-page="false"
        @submitForm="onSubmitForm"
        :loading="loading"
        :error-message="errorMessage"
    >
      <template #form-footer>
        <div>Вже маєте акаунт?
          <RouterLink to="login">Залогіньтеся</RouterLink>
        </div>
      </template>
    </common-auth-view>
  </GeneralViewWrapper>
</template>

<style lang="less">
</style>