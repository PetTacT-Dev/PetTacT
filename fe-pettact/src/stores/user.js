import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';
import { connectNotificationSSE, disconnectNotificationSSE } from '@/utils/sse/connectNotification';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: ref(null),
    accessToken: ref(localStorage.getItem('accessToken')),
  }),
  actions: {
    async login(email, password) {
      const res = await axios.post('/v1/user/login', {
        userEmail: email,
        userPassword: password,
      });

      this.accessToken = res.data.accessToken;
      localStorage.setItem('accessToken', this.accessToken);

      await this.fetchUser();

      connectNotificationSSE(this.accessToken, (data) => {
        alert(`${data.notificationTitle}`);
      });
    },

    async fetchUser() {
      if (!this.accessToken) return;
      try {
        const res = await axios.get('/v1/user/me', {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
          },
        });
        this.user = res.data;
      } catch (err) {
        console.error('유저 정보 조회 실패', err);
        this.logout();
      }
    },

    logout() {
      this.user = null;
      this.accessToken = null;
      localStorage.removeItem('accessToken');
      disconnectNotificationSSE();
    },
  },
});
