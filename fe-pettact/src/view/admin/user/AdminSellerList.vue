<template>
  <div class="container mt-4">
    <h2 class="mb-4 border-bottom pb-2">판매자 관리</h2>

    <div class="card mb-4">
      <div class="card-body">
        <form class="row g-3">
          <div class="col-md-6">
            <input v-model="filters.keyword" type="text" class="form-control" placeholder="검색어 (이름/닉네임/이메일)">
          </div>
          <div class="col-md-4">
            <select v-model="filters.status" class="form-select">
              <option value="">상태 전체</option>
              <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
          <div class="col-md-2 d-grid">
            <button type="button" class="btn btn-primary" @click="onSearch">검색</button>
          </div>
        </form>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover align-middle">
        <thead class="table-dark">
          <tr>
            <th scope="col">번호</th>
            <th scope="col">이름</th>
            <th scope="col">닉네임</th>
            <th scope="col">이메일</th>
            <th scope="col">연락처</th>
            <th scope="col">가입일</th>
            <th scope="col">상태</th>
            <th scope="col">상세</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user, index) in pageData.content" :key="user.userNo">
            <td>{{ index + 1 }}</td>
            <td>{{ user.userName }}</td>
            <td>{{ user.userNickname }}</td>
            <td>{{ user.userEmail }}</td>
            <td>{{ user.userTel }}</td>
            <td>{{ formatDateTime(user.createdAt) }}</td>
            <td>{{ user.statusCode }}</td>
            <td>
              <button type="button" class="btn btn-sm btn-outline-primary" @click="showDetail(user.userNo)">상세보기</button>
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

const filters = ref({
  keyword: '',
  status: ''
})

const statusOptions = ref([])

const pageData = ref({
  content: [],
  totalElements: 0,
  currentPage: 1,
  pageSize: 10
})

const getSellerList = async (page = 1) => {
  try {
    const res = await axios.get('/v1/admin/seller', {
      params: {
        keyword: filters.value.keyword,
        status: filters.value.status,
        page: page,
        size: pageData.value.pageSize
      }
    })
    pageData.value = res.data
  } catch (err) {
    console.error(err)
  }
}

const getFilterOptions = async () => {
  try {
    const res = await axios.get('/v1/admin/user/list/filters')
    statusOptions.value = res.data.statusOptions
  } catch (err) {
    console.error(err)
  }
}

const onSearch = () => {
  getSellerList(1)
}

const onPageChange = (page) => {
  getSellerList(page)
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ').split('.')[0]
}

const showDetail = (userNo) => {
  router.push({ name: 'adminUserDetail', params: { userNo } })
}

onMounted(() => {
  getFilterOptions()
  getSellerList()
})
</script>
