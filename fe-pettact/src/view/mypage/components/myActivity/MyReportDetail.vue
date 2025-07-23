<template>
  <div class="container mt-4" style="max-width: 720px;">
    <h4 class="mb-4 border-bottom pb-2">신고 상세 내역</h4>

    <div v-if="report" class="card shadow-sm">
      <div class="card-body">
        <div class="row mb-3">
          <div class="col-sm-4 fw-bold">신고 사유</div>
          <div class="col-sm-8">{{ report.reportReasonDescription }}</div>
        </div>

        <div class="row mb-3">
          <div class="col-sm-4 fw-bold">신고 내용</div>
          <div class="col-sm-8">{{ report.reportDescription || '없음' }}</div>
        </div>

        <div class="row mb-3">
          <div class="col-sm-4 fw-bold">신고 대상</div>
          <div class="col-sm-8">
            {{ locationLabel(report.reportTargetLocation) }} #{{ report.reportTargetNo }}
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-sm-4 fw-bold">처리 상태</div>
          <div class="col-sm-8">
            <span :class="statusBadgeClass(report.reportStatus)">
              {{ statusText(report.reportStatus) }}
            </span>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-sm-4 fw-bold">신고일</div>
          <div class="col-sm-8">{{ formatDate(report.createdAt) }}</div>
        </div>

        <div class="row">
          <div class="col-sm-4 fw-bold">처리일</div>
          <div class="col-sm-8">{{ formatDate(report.updatedAt) }}</div>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-muted mt-5">
      <div class="spinner-border" role="status"><span class="visually-hidden">로딩 중...</span></div>
      <p class="mt-2">신고 정보를 불러오는 중입니다.</p>
    </div>

    <div class="mt-4">
      <button class="btn btn-secondary" @click="goBack">← 목록으로 돌아가기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const report = ref(null);

const fetchReportDetail = async () => {
  try {
    const res = await axios.get(`/v1/user/mypage/my-report/${route.params.reportNo}`);
    report.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  return date.toLocaleDateString() + " " + date.toLocaleTimeString();
};

const statusText = (status) => {
  switch (status) {
    case 0: return "처리중";
    case 1: return "승인";
    case 2: return "반려";
    default: return "알 수 없음";
  }
};

const statusBadgeClass = (status) => {
  switch (status) {
    case 0: return "badge bg-secondary";
    case 1: return "badge bg-success";
    case 2: return "badge bg-danger";
    default: return "badge bg-light text-dark";
  }
};

const locationLabel = (location) => {
  switch (location) {
    case "BOARD": return "게시글";
    case "REPLY": return "댓글";
    case "PRODUCT": return "상품";
    case "CART": return "장바구니";
    case "ORDER": return "주문";
    case "PET": return "동물";
    case "USER": return "회원";
    default: return location;
  }
};

const goBack = () => {
  router.back();
};

onMounted(fetchReportDetail);
</script>
