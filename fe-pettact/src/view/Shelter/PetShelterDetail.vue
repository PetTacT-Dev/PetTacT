<template>
  <div class="container mt-4">
    <h2>보호소 상세 정보</h2>
    <div v-if="shelter">
        <p><strong>보호소명:</strong> {{ shelter.careNm }}</p>
        <p><strong>보호소 코드:</strong> {{ shelter.careRegNo }}</p>
        <p><strong>시군구:</strong> {{ shelter.orgNm }}</p>
        <p><strong>주소:</strong> {{ shelter.careAddr }} ({{ shelter.jibunAddr }})</p>
        <p><strong>위치:</strong> 위도 {{ shelter.lat }}, 경도 {{ shelter.lng }}</p>
        <p><strong>전화번호:</strong> {{ shelter.careTel }}</p>
        <p><strong>평일 운영시간:</strong> {{ shelter.weekOprStime }} ~ {{ shelter.weekOprEtime }}</p>
        <p><strong>주말 운영시간:</strong> {{ shelter.weekendOprStime }} ~ {{ shelter.weekendOprEtime }}</p>
        <p><strong>휴무일:</strong> {{ shelter.closeDay }}</p>
        <p><strong>보호 대상:</strong> {{ shelter.saveTrgtAnimal }}</p>
        <p><strong>지정일:</strong> {{ shelter.dsignationDate }}</p>
        <p><strong>부서명:</strong> {{ shelter.divisionNm }}</p>
    </div>
    <div v-else>
      <p>보호소 정보를 불러오는 중...</p>
    </div>
    <router-link :to="`/shelter/update/${route.params.shelterNo}`" class="btn btn-primary mt-3">
  수정하기
</router-link>
    <router-link to="/shelter" class="btn btn-secondary mt-3">← 목록으로</router-link>

  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const shelter = ref(null)

onMounted(() => {
  const shelterNo = route.params.shelterNo
  axios.get(`/v1/api/shelter/${shelterNo}`)
    .then(res => {
      shelter.value = res.data
    })
    .catch(err => {
      console.error('보호소 상세 조회 실패:', err)
    })
})
</script>
