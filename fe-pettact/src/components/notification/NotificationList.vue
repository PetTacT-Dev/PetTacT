<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>실시간 알림 목록</h4>
      <div>
        <button class="btn btn-sm btn-outline-secondary me-2" @click="markAll">모두 읽음</button>
        <button class="btn btn-sm btn-outline-danger" @click="deleteAll">전체 삭제</button>
      </div>
    </div>

    <div v-if="notificationStore.notifications.length === 0" class="text-muted">
      알림이 없습니다.
    </div>

    <ul class="list-group">
      <li
        v-for="noti in notificationStore.notifications"
        :key="noti.notificationNo"
        class="list-group-item noti-entry d-flex justify-content-between align-items-start"
        :class="{ read: noti.isRead }"
      >
        <div class="flex-grow-1 noti-click-area" @click="handleClick(noti)">
          <div class="d-flex align-items-center">
            <span class="noti-title">{{ noti.notificationTitle }}</span>
            <span v-if="!noti.isRead" class="new-dot"></span>
          </div>
          <div class="noti-content text-muted small mt-1">{{ noti.notificationContent }}</div>
          <div class="noti-time text-muted small text-end">{{ formatDate(noti.createdAt) }}</div>
        </div>

        <button class="btn btn-sm btn-close delete-btn ms-2" @click.stop="deleteNoti(noti.notificationNo)"></button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import dayjs from 'dayjs';
import { useNotificationStore } from '@/stores/notification';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const notificationStore = useNotificationStore();

onMounted(() => {
  if (userStore.accessToken) {
    notificationStore.fetchNotifications();
    notificationStore.fetchUnreadCount();
  }
});

const handleClick = async (noti) => {
  if (!noti.isRead) {
    await notificationStore.markAsRead(noti.notificationNo);
  }

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
      router.push(`/board/post/${targetId}`);
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

const formatDate = (isoDateStr) => {
  return dayjs(isoDateStr).fromNow();
};

const markAll = async () => {
  await notificationStore.markAllAsRead();
};

const deleteAll = async () => {
  await notificationStore.deleteAllNotifications();
};

const deleteNoti = async (id) => {
  await notificationStore.deleteNotification(id);
};
</script>

<style scoped>
.noti-entry {
  padding: 12px 16px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  position: relative;
  cursor: pointer;
}

.noti-entry.read {
  background-color: #f8f9fa;
  color: #aaa;
}

.noti-title {
  font-weight: bold;
  font-size: 15px;
}

.noti-content {
  font-size: 13px;
  color: #666;
}

.noti-time {
  font-size: 12px;
  color: #aaa;
  text-align: right;
}

.new-dot {
  width: 8px;
  height: 8px;
  background-color: #007bff;
  border-radius: 50%;
  display: inline-block;
  margin-left: 6px;
}

.delete-btn {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.noti-entry:hover .delete-btn {
  opacity: 1;
}
</style>
