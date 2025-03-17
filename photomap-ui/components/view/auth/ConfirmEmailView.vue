<script setup lang="ts">
import {onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {useSystemModalStore} from "@/store/systemModalStore";
import {useConfirmEmail} from "@/composables/auth/confirmEmail.js";

const router = useRouter();
const route = useRoute();
const systemMessageModalStore = useSystemModalStore();

onMounted(() => {
  const {token} = route.query;
  if (token && typeof token === 'string') {
    useConfirmEmail(token)
        .then(() => {
          systemMessageModalStore.setMessage("Пошта підтверджена! Тепер ви можете увійти.");
          router.push({name: 'login'});
        })
        .catch(() => {
          systemMessageModalStore.setErrorMessage("Пошта не підтверджена, трапилася помилка.");
          router.push({name: 'login'});
        });
  } else {
    router.push({name: 'login'});
  }
});
</script>