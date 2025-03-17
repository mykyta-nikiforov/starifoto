<script setup lang="ts">
import {computed, defineEmits, ref} from "vue";
import {TOOLTIP_MESSAGES} from "@/constants/messages";
import {handleSuccessAuth} from "@/utils/authUtils";
import {useGoogleAuth} from "@/composables/auth/google";
import {GoogleLogin} from "vue3-google-login";
import {useSystemModalStore} from "@/store/systemModalStore";
import {VALIDATION_RULES} from "../../../constants/validationRules";
import {VForm} from "vuetify/components";
import LargeButton from "../../buttons/LargeButton.vue";

const props = defineProps({
  pageTitle: {
    type: String,
    required: true
  },
  isLoginPage: {
    type: Boolean,
    required: true
  },
  loading: {
    type: Boolean,
    required: true
  },
  errorMessage: {
    type: String,
    required: false
  }
})

const emit = defineEmits(['submitForm']);
const email = ref(null);
const password = ref(null);
const username = ref(null);

const isFieldsFilled = computed(() => {
  return email.value && password.value && (props.isLoginPage ? true : username.value);
});

const onSubmit = () => {
  emit('submitForm', {
    email: email.value,
    password: password.value,
    username: username.value
  });
}

const onGoogleAuthSuccess = async (googleResponse: any) => {

  useGoogleAuth(googleResponse.credential)
      .then((response) => {
        let authResponse = response.data;
        if (authResponse.accessToken) {
          handleSuccessAuth(authResponse);
        } else {
          // no auth
        }
      })
      .catch((error) => {
        if (error.status === 403) {
          // error
        }
        console.log(error);
        useSystemModalStore().setErrorMessage(error.message);
      });
}

const form = ref<typeof VForm>();
const isFormValid = ref(false);
const isButtonActive = computed(() => {
  return isFormValid.value;
});

</script>

<template>
  <div class="user-settings-container">
    <div class="login-form-container">
      <h1>{{ pageTitle }}</h1>
      <client-only>
        <div class="google-login-container">
          <GoogleLogin :callback="onGoogleAuthSuccess"/>
        </div>
      </client-only>
      <hr class="divider-line"/>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div class="email-password-login-container">
        <v-form ref="form" v-model="isFormValid" class="auth-form">
          <div class="input-field" v-if="!isLoginPage">
            <label for="username" class="input-field-label">Ім'я користувача</label>
            <v-text-field
                id="username"
                v-model="username"
                placeholder="Ваше ім'я"
                :rules="[VALIDATION_RULES.REQUIRED]"
            />
          </div>
          <div class="input-field">
            <label for="email" class="input-field-label">Електронна пошта</label>
            <v-text-field
                id="email"
                v-model="email"
                placeholder="email@domain.ua"
                :rules="[VALIDATION_RULES.REQUIRED, VALIDATION_RULES.EMAIL]"
            />
          </div>
          <div class="input-field">
            <div class="password-label-wrapper">
              <label for="password" class="input-field-label">Пароль</label>
              <small>
                <NuxtLink to="reset-password">Забули пароль?</NuxtLink>
              </small>
            </div>
            <v-text-field
                id="password"
                v-model="password"
                placeholder="Ваш пароль"
                type="password"
                :rules="[VALIDATION_RULES.REQUIRED, VALIDATION_RULES.MIN_CHARS_8]"
            />
          </div>
          <div class="submit-container">
            <LargeButton
                :label="pageTitle"
                :disabled="!isButtonActive"
                :loading="loading"
                @click="onSubmit"
                width='300px'
            />
            <v-tooltip
                v-if="!isButtonActive"
                activator="parent"
                location="top"
            >{{ TOOLTIP_MESSAGES.NOT_FILLED_IN }}
            </v-tooltip>
          </div>
        </v-form>
      </div>
      <div class="sign-in-footer">
        <slot name="form-footer"></slot>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.user-settings-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form-container {
  width: 400px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}

.email-password-login-container {
  width: 100%;
  display: flex;
  justify-content: start;
}

.login-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 2rem;

  .photomap-button {
    width: 100%;
    max-width: 100%;
    position: relative;
    min-height: 56px;

    .loader {
      margin: auto;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      position: absolute;
    }
  }
}

.sign-in-footer {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
}

.google-login-container {
  margin-top: 1rem;
  height: 44px;
}

.error-message {
  width: 100%;
  height: 44px;
  border: 1px solid #EF4444;
  border-radius: 8px;
  background-color: #FEE2E2;
  color: #EF4444;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.875rem;
  line-height: 1.25rem;
  padding: 0 1rem;
  box-sizing: border-box;
  margin-bottom: 1rem;
}

.auth-form {
  width: 100%;

  .input-field {
    margin-bottom: 1rem;
  }
}

.submit-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.password-label-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;

  a {
    text-decoration: none;
    color: black;
  }
}
</style>