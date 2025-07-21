<template>
  <div class="report-container">
    <h2 class="page-title">내 신고 내역</h2>

    <div class="report-card">
      <table class="report-table">
        <thead>
          <tr>
            <th>대상정보</th>
            <th>신고사유</th>
            <th>작성일</th>
            <th>상태</th>
            <th>상세보기</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="reports.length === 0">
            <td colspan="5" class="empty-text">내역이 없습니다</td>
          </tr>
          <tr v-for="report in reports" :key="report.reportNo">
            <td>{{ locationLabel(report.reportTargetLocation) }}</td>
            <td>{{ report.reportReasonDescription }}</td>
            <td>{{ formatDate(report.createdAt) }}</td>
            <td>
              <span :class="['status-badge', statusBadgeClass(report.reportStatus)]">
                {{ statusText(report.reportStatus) }}
              </span>
            </td>
            <td>
              <button class="detail-button" @click="goToDetail(report.reportNo)">
                상세
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const reports = ref([]);
const router = useRouter();

const fetchReports = async () => {
  try {
    const res = await axios.get("/v1/user/mypage/my-reports");
    reports.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

const goToDetail = (reportNo) => {
  router.push({ name: "myReportDetail", params: { reportNo } });
};

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString() + " " + date.toLocaleTimeString();
};

const statusText = (status) => {
  switch (status) {
    case 0:
      return "처리중";
    case 1:
      return "승인";
    default:
      return "반려";
  }
};

const locationLabel = (location) => {
  switch (location) {
    case 'BOARD': return '게시글'
    case 'REPLY': return '댓글'
    case 'PRODUCT': return '상품'
    case 'CART': return '장바구니'
    case 'ORDER': return '주문'
    case 'PET': return '동물'
    case 'USER': return '회원'
    default: return location
  }
}

const statusBadgeClass = (status) => {
  return "badge " + (status === 1 ? "bg-success" : status === 2 ? "bg-danger" : "bg-secondary");
};

onMounted(fetchReports);
</script>
<style scoped>
.report-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.page-title {
  font-size: 1.6rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #eee;
  padding-bottom: 0.5rem;
}

.report-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 1rem;
  overflow-x: auto;
}

.report-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

.report-table th,
.report-table td {
  padding: 12px 8px;
  border: 1px solid #ddd;
  font-size: 0.95rem;
}

.report-table thead {
  background-color: #f9f9f9;
  font-weight: bold;
}

.empty-text {
  color: #999;
  padding: 2rem 0;
  text-align: center;
  font-style: italic;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  font-size: 0.85rem;
  border-radius: 999px;
  font-weight: 500;
}

.status-badge.pending {
  background-color: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
}

.status-badge.approved {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-badge.rejected {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.detail-button {
  background-color: #007bff;
  border: none;
  color: white;
  font-size: 0.85rem;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
}

.detail-button:hover {
  background-color: #0056b3;
}
</style>