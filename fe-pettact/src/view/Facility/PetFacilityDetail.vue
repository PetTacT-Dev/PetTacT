<template>
  <div class="container mt-4">
    <h2>시설 상세 정보</h2>

    <div v-if="facility">
      <p><strong>시설명:</strong> {{ facility.facilityName }}</p>
      <p><strong>도로명 주소:</strong> {{ facility.roadAddress }}</p>
      <p><strong>지번 주소:</strong> {{ facility.lotAddress }}</p>
      <p><strong>시군구:</strong> {{ facility.sigunguName }}</p>
      <p><strong>시도:</strong> {{ facility.sidoName }}</p>
      <p><strong>운영시간:</strong> {{ facility.openHours }}</p>
      <p><strong>휴무일:</strong> {{ facility.closedDays }}</p>
      <p><strong>전화번호:</strong> {{ facility.phone }}</p>
      <p><strong>주차 가능 여부:</strong> {{ facility.parkingAvailable }}</p>
      <p><strong>위도:</strong> {{ facility.latitude }}, <strong>경도:</strong> {{ facility.longitude }}</p>
      <p><strong>카테고리:</strong> {{ facility.category1 }} / {{ facility.category2 }} / {{ facility.category3 }}</p>
      <p><strong>홈페이지:</strong> {{ facility.homepage }}</p>
      <p><strong>마지막 수정일:</strong> {{ facility.lastUpdatedDate }}</p>
      <p><strong>반려동물 동반:</strong> {{ facility.petAllowed }}</p>
      <p><strong>반려동물 전용 여부:</strong> {{ facility.petOnly }}</p>
      <p><strong>반려동물 제한사항:</strong> {{ facility.petLimit }}</p>
      <p><strong>입장 가능 동물 크기:</strong> {{ facility.allowedPetSize }}</p>
      <p><strong>입장료:</strong> {{ facility.entranceFeeInfo }}</p>
      <p><strong>실내 여부:</strong> {{ facility.isIndoor }}</p>
      <p><strong>실외 여부:</strong> {{ facility.isOutdoor }}</p>
      <p><strong>우편번호:</strong> {{ facility.postalCode }}</p>
      <p><strong>리 명칭:</strong> {{ facility.riName }}</p>
      <p><strong>번지:</strong> {{ facility.lotNumber }}</p>
      <p><strong>법정동:</strong> {{ facility.legalDongName }}</p>
    </div>

    <router-link to="/facility" class="btn btn-secondary mt-3">목록으로</router-link>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'


const facility = ref(null)
const route = useRoute()

onMounted(() => {
  const facilityNo = route.params.facilityNo

  axios.get(`/v1/api/facility/${facilityNo}`)
    .then(res => {
      facility.value = res.data
      console.log(res.data);
    })
    .catch(err => {
      console.error('시설 상세 조회 실패:', err)
    })
})
</script>
