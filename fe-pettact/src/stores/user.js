import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';
import { connectNotificationSSE, disconnectNotificationSSE } from '@/utils/sse/connectNotification';

export const useUserStore = defineStore('user', () => {
  const user = ref(null);
  const accessToken = ref(localStorage.getItem('accessToken'));

  const login = async (email, password) => {
    const res = await axios.post('/v1/user/login', {
      userEmail: email,
      userPassword: password,
    });

    accessToken.value = res.data.accessToken;
    localStorage.setItem('accessToken', accessToken.value);

    await fetchUser();

    connectNotificationSSE(accessToken.value, (data) => {
      alert(`${data.notificationTitle}`);
    });
  };

  const fetchUser = async () => {
    if (!accessToken.value) return;
    try {
      const res = await axios.get('/v1/user/me', {
        headers: {
          Authorization: `Bearer ${accessToken.value}`,
        },
      });
      user.value = res.data;
    } catch (err) {
      console.error('유저 정보 조회 실패', err);
      logout();
    }
  };

  const logout = () => {
    user.value = null;
    accessToken.value = null;
    localStorage.removeItem('accessToken');
    disconnectNotificationSSE();
  };

  const restoreUserFromToken = async () => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      accessToken.value = token;
      await fetchUser();
    }
  };

  return {
    user,
    accessToken,
    login,
    fetchUser,
    logout,
    restoreUserFromToken,
  };
});