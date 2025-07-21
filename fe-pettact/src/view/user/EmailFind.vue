<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-form-section">
        <div class="login-header">
          <h1>아이디(이메일) 찾기</h1>
          <p class="login-subtitle">가입할 때 사용한 이름과 전화번호를 입력해주세요</p>
        </div>

        <form @submit.prevent="findEmail" class="login-form">
          <div class="input-group">
            <input
              v-model="userName"
              type="text"
              placeholder="이름을 입력해주세요"
              class="input-field"
              required
            />
            <input
              v-model="userTel"
              type="tel"
              placeholder="전화번호를 입력해주세요"
              class="input-field"
              :class="{ error: error }"
              required
            />
          </div>

          <div v-if="error" class="error-message">
            {{ error }}
          </div>

          <button type="submit" class="login-button">아이디 찾기</button>
        </form>

        <div v-if="userEmail" class="success-message">
          위의 정보로 가입된 이메일: <strong>{{ userEmail }}</strong>
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
import axios from 'axios';
import { ref } from 'vue';

const userName = ref('');
const userTel = ref('');
const userEmail = ref('');
const error = ref('');

const findEmail = async () => {
  try {
    const res = await axios.post('/v1/user/email/find', {
      userName: userName.value,
      userTel: userTel.value,
    });
    userEmail.value = res.data.userEmail;
    error.value = '';
  } catch (err) {
    userEmail.value = '';
    error.value = err.response?.data || '정보를 찾을 수 없습니다.';
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
  width: 100%;
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
  font-size: 20px;
  line-height: 28px;
  cursor: pointer;
  align-self: center;
  transition: background-color 0.2s;
}

.login-button:hover {
  background: #0066cc;
}

.error-message {
  color: #ee0000;
  font-family: 'Pretendard', sans-serif;
  font-weight: 300;
  font-size: 13px;
  line-height: 18.2px;
  text-align: left;
  padding-left: 2px;
}

.success-message {
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #005ea8;
  background: #e7f5ff;
  padding: 15px;
  border-radius: 7px;
  margin-top: 15px;
  text-align: center;
}

.account-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-family: 'Pretendard', sans-serif;
  font-weight: 100;
  font-size: 13px;
  line-height: 18.2px;
}

.account-links a {
  color: #0033cc;
  text-decoration: none;
}

.account-links a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }

  .login-wrapper {
    max-width: 100%;
  }

  .login-button {
    width: 100%;
  }
}
</style>
