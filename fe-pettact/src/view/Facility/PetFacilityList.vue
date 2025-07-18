<template>
  <div class="container mt-4">
    <h2>시설 목록</h2>

    <!-- 검색 필터 -->
    <div class="mb-3 d-flex flex-wrap gap-2 align-items-end">
      <select v-model="selectedSido" class="form-select w-auto" @change="fetchSigungu">
        <option value="">시도 선택</option>
        <option v-for="s in sidoList" :key="s.orgCd" :value="s.orgCd">{{ s.orgdownNm }}</option>
      </select>

      <select v-model="selectedSigungu" class="form-select w-auto" :disabled="!sigunguList.length">
        <option value="">시군구 선택</option>
        <option v-for="g in sigunguList" :key="g.orgCd" :value="g.orgCd">{{ g.orgdownNm }}</option>
      </select>

      <input v-model="facilityName" class="form-control w-auto" placeholder="시설명 검색" @keyup.enter="goPage(1)"/>

      <button class="btn btn-outline-primary"  @click="goPage(1)">조회</button>
    </div>

    <!-- 등록 및 건수 -->
    <router-link to="facility/register" class="btn btn-primary mb-3">+ 시설 등록</router-link>
    <p v-if="searched" class="mb-2">총 {{ totalElements.toLocaleString() }}건</p>

    <!-- 목록 -->
    <table class="table table-hover">
      <thead>
        <tr>
          <th>시설명</th>
          <th>도로명 주소</th>
          <th>시군구</th>
          <th>전화번호</th>
          <th>상세</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="facility in facilityList" :key="facility.facilityNo">
          <td>{{ facility.facilityName }}</td>
          <td>{{ facility.roadAddress }}</td>
          <td>{{ facility.sigunguName }}</td>
          <td>{{ facility.phone }}</td>
          <td>
            <router-link :to="`/facility/${facility.facilityNo}`" class="btn btn-sm btn-outline-secondary">보기</router-link>
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

const facilityList = ref([])
const sidoList = ref([])
const sigunguList = ref([])

const selectedSido = ref('')
const selectedSigungu = ref('')
const facilityName = ref('')

const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const searched = ref(false)

const fetchSido = () => {
  axios.get('/v1/pet/sido').then(res => {
    sidoList.value = res.data.items
  })
}

const fetchSigungu = () => {
  sigunguList.value = []
  selectedSigungu.value = ''
  if (!selectedSido.value) return

  axios.get('/v1/pet/sigungu', { params: { uprCd: selectedSido.value } }).then(res => {
    sigunguList.value = res.data.items
  })
}

const goPage = (targetPage) => {
  const params = {
    page: targetPage,
    size: 10
  }

  if (selectedSido.value) params.sidoCode = selectedSido.value
  if (selectedSigungu.value) params.sigunguCode = selectedSigungu.value
  if (facilityName.value) params.facilityName = facilityName.value

  axios.get('/v1/pet/facility', { params }).then(res => {
    facilityList.value = res.data.content
    totalPages.value = res.data.totalPages
    totalElements.value = res.data.totalElements
    page.value = targetPage
    searched.value = true
    console.log(res.data);
  }).catch(err => {
    console.error('시설 목록 조회 실패:', err)
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
