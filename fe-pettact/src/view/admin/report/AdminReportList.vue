<template>
  <div class="container mt-4">
    <h2 class="mb-4 border-bottom pb-2">신고 관리</h2>

    <div class="card mb-4">
      <div class="card-body">
        <form class="row g-3 align-items-center">
          <!-- status -->
          <div class="col-md-4">
            <label class="form-label">상태</label>
            <select v-model="filters.status" class="form-select">
              <option value="">상태 전체</option>
              <option value="0">대기</option>
              <option value="1">승인</option>
              <option value="2">반려</option>
            </select>
          </div>
          <!-- targetLocation -->
          <div class="col-md-4">
            <label class="form-label">대상종류</label>
              <select v-model="filters.location" class="form-select">
                <option value="">대상 전체</option>
                <option
                  v-for="loc in reportLocations"
                  :key="loc.code"
                  :value="loc.code"
                >
                  {{ loc.label }}
                </option>
              </select>
          </div>
          <!-- reason -->
          <div class="col-md-4">
            <label class="form-label">신고 사유</label>
            <select v-model="filters.reason" class="form-select">
              <option value="">사유 전체</option>
              <option
                v-for="reason in reportReasons"
                :key="reason.code"
                :value="reason.code"
              >
                {{ reason.description }}
              </option>
            </select>
          </div>
          <div class="col-md-4 d-grid mt-4">
            <button type="button" class="btn btn-primary" @click="onSearch">검색</button>
          </div>

        </form>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover align-middle">
        <thead class="table-dark">
          <tr>
            <th scope="col">신고번호</th>
            <th scope="col">대상번호</th>
            <th scope="col">대상종류</th>
            <th scope="col">신고자</th>
            <th scope="col">이메일</th>
            <th scope="col">사유</th>
            <th scope="col">상태</th>
            <th scope="col">신고일자</th>
            <th scope="col">관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(report, index) in pageData.content" :key="report.reportNo">
            <td>{{ report.reportNo }}</td>
            <td>{{ report.reportTargetNo }}</td>
            <td>{{ report.reportTargetLocation }}</td>
            <td>{{ report.userNickname }}</td>
            <td>{{ report.userEmail }}</td>
            <td>{{ report.reportReason }}</td>
            <td>{{ statusLabel(report.reportStatus) }}</td>
            <td>{{ formatDateTime(report.createdAt) }}</td>
            <td>
              <button type="button" class="btn btn-sm btn-outline-primary" @click="showDetail(report.reportNo)">상세</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination
      :totalElements="pageData.totalElements"
      :currentPage="pageData.currentPage"
      :pageSize="pageData.pageSize"
      @change="onPageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Pagination from '@/components/common/Pagination.vue'

const router = useRouter()

const pageData = ref({
  content: [],
  totalElements: 0,
  currentPage: 1,
  pageSize: 10
})

const filters = ref({
  status: '',
  location: '',
  reason: ''
})

const reportLocations = ref([])
const reportReasons = ref([])

const statusLabel = (status) => {
  switch(status){
    case 0: return '대기'
    case 1: return '승인'
    case 2: return '반려'
    default: return 'Unknown'
  }
}

const fetchEnums = async () => {
  try {
    const [locationsRes, reasonsRes] = await Promise.all([
      axios.get('/v1/report/locations'),
      axios.get('/v1/report/reasons')
    ])
    reportLocations.value = locationsRes.data
    reportReasons.value = reasonsRes.data
  } catch (err) {
    console.error('enum 로딩 실패:', err)
  }
}

const getReportList = async (page = 1) => {
  try {
    const response = await axios.get('/v1/admin/report', {
      params: {
        status: filters.value.status,
        location: filters.value.location,
        reason: filters.value.reason,
        page: page,
        size: pageData.value.pageSize
      }
    })
    pageData.value = response.data
  } catch (err) {
    console.error(err)
    alert('신고 목록 조회 실패')
  }
}

const onSearch = () => {
  getReportList(1)
}

const onPageChange = (page) => {
  getReportList(page)
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ').split('.')[0]
}

const showDetail = (reportNo) => {
  router.push({ name: 'adminReportDetail', params: { reportNo } })
}

onMounted(() => {
  fetchEnums()
  getReportList()
})
</script>
