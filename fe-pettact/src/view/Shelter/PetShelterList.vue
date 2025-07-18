<template>
  <div class="container mt-4">
    <h2>쉘터 목록</h2>

    <!-- 시도 선택 필터 -->
    <div class="mb-3 d-flex gap-2">
      <select v-model="selectedSido" class="form-select w-auto">
        <option value="">전체</option>
        <option v-for="s in sidoList" :key="s.orgCd" :value="s">{{ s.orgdownNm }}</option>
      </select>
      <button class="btn btn-outline-primary" @click="goPage(1)">조회</button>
    </div>

    <!-- 등록 및 건수 -->
    <router-link to="/shelter/register" class="btn btn-primary mb-3">+ 쉘터 등록</router-link>
    <p v-if="searched" class="mb-2">총 {{ totalElements.toLocaleString() }}건</p>

    <!-- 테이블 목록 -->
    <table class="table table-hover">
      <thead>
        <tr>
          <th>쉘터명</th>
          <th>보호소 코드</th>
          <th>시군구</th>
          <th>전화번호</th>
          <th>상세</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="shelter in shelterList" :key="shelter.shelterNo">
          <td>{{ shelter.careNm }}</td>
          <td>{{ shelter.careRegNo }}</td>
          <td>{{ shelter.orgNm }}</td>
          <td>{{ shelter.careTel }}</td>
          <td>
            <router-link :to="`/shelter/${shelter.shelterNo}`" class="btn btn-sm btn-outline-secondary">보기</router-link>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 페이지네이션 -->
    <Pagination
      v-if="totalPages > 1"
      :current-page="page"
      :total-pages="totalPages"
      @change="goPage"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Pagination from '@/components/common/Paginations.vue'

const shelterList = ref([])
const sidoList = ref([])
const selectedSido = ref('')

const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const searched = ref(false)

const fetchSido = () => {
  axios.get('/v1/pet/sido').then(res => {
    sidoList.value = res.data.items
  })
}

const goPage = (targetPage) => {
  const params = {
    page: targetPage,
    size: 10
  }

  if (selectedSido.value?.orgdownNm) {
    params.sido = selectedSido.value.orgdownNm
  }

  axios.get('/v1/pet/shelter', { params }).then(res => {
    shelterList.value = res.data.content
    totalPages.value = res.data.totalPages
    totalElements.value = res.data.totalElements
    page.value = targetPage
    searched.value = true
  }).catch(err => {
    console.error('쉘터 목록 조회 실패:', err)
  })
}

onMounted(() => {
  fetchSido()
  goPage(1)
})
</script>

<style scoped>
select.form-select {
  min-width: 160px;
}
</style>
