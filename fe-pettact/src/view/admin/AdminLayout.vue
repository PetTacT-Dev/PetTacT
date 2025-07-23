<template>
<div class="admin-layout">
    <!-- Sidebar -->
    <div class="sidebar">
      <h4 class="sidebar-title">관리자</h4>
      <ul class="sidebar-menu">
        <!-- 공통 -->
        <li class="menu-item">
          <router-link :to="{ name: 'adminDashboard' }" class="menu-link">
            <i class="icon">📊</i>
            대시보드
          </router-link>
        </li>

        <!-- 회원 관리 -->
        <li class="menu-item">
          <router-link :to="{ name: 'adminUserList' }" class="menu-link">
            <i class="icon">👥</i>
            회원 관리
          </router-link>
        </li>

        <!-- 게시물 관리 -->
        <li class="menu-item">
          <router-link :to="{ name: 'adminBoardList' }" class="menu-link">
            <i class="icon">📝</i>
            게시물 관리
          </router-link>
        </li>

        <!-- 구분선 -->
        <li class="menu-divider"></li>

        <!-- 판매자 관리 -->
        <li class="menu-category">판매자 관리</li>
        <li class="menu-item">
          <router-link :to="{ name: 'adminSellerList' }" class="menu-link">
            <i class="icon">🏪</i>
            판매자 목록
          </router-link>
        </li>
        <li class="menu-item">
          <router-link :to="{ name: 'adminSellerRequests' }" class="menu-link">
            <i class="icon">⏰</i>
            권한 요청 대기
          </router-link>
        </li>

        <!-- 구분선 -->
        <li class="menu-divider"></li>

        <!-- 신고 관리 -->
        <li class="menu-category">신고 관리</li>
        <li class="menu-item">
          <router-link :to="{ name: 'adminReportList' }" class="menu-link">
            <i class="icon">🚨</i>
            신고
          </router-link>
        </li>

        <!-- 구분선 -->
        <li class="menu-divider"></li>

        <!-- 신고 관리 -->
        <li class="menu-category">로그 관리</li>
        <li class="menu-item">
          <router-link :to="{ name: 'logs' }" class="menu-link">
            <i class="icon">🖥️</i>
            로그
          </router-link>
        </li>
      </ul>
    </div>

    <!-- Main Content -->
    <div class="content-wrapper">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: grid;
  grid-template-columns: 250px 1fr;
  height: 100vh;
  background: #f8f9fa;
}

/* Sidebar Styles */
.sidebar {
  background: #2c3e50;
  color: white;
  padding: 2rem 0;
  overflow-y: auto;
}

.sidebar-title {
  text-align: center;
  margin-bottom: 2rem;
  color: #3498db;
  font-size: 1.5rem;
  font-weight: bold;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  margin: 0.2rem 0;
}

.menu-link {
  display: flex;
  align-items: center;
  padding: 1rem 2rem;
  color: #ecf0f1;
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.menu-link:hover {
  background: rgba(52, 152, 219, 0.2);
  border-left-color: #3498db;
  color: white;
  text-decoration: none;
}

.menu-link.router-link-active {
  background: rgba(52, 152, 219, 0.3);
  border-left-color: #3498db;
  color: white;
}

.icon {
  margin-right: 0.5rem;
  font-size: 1.1rem;
}

.menu-category {
  padding: 1rem 2rem;
  color: #95a5a6;
  font-weight: bold;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 1rem 2rem;
}

/* Content Wrapper Styles */
.content-wrapper {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}
</style>