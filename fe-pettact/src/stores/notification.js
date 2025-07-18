import axios from 'axios';
import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
    unreadCount: 0,
    toastQueue: []  // 토스트 알림 전용 큐
  }),
  actions: {
    async fetchNotifications() {
      try {
        const res = await axios.get('/v1/notification');
        this.notifications = res.data;
      } catch (err) {
        console.error('[알림 목록 조회 실패]', err);
      }
    },

    async fetchUnreadCount() {
      try {
        const res = await axios.get('/v1/notification/unread-count');
        this.unreadCount = res.data;
      } catch (err) {
        console.error('[읽지 않은 개수 조회 실패]', err);
      }
    },

    async markAsRead(notificationNo) {
      try {
        await axios.post(`/v1/notification/${notificationNo}/read`);
        const noti = this.notifications.find(n => n.notificationNo === notificationNo);
        if (noti) noti.isRead = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      } catch (err) {
        console.error('[알림 읽음 처리 실패]', err);
      }
    },

    async markAllAsRead() {
      try {
        await axios.post('/v1/notification/read-all');
        this.notifications.forEach(n => (n.isRead = true));
        this.unreadCount = 0;
      } catch (err) {
        console.error('[전체 읽음 처리 실패]', err);
      }
    },

    addNotification(notification) {
      console.log('[SSE] store에 도착한 알림:', notification);
      this.notifications.unshift(notification);
      this.unreadCount += 1;

      // ✅ 토스트 큐에도 추가
      this.toastQueue.push(notification);
    },

    async deleteNotification(notificationNo) {
      try {
        await axios.patch(`/v1/notification/${notificationNo}/delete`);
        this.notifications = this.notifications.filter(n => n.notificationNo !== notificationNo);
        console.log(`[알림 삭제] notificationNo=${notificationNo}`);
      } catch (err) {
        console.error('[알림 삭제 실패]', err);
      }
    },

    async deleteAllNotifications() {
      try {
        await axios.patch('/v1/notification/delete-all');
        this.notifications = [];
        this.unreadCount = 0;
      } catch (err) {
        console.error('[알림 전체 삭제 실패]', err);
      }
    },

    popToast() {
      this.toastQueue.shift();
    }
  }
});
