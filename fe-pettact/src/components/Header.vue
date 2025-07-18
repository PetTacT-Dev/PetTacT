<template>
  <div class="navbar">
    <div class="logo" @click="goHome">PETTACT</div>
    <div class="menu">
      <div class="menu-item">유기동물 조회</div>
      <div class="menu-item" @click="goToUserPet">반려동물</div>
      <div class="menu-item">쇼핑몰</div>
      <div class="menu-item" @click="goToBoardCategoryList">커뮤니티</div>
    </div>

    <div class="auth-section">
      <div v-if="isLoggedIn" class="user-section">
        <!-- 🔔 알림 드롭다운 -->
        <div class="notification-area">
            <NotificationDropdown />
        </div>

        <!-- 👤 사용자 드롭다운 -->
        <div class="user-profile" ref="profileDropdownRef" @click="toggleProfileDropdown">
          <span class="user-name">{{ userNickname }}</span>
          <div class="dropdown-menu" :class="{ show: isProfileDropdownOpen }">
            <div class="dropdown-item" @click="goToMyInfo">마이페이지</div>
            <div class="dropdown-item" @click="goToUpdateProfile">개인정보 수정</div>
            <div v-if="isAdmin" class="dropdown-item" @click="goToAdminDashboard">관리자 페이지</div>
            <div class="dropdown-divider"></div>
            <div class="dropdown-item logout" @click="logout">로그아웃</div>
          </div>
        </div>
      </div>

      <div v-else class="auth-buttons">
        <button class="login-button" @click="goToLogin">로그인</button>
        <button class="signup-button" @click="goToSignup">회원가입</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import NotificationDropdown from './notification/NotificationDropdown.vue';

const router = useRouter();
const userStore = useUserStore();

const isProfileDropdownOpen = ref(false);

const profileDropdownRef = ref(null);

const isLoggedIn = computed(() => userStore.accessToken !== null);
const userNickname = computed(() => userStore.user?.userNickname || '사용자님');
const isAdmin = computed(() => userStore.user?.userRole === 'ROLE_ADMIN');

// 라우팅 함수들
const goHome = () => router.push('/');
const goToLogin = () => router.push({ name: 'login' });
const goToSignup = () => router.push({ name: 'join' });
const goToUserPet = () => router.push({ name: 'userPetList' });
const goToBoardCategoryList = () => router.push('/boardCategoryList');
const goToMyInfo = () => router.push({ name: 'myInfo' });
const goToUpdateProfile = () => router.push('/user/update');
const goToAdminDashboard = () => router.push({ name: 'adminDashboard' });

// 토글 함수
const toggleProfileDropdown = () => {
  isProfileDropdownOpen.value = !isProfileDropdownOpen.value;
};

// 외부 클릭 시 드롭다운 닫기
const handleClickOutside = (e) => {
  if (
    isProfileDropdownOpen.value &&
    profileDropdownRef.value &&
    !profileDropdownRef.value.contains(e.target)
  ) {
    isProfileDropdownOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

const logout = () => {
  userStore.logout();
  router.push('/');
};
</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Paytone+One&display=swap');
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css');

.navbar {
  width: 100%;
  /* height: 149px; */
  height: 120px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-sizing: border-box;
  border-bottom: 1px solid #ccc;
}

.logo {
  font-family: 'Paytone One', sans-serif;
  font-size: 30px;
  color: #008BE6;
  text-align: center;
  cursor: pointer;
}

.menu {
  display: flex;
  gap: 36px;
}

.menu-item {
  width: 195px;
  height: 76px;
  font-family: 'Pretendard', sans-serif;
  /* font-size: 22px; */
  font-size: 18px;
  color: #111;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  cursor: pointer;
  transition: color 0.2s ease;
}

.menu-item:hover {
  color: #008BE6;
}

.auth-section {
  position: relative;
}

.auth-buttons {
  display: flex;
  gap: 20px;
}

.login-button,
.signup-button {
  width: 97px;
  height: 52px;
  font-family: 'Pretendard', sans-serif;
  font-size: 15px;
  border-radius: 25px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-button {
  background: transparent;
  color: #111;
  border: 2px solid #008BE6;
}

.login-button:hover {
  background: #008BE6;
  color: white;
}

.signup-button {
  background: #008BE6;
  color: white;
}

.signup-button:hover {
  background: #0074c7;
}

/* 로그인 후 사용자 섹션 */
.user-section {
  display: flex;
  align-items: center;
  gap: 25px; /* 알림과 프로필 사이 간격 */
}

/* 알림 영역 */
.notification-area {
  display: flex;
  gap: 20px;
  align-items: center;
}

.icon {
  font-size: 20px;
  display: block;
}

/* 사용자 프로필 */
.user-profile {
  position: relative;
  cursor: pointer;
  padding: 12px 20px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.user-profile:hover {
  background: #f8f9fa;
}

.user-name {
  font-family: 'Pretendard', sans-serif;
  font-size: 16px;
  color: #333;
  font-weight: 500;
  user-select: none;
}

/* 드롭다운 메뉴 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  min-width: 160px;
  z-index: 1000;
  margin-top: 8px;
  padding: 8px 0;
  opacity: 0;
  transform: translateY(-10px);
  visibility: hidden;
  transition: all 0.3s ease;
}

.dropdown-menu.show {
  opacity: 1;
  transform: translateY(0);
  visibility: visible;
}

.dropdown-item {
  padding: 12px 20px;
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  gap: 10px;
}

.dropdown-item:hover {
  background: #f8f9fa;
}

.dropdown-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.dropdown-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 8px 0;
}

.dropdown-item.logout {
  color: #dc3545;
}

.dropdown-item.logout:hover {
  background: #fff5f5;
}
</style>