<template>
  <div class="mypage-container" :class="{ 'center-layout': $route.name === 'myInfo' }">
    <!-- <h2>My page</h2> -->
      <!-- 왼쪽 사이드바 (첫 번째 템플릿 내용) -->
      <div class="sidebar">
        <div class="welcome-section">
          <h2 class="greeting">
            반가워요!
            <span class="username">{{ userNickname }}</span>
             님
          </h2>
        </div>

        <div class="menu-section">
          <div class="menu-category">
            <h3>사용자 닉네임</h3>
          </div>

          <div class="menu-list">
            <div class="menu-item">
              <div class="menu-icon">👤</div>
              <router-link :to="{ name: 'myInfo' }" class="menu-text"
                >나의 정보</router-link
              >
            </div>

            <router-link :to="{ name: 'userPetList' }" class="menu-item">
  <div class="menu-icon">🐕</div>
  <span class="menu-text">나의 반려동물</span>
</router-link>

            <div class="menu-item">
              <div class="menu-icon">❤️</div>
              <span class="menu-text">나의 관심공고 (준비중)</span>
            </div>

            <div class="menu-item">
              <div class="menu-icon">📔</div>
              <span class="menu-text">일기 (준비중)</span>
            </div>

            <div class="menu-item">
              <div class="menu-icon">🛒</div>
              <router-link :to="{ name: 'myMarket' }" class="menu-text"
                >나의 마켓</router-link
              >
            </div>

            <div class="menu-item">
              <div class="menu-icon">📊</div>
              <router-link :to="{ name: 'myActivity' }" class="menu-text"
                >나의 활동</router-link
              >
            </div>
          </div>
        </div>
      </div>

      <!-- 오른쪽 컨텐츠 영역 -->
      <div class="main-content">
        <router-view />
      </div>
    </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user';
import { computed } from 'vue';

const userStore = useUserStore();
const userNickname = computed(() => userStore.user?.userNickname || '사용자님');
</script>

<style scoped>
.mypage-container {
  display: flex;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 40px;
  min-width: 0;
}

/* 나의 정보일 때만 가운데 정렬 */
.mypage-container.center-layout {
  justify-content: center;
  align-items: flex-start;
}

.sidebar {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.welcome-section {
  background: white;
  padding: 25px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.greeting {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.username {
  color: #008be6;
  font-weight: 700;
}

.menu-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.menu-category {
  padding: 20px 20px 15px;
  border-bottom: 1px solid #f5f5f5;
}

.menu-category h3 {
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

.menu-list {
  padding: 0;
}

.menu-item {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f8f8f8;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background-color: #f8f9fa;
}

.menu-item.active {
  background-color: #008be6;
  color: white;
}

.menu-item.active .menu-text {
  color: white;
  font-weight: 600;
}

.menu-icon {
  font-size: 18px;
  margin-right: 12px;
}

.menu-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
</style>