<template>
  <div class="notification-item dropdown" ref="notiDropdownRef" @click="toggle">
    🔔
    <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>

    <div v-if="isOpen" class="dropdown-menu show" style="min-width: 300px; right: 0;">
      <div class="dropdown-item text-primary" @click="goToAllNotifications">전체 알림 보기</div>
      <div class="dropdown-divider"></div>

      <div v-if="notifications.length === 0" class="dropdown-item text-muted">알림 없음</div>

      <div
        v-for="noti in notifications.slice(0, 5)"
        :key="noti.notificationNo"
        class="dropdown-item noti-entry"
        :class="{ unread: !noti.isRead, read: noti.isRead }"
      >
        <div class="d-flex justify-content-between align-items-start w-100">
          <div class="flex-grow-1 noti-click-area" @click="handleNotiClick(noti)">
            <div class="d-flex align-items-center">
              <span class="noti-title">{{ noti.notificationTitle }}</span>
              <span v-if="!noti.isRead" class="new-dot"></span>
            </div>
            <div class="noti-content text-muted small mt-1">{{ noti.notificationContent }}</div>
            <div class="noti-time text-muted small text-end">{{ formatDate(noti.createdAt) }}</div>
          </div>
          <button class="btn btn-sm btn-close delete-btn" @click.stop="deleteNoti(noti.notificationNo)"></button>
        </div>
      </div>

      <div class="dropdown-divider"></div>
      <div class="dropdown-item text-primary" @click="markAllAsRead">모두 읽음</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import dayjs from 'dayjs'

const router = useRouter()
const notiStore = useNotificationStore()

const isOpen = ref(false)
const notiDropdownRef = ref(null)

const notifications = computed(() => notiStore.notifications)
const unreadCount = computed(() => notiStore.unreadCount)

const toggle = () => {
  isOpen.value = !isOpen.value
}

const formatDate = (isoDateStr) => {
  return dayjs(isoDateStr).fromNow()
}

const markAllAsRead = async () => {
  await notiStore.markAllAsRead()
}

const goToAllNotifications = () => {
  router.push({ name: 'notifications' })
  isOpen.value = false
}

const deleteNoti = async (id) => {
  await notiStore.deleteNotification(id)
}

const handleNotiClick = async (noti) => {
  isOpen.value = false

  if (!noti.isRead) {
    await notiStore.markAsRead(noti.notificationNo)
  }

  const { targetType, targetId } = noti

  switch (targetType) {
    case 'POST':
      try {
        const res = await fetch(`/v1/board/${targetId}`)
        const data = await res.json()
        const categoryNo = data.responseDto.boardCategoryNo
        router.push(`/board/${categoryNo}/${targetId}`)
      } catch (err) {
        console.error('게시글 정보 조회 실패', err)
      }
      break
    case 'REPLY':
      router.push(`/board/post/${targetId}`)
      break
    case 'REPORT':
      router.push({ name: 'myReportDetail', params: { reportNo: targetId } })
      break
    case 'USER':
      router.push({ name: 'myMarket' })
      break
    default:
      console.warn('알 수 없는 알림 타입:', targetType)
  }
}

const handleClickOutside = (e) => {
  if (
    isOpen.value &&
    notiDropdownRef.value &&
    !notiDropdownRef.value.contains(e.target)
  ) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.notification-item {
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.notification-item:hover {
  background: #f8f9fa;
}

.badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 11px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
  font-family: 'Pretendard', sans-serif;
}

/* 드롭다운 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  margin-top: 8px;
  padding: 8px 0;
  min-width: 300px;
}

/* 알림 항목 스타일 */
.noti-entry {
  padding: 10px 14px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  position: relative;
}

.noti-entry.read {
  background-color: #f8f9fa;
  color: #aaa;
}

.noti-entry.unread {
  background-color: #fff;
}

.new-dot {
  width: 8px;
  height: 8px;
  background-color: #007bff;
  border-radius: 50%;
  display: inline-block;
  margin-left: 6px;
}

.noti-title {
  font-weight: bold;
  font-size: 14px;
}

.noti-content {
  font-size: 13px;
  color: #666;
}

.noti-time {
  font-size: 12px;
  color: #aaa;
}

/* ❌ 버튼 */
.delete-btn {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.noti-entry:hover .delete-btn {
  opacity: 1;
}
</style>
