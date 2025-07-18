<template>
  <div class="container-fluid mt-4">

    <div class="row g-3">
      <div class="col-md-3">
          <CategoryBoardChart />
      </div>

      <div class="col-md-6">
          <KpiCards />
      </div>
    </div>

    <div class="row g-3 mt-3">
      <div class="col-md-4">
          <UserDailyChart />
      </div>

      <div class="col-md-4">
          <BoardDailyChart />
      </div>

      <div class="col-md-4">
          <ReportDailyChart />
      </div>
    </div>
    
    <div class="row g-3 mt-3">
      <div class="col-12">
          <ReportSummary />
      </div>
    </div>
    <div class="row g-3 mt-3">
      <div class="col-12">
          <SellerPendingSummary />
      </div>
    </div>

  </div>
</template>

<script setup>
import CategoryBoardChart from './components/CategoryBoardChart.vue';
import KpiCards from './components/KpiCards.vue';
import BoardDailyChart from './components/BoardDailyChart.vue';
import ReportDailyChart from './components/ReportDailyChart.vue';
import ReportSummary from './components/ReportSummary.vue';
import SellerPendingSummary from './components/SellerPendingSummary.vue';
import UserDailyChart from './components/UserDailyChart.vue';
</script>

<style scoped>
.card {
  border-radius: 12px;
  min-height: 250px;
}
</style>
