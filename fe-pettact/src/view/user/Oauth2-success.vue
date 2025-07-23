<template>
  <div>소셜 로그인 처리중...</div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { jwtDecode } from 'jwt-decode';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

onMounted(async () => {
  const accessToken = route.query.accessToken;
  const refreshToken = route.query.refreshToken;

  if (accessToken && refreshToken) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    
    const decoded = jwtDecode(accessToken);
    
    userStore.accessToken = accessToken;
    await userStore.fetchUser();

    if (decoded.userStatus === 'STATUS_SOCIAL_PENDING') {
      router.push({ name: 'socialJoin' });
    } else {
      router.push('/');
    }
  } else {
    alert('소셜 로그인 실패');
    router.push('/login');
  }
});
</script>

