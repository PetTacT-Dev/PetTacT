<template>
  <div class="d-flex">
    <!-- Sidebar -->
    <div class="bg-dark text-white p-3" style="min-width: 200px;">
      <h4 class="text-center mb-4">관리자</h4>
      <ul class="nav flex-column">

        <!-- 공통 -->
        <li class="nav-item">
          <router-link :to="{ name: 'adminDashboard' }" class="nav-link text-white">대시보드</router-link>
        </li>

        <!-- 회원 관리 -->
        <li class="nav-item">
          <router-link :to="{ name: 'adminUserList' }" class="nav-link text-white">회원 관리</router-link>
        </li>

        <!-- 게시물 관리 -->
        <li class="nav-item">
          <router-link :to="{ name: 'adminBoardList' }" class="nav-link text-white">게시물 관리</router-link>
        </li>

        <!-- 구분선 -->
        <li><hr class="dropdown-divider bg-secondary"></li>

        <!-- 판매자 관리 -->
        <li class="nav-item">
          <span class="nav-link text-white fw-bold">판매자 관리</span>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'adminSellerRequests' }" class="nav-link text-white">권한 요청 대기</router-link>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'adminSellerList' }" class="nav-link text-white">판매자 목록</router-link>
        </li>
        <!-- <li class="nav-item">
          <router-link :to="{ name: 'adminSellerActivity' }" class="nav-link text-white">활동 내역</router-link>
        </li> -->

        <!-- 구분선 -->
        <li><hr class="dropdown-divider bg-secondary"></li>

        <!-- 신고 관리 -->
        <li class="nav-item">
          <span class="nav-link text-white fw-bold">신고 관리</span>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'adminReportList' }" class="nav-link text-white">신고</router-link>
        </li>
        <!-- <li class="nav-item">
          <router-link :to="{ name: 'adminReportBoard' }" class="nav-link text-white">게시물 신고</router-link>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'adminReportComment' }" class="nav-link text-white">댓글 신고</router-link>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'adminReportSeller' }" class="nav-link text-white">판매자 신고</router-link>
        </li> -->

      </ul>
    </div>

    <!-- Main Content -->
    <div class="flex-grow-1 p-4">
      <router-view />
    </div>
  </div>
</template>
