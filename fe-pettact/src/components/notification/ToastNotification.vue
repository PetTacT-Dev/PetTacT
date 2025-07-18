<template>
  <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999">
    <div
      v-for="(noti, index) in toasts"
      :key="index"
      class="toast show align-items-start text-bg-light border-0 mb-2 shadow-sm noti-toast"
      role="alert"
      @click="handleClick(noti, index)"
      :class="{ read: noti.isRead }"
      style="cursor: pointer"
    >
      <div class="d-flex">
        <div class="toast-body">
          <div class="d-flex">
            <strong>{{ noti.notificationTitle }}</strong>
            <span v-if="!noti.isRead" class="new-dot ms-2 mt-1"></span>
          </div>
          <small class="text-muted">{{ noti.notificationContent }}</small>
        </div>
        <button
          type="button"
          class="btn-close me-2 m-auto"
          @click.stop="removeToast(index)"
        ></button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useNotificationStore } from '@/stores/notification';
import { useRouter } from 'vue-router';
import { computed, watch } from 'vue';
import axios from 'axios';

const notificationStore = useNotificationStore();
const router = useRouter();

const toasts = computed(() => notificationStore.toastQueue);

const removeToast = (index) => {
  notificationStore.popToast();
};

const handleClick = async (noti, index) => {
  if (!noti.isRead) {
    await notificationStore.markAsRead(noti.notificationNo);
  }

  removeToast(index);

  const { targetType, targetId } = noti;

  switch (targetType) {
    case 'POST':
      try {
        const res = await axios.get(`/v1/board/${targetId}`);
        const categoryNo = res.data.responseDto.boardCategoryNo;
        router.push(`/board/${categoryNo}/${targetId}`);
      } catch (err) {
        console.error('게시글 정보 조회 실패', err);
      }
      break;
    case 'REPLY':
      router.push({ path: `/board/post/${targetId}` });
      break;
    case 'REPORT':
      router.push({ name: 'myReportDetail', params: { reportNo: targetId } });
      break;
    case 'USER':
      router.push({ name: 'myMarket' });
      break;
    default:
      console.warn('알 수 없는 알림 타입:', targetType);
  }
};

// 자동 제거: 5초 후 shift()
watch(toasts, (newVal) => {
  if (newVal.length > 0) {
    setTimeout(() => {
      notificationStore.popToast();
    }, 5000);
  }
});
</script>

<style scoped>
.toast-container {
  position: relative;
  width: 100%;
  display: flex;
  justify-content: right;
  margin-top: 120px;
}

.noti-toast.read {
  opacity: 0.7;
}

.toast-body {
  font-size: 14px;
}

.new-dot {
  width: 8px;
  height: 8px;
  background-color: #007bff;
  border-radius: 50%;
  display: inline-block;
}
</style>
