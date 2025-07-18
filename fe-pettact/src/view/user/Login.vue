<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h2 class="text-center mb-4">로그인 하시고 다양한 혜택을 누려보세요</h2>

    <div v-if="errorMessage" style="color:red;">
      {{ errorMessage }}
    </div>

    <form @submit.prevent="handleLogin">
      <div class="mb-3">
        <label for="userEmail" class="form-label visually-hidden"></label>
        <input type="email" v-model="userEmail" id="userEmail" class="form-control" placeholder="이메일을 입력해주세요">
      </div>

      <div class="mb-3">
        <label for="userPassword" class="form-label visually-hidden">비밀번호:</label>
        <input type="password" v-model="userPassword" id="userPassword" class="form-control"
          placeholder="비밀번호를 입력해주세요" />
        <!-- <span class="input-group-text">
          <i class="bi bi-eye"></i>
        </span> -->
      </div>

      <div class="d-flex justify-content-between mb-3">
        <div class="form-check">
          <input class="form-check-input" type="checkbox" id="autoLogin">
          <label class="form-check-label" for="autoLogin">자동 로그인</label>
        </div>
        <div class="form-check form-switch">
          <input class="form-check-input" type="checkbox" id="saveEmail" v-model="saveEmail">
          <label class="form-check-label" for="saveEmail">아이디 저장</label>
        </div>
      </div>

      <button type="submit" class="btn btn-primary w-100 mb-3">로그인</button>

      <div class="d-flex justify-content-between mb-3">
        <router-link :to="{ name: 'emailFind' }">이메일 찾기</router-link>
        <router-link :to="{ name: 'passwordFind' }">비밀번호 찾기</router-link>
        <router-link :to="{ name: 'join' }">회원가입</router-link>
      </div>

      <div class="text-center mb-3">
        <hr class="flex-grow-1">
        <span>SNS로 로그인</span>
        <hr class="flex-grow-1">
      </div>

      <div class="d-grid gap-2">
        <button class="btn btn-light border" @click="loginWithGoogle">Google 로그인</button>
        <button class="btn btn-light border" @click="loginWithNaver">Naver 로그인</button>
        <button class="btn btn-light border" @click="loginWithKakao">KaKao 로그인</button>
      </div>
    </form>
    <button @click="logout">
      로그아웃
    </button>

    <div v-if="oauth2ErrorMessage" style="color:red;">
      {{ decodeURIComponent(oauth2ErrorMessage) }}
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
const saveEmail = ref(false);

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 아이디 저장
onMounted(() => {
  const storedEmail = localStorage.getItem('savedEmail');
  if (storedEmail) {
    userEmail.value = storedEmail;
    saveEmail.value = true;
  }
});

const errorMessages = {
  EMAIL_ALREADY_EXISTS: "이미 다른 플랫폼(구글/카카오/네이버)으로 가입된 이메일입니다."
};

const errorCode = route.query.error;
const oauth2ErrorMessage = errorMessages[errorCode] || errorMessages.DEFAULT;

const handleLogin = async () => {
  errorMessage.value = '';
  try {
    await userStore.login(userEmail.value, userPassword.value);
    console.log('로그인 성공');
    console.log('토큰' + userStore.accessToken)
    if (saveEmail.value) {
      localStorage.setItem('savedEmail', userEmail.value);
      console.log('저장된 아이디' , userEmail.value)
    } else {
      localStorage.removeItem('savedEmail');
    }
    router.push('/');
  } catch (error) {
    console.error('로그인 실패:', error);
    errorMessage.value = '로그인 실패. 아이디/비밀번호를 확인하세요.';
  }
};

const logout = () => {
  userStore.logout();
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