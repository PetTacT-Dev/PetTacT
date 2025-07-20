<template>
  <div>
    <Header />
    <ToastNotification />
    <router-view :key="$route.fullPath" />

    <MessageModal v-if="modalStore.isMessageModalOpen" @close="modalStore.closeMessageModal()" />
  </div>
</template>

<script setup>
import { onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { useNotificationStore } from '@/stores/notification';
import { connectNotificationSSE, disconnectNotificationSSE } from '@/utils/sse/connectNotification';
import { useModalStore } from '@/js/modalStore';
import ToastNotification from './components/notification/ToastNotification.vue';
import Header from './components/Header.vue';
import MessageModal from '@/view/Chat/MessageModal.vue';

const userStore = useUserStore();
const notificationStore = useNotificationStore();
const modalStore = useModalStore();

// 초기 진입 시
onMounted(async () => {
  if (userStore.accessToken && !userStore.user) {
    await userStore.fetchUser();
  }
});

// accessToken이 설정되는 시점 감지
watch(
  () => userStore.accessToken,
  async (newToken) => {
    if (newToken) {
      await notificationStore.fetchNotifications();
      await notificationStore.fetchUnreadCount();

      connectNotificationSSE(newToken, (noti) => {
        notificationStore.addNotification(noti);
      });
    }
  },
  { immediate: true } // accessToken이 이미 있을 경우에도 즉시 실행
);
</script>
