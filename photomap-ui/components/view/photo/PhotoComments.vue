<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import type {CommentDTO} from "@/dto/photo/Comment";
import NoContent from "@/components/common/NoContent.vue";
import {COMMENT_PAGE_SIZE} from "@/constants/apiDefaults";
import CommentInput from "@/components/view/photo/CommentInput.vue";
import {useSystemModalStore} from "@/store/systemModalStore";
import {formatToDateTime} from "@/utils/dateUtils";
import {useAuthStore} from "@/store/authStore";
import {CONFIRMATION_MESSAGES, TITLES} from "@/constants/messages";
import {useDeleteComment} from "@/composables/comment/deleteComment";
import {useGetCommentsByPhotoId} from "@/composables/comment/getCommentsByPhotoId";
import {useAddComment} from "@/composables/comment/addComment";
import ConfirmDialog from "@/components/modal/ConfirmDialog.vue";

const props = defineProps({
  photoId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['deleted']);

const comments = ref<CommentDTO[]>([]);
const page = ref(1);
const totalPages = ref(0);
const confirm = ref();
const authStore = useAuthStore();

const onDeleteClicked = (commentId: number) => {
  confirm.value.open(TITLES.DELETE_COMMENT, CONFIRMATION_MESSAGES.ARE_YOU_SURE, {color: '#B64038'})
      .then((confirm: any) => {
        if (confirm) {
          onDeleteComment(commentId);
        }
      });
};

const onDeleteComment = (commentId: number) => {
  useDeleteComment(props.photoId, commentId)
      .then(() => {
        emit('deleted');
        getComments()
      })
      .catch(() => {
        useSystemModalStore().setErrorMessage('Не вдалося видалити коментар');
      });
};

onMounted(() => {
  getComments();
});

const getComments = () => {
  useGetCommentsByPhotoId(props.photoId, page.value - 1, COMMENT_PAGE_SIZE)
      .then((response) => {
        comments.value = response.data.content;
        totalPages.value = response.data.totalPages;
      })
      .catch(() => {
        useSystemModalStore().setErrorMessage('Не вдалося завантажити коментарі');
      });
};

const addComment = (text: string) => {
  useAddComment(props.photoId, text)
      .then(() => getComments())
      .catch(() => {
        useSystemModalStore().setErrorMessage('Не вдалося додати коментар')
      });
};

const showDeleteButton = (userId: number) => {
  return authStore.isLoggedIn && userId === authStore.getUser?.id;
};

const updatePage = (newPage: number) => {
  if (newPage >= 0 && newPage <= totalPages.value) {
    page.value = newPage;
    getComments();
  }
}

const showPagination = computed(() => totalPages.value > 1);
const inputRef = ref();
const commentInputFocused = computed(() => inputRef.value.isInputFocused);

defineExpose({
  commentInputFocused
});
</script>

<template>
  <div class="comments-container">
    <label>Коментарі:</label>
    <div class="input-wrapper">
      <CommentInput
          ref="inputRef"
          :disabled="!authStore.isLoggedIn"
          @comment-added="addComment"
      />
    </div>
    <v-divider/>
    <div class="comments-wrapper" v-if="comments.length > 0">
      <ConfirmDialog ref="confirm"></ConfirmDialog>
      <div v-for="comment in comments" :key="comment.id" class="comment">
        <div class="comment-meta-row">
          <RouterLink
              :to="{name: 'user', params: {userId: comment.userId}}"
              class="user-name"
          >
            {{ comment.userName }}
          </RouterLink>
          <span class="created-at">{{ formatToDateTime(comment.createdAt) }}</span>
        </div>
        <div class="comment-text">{{ comment.text }}</div>
        <div class="comment-actions-row">
          <div v-if="showDeleteButton(comment.userId)">
            <div
                class="comment-action-button"
                @click="onDeleteClicked(comment.id)"
            >Видалити
            </div>
          </div>
        </div>
        <v-divider/>
      </div>
    </div>
    <div v-if="comments.length === 0">
      <NoContent message="Немає коментарів"/>
    </div>
    <div>
      <v-pagination
          :class="!showPagination ? 'no-pagination' : ''"
          :length="totalPages"
          :start="1"
          v-model="page"
          @update:model-value="updatePage"/>
    </div>
  </div>
</template>

<style scoped lang="less">
.comments-container {
  padding: 1rem 0 2rem;

  @media (min-width: 576px) {
    padding: 1rem 1rem 2rem;
  }

  @media (min-width: 768px) {
    padding: 1rem 2rem 2rem;
  }
}

.input-wrapper {
  margin: 1rem 0 2rem;
}

.comments-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;

  .comment {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .comment-meta-row {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;

    @media (min-width: 576px) {
      flex-direction: row;
      align-items: center;
      gap: 1rem;
    }

    @media (min-width: 768px) {
      gap: 0.5rem;
    }

    .created-at {
      color: #6B7280;
      font-size: 0.75rem;
    }

    .user-name,
    .user-name:visited {
      text-decoration: none;
      color: black;
    }
  }

  .comment-text {
    font-weight: 300;
  }

  .comment-actions-row {
    .comment-action-button {
      color: #B64038;
      font-size: 0.75rem;
      cursor: pointer;
    }
  }
}
</style>