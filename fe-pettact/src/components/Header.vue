<template>
  <div class="navbar">
  <div class="logo" @click="goHome">
    <img src="/image/logo.png" alt="logo" class="logo-img" />
  </div>
    <div class="menu">
      <div class="menu-item" @click="goToPetAbandonmentList">구조동물 </div>
      <div class="menu-item" @click="goToPetShelterList">보호소</div>
      <div class="menu-item" @click="goToPetFacilityList">Pet Zone</div>
      <div class="menu-item" @click="goToUserPet">내 반려동물</div>
      <div class="menu-item" @click="goToProduct">쇼핑몰</div>
      <div class="menu-item" @click="goToBoardCategoryList">커뮤니티</div>
    </div>

    <div class="auth-section">
      <div v-if="isLoggedIn" class="user-section">
        <!-- 🔔 알림 드롭다운 -->
        <div class="notification-area">
            <NotificationDropdown />
        </div>

         <div class="chat-area">
    <button @click="openChatModal" class="chat-button">
      <span class="chat-icon">💬</span>
      <span class="chat-text">채팅</span>
      <!-- 읽지 않은 메시지 배지 (옵션) -->
      <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
    </button>
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
import { useModalStore } from '@/js/modalStore'


const router = useRouter();
const userStore = useUserStore();
const modalStore = useModalStore();

const isProfileDropdownOpen = ref(false);
const profileDropdownRef = ref(null);
const unreadChatCount = ref(0);

const isLoggedIn = computed(() => userStore.accessToken !== null);
const userNickname = computed(() => userStore.user?.userNickname || '사용자님');
const isAdmin = computed(() => userStore.user?.userRole === 'ROLE_ADMIN');

// 라우팅 함수들
const goHome = () => router.push('/');
const goToLogin = () => router.push({ name: 'login' });
const goToSignup = () => router.push({ name: 'join' });
const goToUserPet = () => router.push({ name: 'userPetList' });
// const goToBoardCategoryList = () => router.push('/boardCategoryList');
const goToMyInfo = () => router.push({ name: 'myInfo' });
const goToUpdateProfile = () => router.push({ name: 'myInfo' });
const goToAdminDashboard = () => router.push({ name: 'adminDashboard' });
const goToPetAbandonmentList = () => router.push({ name: 'abandonmentList' });
const goToPetShelterList = () => router.push({ name: 'shelterList' });
const goToPetFacilityList = () => router.push({ name: 'facilityList' });
const goToProduct = () => router.push({ name: 'ProductList' });

// 채팅 모달 열기
const openChatModal = () => {
  modalStore.openMessageModal();
};

// 토글 함수
const toggleProfileDropdown = () => {
  isProfileDropdownOpen.value = !isProfileDropdownOpen.value;
};

const goToBoardCategoryList = () => {
  // 관리자든 사용자든 모두 게시판 목록 페이지로 이동
  router.push('/boardCategoryList')
}


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

// 읽지 않은 채팅 메시지 수 확인 (옵션)
const fetchUnreadChatCount = async () => {
  if (!isLoggedIn.value) return;
  
  try {
    // API 호출해서 읽지 않은 메시지 수 가져오기
    // const response = await axios.get('/v1/chat/unread-count');
    // unreadChatCount.value = response.data;
  } catch (err) {
    console.error('읽지 않은 채팅 수 조회 실패:', err);
  }
};

// 생명주기 - 한 번만 선언
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  
  // 로그인 상태면 읽지 않은 채팅 수 확인
  if (isLoggedIn.value) {
    fetchUnreadChatCount();
  }
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
.logo-img {
  width: 200px;
  object-fit: contain;
  cursor: pointer;
}
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


.chat-area {
  position: relative;
}

/* 채팅 버튼 */
.chat-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: transparent;
  border: 1px solid #e1e8ed;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  min-height: 36px;
}

.chat-button:hover {
  background: #f8fafe;
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.chat-button:active {
  transform: translateY(0);
}

/* 채팅 아이콘 */
.chat-icon {
  font-size: 16px;
  line-height: 1;
}

/* 채팅 텍스트 */
.chat-text {
  font-weight: 600;
  white-space: nowrap;
}

/* 읽지 않은 메시지 배지 */
.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ef4444;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: 700;
  min-width: 18px;
  text-align: center;
  line-height: 1.2;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
}

/* 채팅 버튼이 활성화된 상태 (모달이 열린 상태) */
.chat-button.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.chat-button.active:hover {
  background: #5a67d8;
  border-color: #5a67d8;
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