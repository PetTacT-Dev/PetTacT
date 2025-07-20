<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-form-section">
        <div class="login-header">
          <h1>비밀번호 재설정</h1>
          <p class="login-subtitle">새로운 비밀번호를 입력해주세요</p>
        </div>

        <!-- 로딩 중 -->
        <div v-if="loading" class="success-message">
          <span>토큰 확인 중...</span>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <!-- 성공 메시지 -->
        <div v-if="message" class="success-message">
          {{ message }}
        </div>

        <!-- 비밀번호 입력 -->
        <form v-if="verified" @submit.prevent="resetPassword" class="login-form">
          <input
            type="password"
            v-model="newPassword"
            placeholder="새 비밀번호"
            class="input-field"
            required
          />
          <button type="submit" class="login-button">비밀번호 변경</button>
        </form>

        <div class="account-links">
          <span>로그인하러 가기</span>
          <router-link :to="{ name: 'login' }">로그인</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

const token = route.query.token;
const userEmail = ref('');
const newPassword = ref('');
const loading = ref(true);
const verified = ref(false);
const error = ref('');
const message = ref('');

onMounted(async () => {
  try {
    const res = await axios.get('/v1/user/password/verify', { params: { token } });
    userEmail.value = res.data.email;
    verified.value = true;
  } catch (err) {
    error.value = err.response?.data || '유효하지 않거나 만료된 링크입니다.';
  } finally {
    loading.value = false;
  }
});

const resetPassword = async () => {
  try {
    const res = await axios.post('/v1/user/password/reset', {
      userEmail: userEmail.value,
      newPassword: newPassword.value
    });
    message.value = res.data;
    error.value = '';
    // 성공 후 로그인 화면으로 이동
    setTimeout(() => router.push('/user/login'), 2000);
  } catch (err) {
    message.value = '';
    error.value = err.response?.data || '오류가 발생했습니다.';
  }
};
</script>
<style scoped>
.login-container {
  width: 100%;
  max-width: 1173px;
  margin: 0 auto;
  padding: 70px 317px;
  background: white;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-wrapper {
  width: 100%;
  max-width: 539px;
  display: flex;
  flex-direction: column;
  gap: 39px;
}

.login-form-section {
  display: flex;
  flex-direction: column;
  gap: 39px;
}

.login-header h1 {
  font-family: 'Inter', sans-serif;
  font-weight: 700;
  font-size: 24px;
  line-height: 33.6px;
  color: black;
  text-align: center;
  margin: 0;
}

.login-subtitle {
  text-align: center;
  font-size: 14px;
  color: #888;
  margin-top: 8px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 50px;
}

.input-field {
  width: 90%;
  margin: 0 auto;
  height: 50px;
  padding: 6px 15px;
  background: white;
  border: none;
  border-radius: 5px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  font-family: 'Pretendard', sans-serif;
  font-weight: 300;
  font-size: 15px;
  line-height: 21px;
  color: black;
  box-sizing: border-box;
}

.input-field:focus {
  outline: 1px solid #008be6;
  box-shadow: 0 0 0 4px rgba(0, 139, 230, 0.2);
}

.login-button {
  width: 340px;
  height: 50px;
  background: #008be6;
  border: none;
  border-radius: 7px;
  color: white;
  font-family: 'Pretendard', sans-serif;
  font-weight: 600;
  font-size: 15px;
  line-height: 28px;
  cursor: pointer;
  align-self: center;
  transition: background-color 0.2s;
}

.success-message,
.error-message {
  font-size: 13px;
  text-align: center;
  padding: 10px;
}

.account-links {
  margin-top: 20px;
  font-size: 13px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
}
</style>
