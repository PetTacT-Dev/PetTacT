<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-form-section">
        <div class="login-header">
          <h1>비밀번호 찾기</h1>
          <p class="login-subtitle">가입할 때 사용한 이메일을 입력 후 인증해주세요</p>
        </div>

        <form @submit.prevent="sendResetMail" class="login-form">
          <input
            type="email"
            v-model="userEmail"
            placeholder="이메일을 입력해주세요"
            class="input-field"
            required
          />

          <div v-if="error" class="error-message">
            {{ error }}
          </div>

          <button type="submit" class="login-button">비밀번호 재설정 메일 보내기</button>
        </form>

        <div v-if="message" class="success-message">
          {{ message }}
        </div>

        <div class="account-links">
          <span>아이디가 기억나셨나요?</span>
          <router-link :to="{ name: 'login' }">로그인</router-link>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref } from 'vue';
import axios from 'axios';

const userEmail = ref('');
const message = ref('');
const error = ref('');

const sendResetMail = async () => {
  try {
    const res = await axios.post('/v1/user/password/send', null, {
      params: { userEmail: userEmail.value }
    });
    message.value = res.data; // "비밀번호 재설정 링크를 이메일로 전송했습니다." 예상
    error.value = '';
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

.input-group {
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

.input-field.error {
  outline: 1px solid #ee0000;
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
