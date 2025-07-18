<template>
  <div class="container mt-4">
    <h4 class="mb-3 border-bottom pb-2">내 신고 내역</h4>

    <div class="table-responsive">
      <table class="table table-bordered align-middle text-center">
        <thead class="table-light">
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
            <td colspan="5" class="py-4 text-muted">내역이 없습니다</td>
          </tr>
          <tr v-for="report in reports" :key="report.reportNo">
            <td>{{ locationLabel(report.reportTargetLocation) }}</td>
            <td>{{ report.reportReasonDescription }}</td>
            <td>{{ formatDate(report.createdAt) }}</td>
            <td>
              <span :class="statusBadgeClass(report.reportStatus)">
                {{ statusText(report.reportStatus) }}
              </span>
            </td>
            <td>
              <button class="btn btn-sm btn-outline-primary" @click="goToDetail(report.reportNo)">
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
