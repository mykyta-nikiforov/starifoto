<script setup lang="ts">
import {nextTick, ref} from "vue";
import {TOOLTIP_MESSAGES} from "@/constants/messages";

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['comment-added']);

const commentText = ref('');
const commentRef = ref();
const form = ref();
const isFormValid = ref(false);
const addComment = async () => {
  commentText.value = commentText.value.trim();
  await nextTick(() => form.value.validate());
  if (!isFormValid.value) {
    return;
  }
  emit('comment-added', commentText.value);
  clearComment();
};

const clearComment = () => {
  commentText.value = '';
  commentRef.value.reset();
};

const isInputFocused = ref(false);
defineExpose({
  isInputFocused
});
</script>

<template>
  <v-form ref="form" v-model="isFormValid">
    <v-textarea
        v-model="commentText"
        :disabled="props.disabled"
        label="Ваш коментар…"
        validate-on="submit"
        rows="4"
        auto-grow
        :rules="[v => !!v || 'Коментар не може бути порожнім']"
        @update:focused="isInputFocused = $event"
    ></v-textarea>
    <div class="button-wrapper disable-dbl-tap-zoom">
      <v-btn
          @click="addComment"
          class="mt-2"
          :disabled="props.disabled"
      >
        Опублікувати
      </v-btn>
    </div>
    <v-tooltip
        v-if="disabled"
        activator="parent"
        location="bottom center"
        origin="bottom center"
    >{{TOOLTIP_MESSAGES.NEED_AUTH_FOR_COMMENT}}
    </v-tooltip>
  </v-form>
</template>

<style scoped lang="less">
.button-wrapper {
  width: fit-content;
}
</style>