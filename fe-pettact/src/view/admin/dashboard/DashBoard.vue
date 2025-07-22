<template>
  <div class="dashboard-wrapper">

    <!-- KPI 카드 -->
    <div class="kpi-section">
      <KpiCards />
    </div>

    <!-- 도넛차트 + 통계 탭 -->
    <div class="chart-grid">
      <div class="donut-card">
        <CategoryBoardChart />
      </div>

      <div class="tab-card">
        <ul class="tab-menu">
          <li :class="{ active: activeTab === 'user' }" @click="activeTab = 'user'">회원 통계</li>
          <li :class="{ active: activeTab === 'board' }" @click="activeTab = 'board'">게시글 통계</li>
          <li :class="{ active: activeTab === 'report' }" @click="activeTab = 'report'">신고 통계</li>
        </ul>

        <div class="tab-content">
          <div v-if="activeTab === 'user'" class="chart-card">
            <UserDailyChart />
          </div>
          <div v-if="activeTab === 'board'" class="chart-card">
            <BoardDailyChart />
          </div>
          <div v-if="activeTab === 'report'" class="chart-card">
            <ReportDailyChart />
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 요약 -->
    <div class="summary-section">
      <div class="summary-card">
        <ReportSummary />
      </div>
      <div class="summary-card">
        <SellerPendingSummary />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import CategoryBoardChart from './components/CategoryBoardChart.vue'
import KpiCards from './components/KpiCards.vue'
import BoardDailyChart from './components/BoardDailyChart.vue'
import ReportDailyChart from './components/ReportDailyChart.vue'
import ReportSummary from './components/ReportSummary.vue'
import SellerPendingSummary from './components/SellerPendingSummary.vue'
import UserDailyChart from './components/UserDailyChart.vue'

const activeTab = ref('user')
</script>

<style scoped>
.dashboard-wrapper {
  padding: 2rem;
  background: #f8f9fa;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* KPI 카드 영역 */
.kpi-section {
  display: flex;
  justify-content: space-between;
}

/* 도넛 + 탭 통계 */
.chart-grid {
  display: flex;
  gap: 2rem;
}

/* 도넛 (1) */
.donut-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  min-width: 250px;
}

/* 탭 차트 (3) */
.tab-card {
  flex: 3;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
    height: 450px; /* 원하는 고정 높이로 설정 */
  overflow-y: auto; /* 내용이 넘칠 경우 스크롤 추가 */
}

.tab-menu {
  display: flex;
  list-style: none;
  padding: 0.5rem 1rem;
  margin: 0;
  border-bottom: 1px solid #ddd;
}

.tab-menu li {
  margin-right: 1rem;
  cursor: pointer;
  font-weight: 500;
  color: #555;
}

.tab-menu li.active {
  border-bottom: 3px solid #007bff;
  color: #007bff;
}

.tab-content {
  padding: 1rem;
}

.chart-card {
  min-height: 300px;
}

/* 하단 요약 */
.summary-section {
  display: flex;
  gap: 1rem;
}

.summary-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
</style>
