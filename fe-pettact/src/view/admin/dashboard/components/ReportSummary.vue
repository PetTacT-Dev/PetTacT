<template>
  <div class="mb-4">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <h5 class="mb-0">신고</h5>
      <div class="text-muted">
        미처리 <strong>{{ pendingCount }}</strong>건 |
        <router-link :to="{ name: 'adminReportList' }">전체보기 ></router-link>
      </div>
    </div>

    <table class="table table-striped table-hover table-sm">
      <thead>
        <tr>
          <th>#</th>
          <th>신고자</th>
          <th>대상</th>
          <!-- <th>사유</th> -->
          <th>상태</th>
          <th>신고일</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="report in latestReports" :key="report.reportNo">
          <td>{{ report.reportNo }}</td>
          <td>{{ report.userNickname }}</td>
          <td>{{ report.reportTargetLocation }}</td>
          <!-- <td>{{ report.reportReason }}</td> -->
          <td>
            <span :class="report.reportStatus === 0 ? 'text-danger' : 'text-success'">
              {{ report.reportStatus === 0 ? '미처리' : '처리완료' }}
            </span>
          </td>
          <td>{{ formatDate(report.createdAt) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const pendingCount = ref(0)
const latestReports = ref([])

onMounted(async () => {
  const { data: count } = await axios.get('/v1/admin/dashboard/report/pending-count')
  pendingCount.value = count

  const { data: reports } = await axios.get('/v1/admin/dashboard/report/latest?limit=5')
  latestReports.value = reports
})

function formatDate(date) {
  return new Date(date).toLocaleDateString()
}
</script>
