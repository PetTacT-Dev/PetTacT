<template>
  <div class="navbar">
    <div class="logo" @click="goHome">PETTACT</div>
    <div class="menu">
      <div class="menu-item">유기동물 조회</div>
      <div class="menu-item" @click="goToPetChat">반려동물</div>
      <div class="menu-item">쇼핑몰</div>
      <div class="menu-item" @click="goToBoardCategoryList">커뮤니티</div>
    </div>

   <div class="auth-section">
  <!-- 로그인된 경우 -->
  <div v-if="isLoggedIn" class="user-section">
    <div class="user-profile" 
         @mouseenter="showDropdown = true" 
         @mouseleave="showDropdown = false">
      <span class="user-Nickname">{{ userNickname }}</span>
      
      <div class="dropdown-menu" :class="{ 'show': showDropdown }">
        <div class="dropdown-item" @click="goToProfile">마이페이지</div>
        <div class="dropdown-item" @click="goToUpdateProfile">개인정보 수정</div>
        <div class="dropdown-divider"></div>
        <div class="dropdown-item logout" @click="handleLogout">로그아웃</div>
      </div>
    </div> 
  </div>

    <!-- 로그인되지 않은 경우 -->
    <div v-else class="auth-buttons">
      <button class="login-button" @click="goToLogin">로그인</button>
      <button class="signup-button">회원가입</button>
    </div>
  </div>
</div> 
</template>


<script setup>
// import { ref, computed, onMounted } from 'vue'
// import { useRouter } from 'vue-router'
// import { useUserStore } from '@/stores/user'

// const router = useRouter()
// const userStore = useUserStore()
// const showDropdown = ref(false)


// // Computed
// const isLoggedIn = computed(() => !!userStore.user)

// const isAdmin = computed(() => userStore.user?.userRole === 'ROLE_ADMIN')

// const userNickname = computed(() => {
//   const user = userStore.user
//   if (!user) return '사용자님'
  
//   // userNickname이 있으면 그대로 사용 (xxxyo23님)
//   if (user.userNickname) {
//     return user.userNickname + '님'
//   } else if (user.userEmail) {
//     return user.userEmail.split('@')[0] + '님'
//   }
//   return '사용자님'
// })

// // Methods
// const goToPetChat = () => {
//   router.push('/petChat')
// }

// const goToLogin = () => {
//   router.push('/user/login')
// }

// const goToSignup = () => {
//   router.push('/signup')
// }

// const goHome = () => {
//   router.push('/')
// }

// const goToBoardCategoryList = () => {
//   router.push('/boardCategoryList')
// }

// const toggleDropdown = () => {
//   showDropdown.value = !showDropdown.value
// }

// // const goToProfile = () => {
// //   router.push('/profile')
// //   showDropdown.value = false
// // }

// // const goToMyPosts = () => {
// //   router.push('/my-posts')
// //   showDropdown.value = false
// // }

// // const goToSettings = () => {
// //   router.push('/settings')
// //   showDropdown.value = false
// // }

// const handleLogout = () => {
//   userStore.logout()
//   showDropdown.value = false
//   router.push('/')
// }

// // Lifecycle
// onMounted(() => {
//   console.log('Navbar mounted, 초기 user:', userStore.user)
//   // 앱 시작시 토큰에서 사용자 정보 복원
//   userStore.restoreUserFromToken()
//   console.log('restoreUserFromToken 후 user:', userStore.user)
  
//   // 외부 클릭시 드롭다운 닫기
//   document.addEventListener('click', (e) => {
//     // 컴포넌트 참조는 template ref를 사용해야 함
//     // 일단 전역으로 처리
//     if (!document.querySelector('.user-profile')?.contains(e.target)) {
//       showDropdown.value = false
//     }
//   })
// })
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

.notification-item {
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.notification-item:hover {
  background: #f8f9fa;
}

.icon {
  font-size: 20px;
  display: block;
}

.badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 11px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
  font-family: 'Pretendard', sans-serif;
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