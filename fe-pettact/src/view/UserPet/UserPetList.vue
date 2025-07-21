<template>
  <div class="container mt-4">
    <h2>내 반려동물 목록</h2>

    <!-- 등록 버튼 -->
    <router-link to="/userPet/register" class="btn btn-primary mb-3">+ 반려동물 등록</router-link>
    <router-link to="/userPet/diary/register" class="btn btn-primary mb-3">+ 일기 작성</router-link>
    <!-- 목록 -->
    <table class="table table-hover">
      <thead>
        <tr>
          <th>이름</th>
          <th>RFID</th>
          <th>품종</th>
          <th>중성화 여부</th>
          <th>생일</th>
          <th>상세</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="pet in petList" :key="pet.petId">
          <td>{{ pet.petName }}</td>
          <td>{{ pet.rfidNo }}</td>
          <td>{{ pet.kindNm }}</td>
          <td>{{ pet.isNeutered ? '예' : '아니오' }}</td>
          <td>{{ pet.petBirth }}</td>
          <td>
            <router-link :to="`/userPet/detail/${pet.petId}`" class="btn btn-sm btn-outline-secondary">보기</router-link>
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

const petList = ref([])
const page = ref(1)
const totalPages = ref(1)

const goPage = (targetPage) => {
  axios.get('/v1/pet/list', { params: { page: targetPage, size: 10 } })
    .then(res => {
      petList.value = res.data.content
      totalPages.value = res.data.totalPages
      page.value = targetPage
      console.log(res.data)
    })
    .catch(err => {
      console.error('반려동물 목록 조회 실패:', err)
    })
}

onMounted(() => {
  goPage(1)
})
</script>
