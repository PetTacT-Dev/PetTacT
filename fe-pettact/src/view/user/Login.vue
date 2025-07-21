<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-form-section">
        <div class="login-header">
          <h1>로그인 하시고<br />다양한 혜택을 누려보세요</h1>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="input-group">
            <input
              v-model="userEmail"
              type="email"
              placeholder="이메일을 입력해주세요"
              class="input-field"
              required
            />
            <input
              v-model="userPassword"
              type="password"
              placeholder="비밀번호를 입력해주세요"
              class="input-field"
              :class="{ focused: isPasswordFocused }"
              @focus="isPasswordFocused = true"
              @blur="isPasswordFocused = false"
              required
            />
          </div>

          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <div class="login-options">
            <label class="checkbox-wrapper">
              <input type="checkbox" v-model="autoLogin" class="checkbox" />
              <span class="checkmark"></span>
              자동 로그인
            </label>
            <label class="checkbox-wrapper">
              <input type="checkbox" v-model="saveEmail" class="checkbox" />
              <span class="checkmark"></span>
              아이디 저장
            </label>
          </div>

          <button type="submit" class="login-button">
            로그인
          </button>
        </form>

        <div class="account-links">
          <router-link :to="{ name: 'emailFind' }">이메일 찾기</router-link>
          <div class="divider">|</div>
          <router-link :to="{ name: 'passwordFind' }">비밀번호 찾기</router-link>
          <div class="divider">|</div>
          <router-link :to="{ name: 'join' }">회원가입</router-link>
        </div>

        <div class="divider-section">
          <div class="divider-line"></div>
          <div class="divider-text">SNS로 로그인</div>
          <div class="divider-line"></div>
        </div>

        <div class="social-login-section">
          <div class="social-buttons">
            <button class="social-button" @click="loginWithGoogle">
              <div class="social-icon"><img src="@/assets/social-login/social_google.png" alt="Google" /></div>
              Google 로그인
            </button>
            <button class="social-button" @click="loginWithNaver">
              <div class="social-icon"><img src="@/assets/social-login/social_naver.png" alt="Naver" /></div>
              Naver 로그인
            </button>
            <button class="social-button" @click="loginWithKakao">
              <div class="social-icon"><img src="@/assets/social-login/social_kakao.png" alt="Kakao" /></div>
              Kakao 로그인
            </button>
          </div>
        </div>

        <div v-if="oauth2ErrorMessage" class="error-message">
          {{ decodeURIComponent(oauth2ErrorMessage) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const userEmail = ref('');
const userPassword = ref('');
const errorMessage = ref('');
const oauth2ErrorMessage = ref('');
const isPasswordFocused = ref(false);
const autoLogin = ref(false);
const saveEmail = ref(false);

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const errorMessages = {
  EMAIL_ALREADY_EXISTS: "이미 다른 플랫폼(구글/카카오/네이버)으로 가입된 이메일입니다."
};

onMounted(() => {
  const storedEmail = localStorage.getItem('savedEmail');
  if (storedEmail) {
    userEmail.value = storedEmail;
    saveEmail.value = true;
  }

  const errorCode = route.query.error;
  if (errorCode && errorMessages[errorCode]) {
    oauth2ErrorMessage.value = errorMessages[errorCode];
  }
});

const handleLogin = async () => {
  errorMessage.value = '';
  try {
    await userStore.login(userEmail.value, userPassword.value);
    console.log('로그인 성공');
    console.log('토큰:', userStore.accessToken);
    if (saveEmail.value) {
      localStorage.setItem('savedEmail', userEmail.value);
    } else {
      localStorage.removeItem('savedEmail');
    }
    router.push('/');
  } catch (error) {
    console.error('로그인 실패:', error);
    errorMessage.value = '로그인 실패. 아이디/비밀번호를 확인하세요.';
  }
};

const loginWithGoogle = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google';
};
const loginWithKakao = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
};
const loginWithNaver = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/naver';
};
</script>

<style scoped>
.login-container {
  width: 90%;
  max-width: 1173px;
  margin: 0 auto;
  padding: 70px 317px;
  background: white;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
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
.login-form {
  display: flex;
  flex-direction: column;
  gap: 26px;
}
.input-group {
  display: flex;
  flex-direction: column;
  gap: 25px;
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
.input-field:focus,
.input-field.focused {
  outline: 1px solid #008be6;
  outline-offset: -1px;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}
.error-message {
  color: #ee0000;
  font-family: 'Pretendard', sans-serif;
  font-weight: 300;
  font-size: 13px;
  line-height: 18.2px;
  text-align: center;
  padding: 5px 0;
}
.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-family: 'Pretendard', sans-serif;
  font-weight: 300;
  font-size: 15px;
  line-height: 21px;
  color: black;
}
.checkbox {
  width: 20px;
  height: 20px;
  background: #e6e6e6;
  border: 1px solid rgba(0, 0, 0, 0.07);
  border-radius: 5px;
  cursor: pointer;
}
.checkbox:checked {
  background: #008be6;
  border-color: #008be6;
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
  color: black;
  text-decoration: none;
  padding: 1px 8px;
}
.account-links a:hover {
  text-decoration: underline;
}
.divider {
  color: #d9d9d9;
}
.divider-section {
  display: flex;
  align-items: center;
  gap: 3px;
}
.divider-line {
  flex: 1;
  height: 1px;
  background: #d9d9d9;
}
.divider-text {
  padding: 4px 9px;
  font-family: 'Pretendard', sans-serif;
  font-weight: 300;
  font-size: 13px;
  line-height: 18.2px;
  color: black;
  text-align: center;
}
.social-login-section {
  width: 340px;
  align-self: center;
}
.social-buttons {
  display: flex;
  flex-direction: column;
  gap: 28px;
}
.social-button {
  width: 100%;
  height: 50px;
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.17);
  border-radius: 7px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  font-family: 'Pretendard', sans-serif;
  font-weight: 400;
  font-size: 15px;
  line-height: 21px;
  color: #111111;
  transition: background-color 0.2s;
}
.social-button:hover {
  background: #f5f5f5;
}
.social-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}
@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }
  .login-wrapper {
    max-width: 100%;
  }
  .login-button,
  .social-login-section {
    width: 100%;
  }
}
</style>
