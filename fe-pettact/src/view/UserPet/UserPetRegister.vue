<template>
  <div class="container mt-4">
    <h2>보호소 등록</h2>
    <form @submit.prevent="submitForm" class="mt-3">
      <!-- 보호소명 -->
      <div class="mb-3">
        <label class="form-label">보호소명</label>
        <input v-model="form.careNm" type="text" class="form-control" required />
      </div>

      <!-- 보호소 코드 -->
      <div class="mb-3">
        <label class="form-label">보호소 코드</label>
        <input v-model="form.careRegNo" type="text" class="form-control" required />
      </div>

      <!-- 시도 / 시군구 선택 -->
      <div class="mb-3 d-flex gap-2">
        <select v-model="selectedSido" @change="handleSidoChange" class="form-select w-auto" required>
          <option value="">시도 선택</option>
          <option v-for="s in sidoList" :key="s.orgCd" :value="s">{{ s.orgdownNm }}</option>
        </select>
        <select v-model="selectedSigungu" @change="handleSigunguChange" class="form-select w-auto">
          <option value="">시군구 선택</option>
          <option v-for="g in sigunguList" :key="g.orgCd" :value="g">{{ g.orgdownNm }}</option>
        </select>
      </div>

      <!-- 주소 검색 -->
      <div class="mb-3">
        <label class="form-label">도로명 주소</label>
        <div class="input-group">
          <input v-model="form.careAddr" type="text" class="form-control" readonly required />
          <button type="button" class="btn btn-outline-secondary" @click="openDaumPostcode">주소 검색</button>
        </div>
      </div>

      <!-- 전화번호 -->
      <div class="mb-3">
        <label class="form-label">전화번호</label>
        <input v-model="form.careTel" type="text" class="form-control" required />
      </div>

      <!-- orgNm (자동입력) -->
      <input type="hidden" v-model="form.orgNm" />
      <!-- 위도/경도 (자동입력) -->
      <input type="hidden" v-model="form.lat" />
      <input type="hidden" v-model="form.lng" />

      <!-- 등록 버튼 -->
      <button type="submit" class="btn btn-primary">등록하기</button>
    </form>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  careNm: '',
  careRegNo: '',
  orgNm: '',
  careAddr: '',
  careTel: '',
  lat: '',
  lng: ''
})

const sidoList = ref([])
const sigunguList = ref([])
const selectedSido = ref('')
const selectedSigungu = ref('')

onMounted(() => {
  axios.get('/v1/pet/sido').then(res => {
    sidoList.value = res.data.items
  })
  const kakaoScript = document.createElement('script')
  kakaoScript.src = '//dapi.kakao.com/v2/maps/sdk.js?appkey=becfe069cca65d679dc79f6ef0a6cee7&libraries=services'
  kakaoScript.async = true
  document.head.appendChild(kakaoScript)

  const postcodeScript = document.createElement('script')
  postcodeScript.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
  postcodeScript.async = true
  document.head.appendChild(postcodeScript)

})

const handleSidoChange = () => {
  selectedSigungu.value = ''
  sigunguList.value = []
  form.value.orgNm = ''

  if (selectedSido.value) {
    axios.get('/v1/pet/sigungu', {
      params: { uprCd: selectedSido.value.orgCd }
    }).then(res => {
      sigunguList.value = res.data.items
    })
  }
}

const handleSigunguChange = () => {
  if (selectedSido.value && selectedSigungu.value) {
    form.value.orgNm = selectedSido.value.orgdownNm + ' ' + selectedSigungu.value.orgdownNm
  }
}

// 다음 주소 API + 카카오 좌표 변환
const openDaumPostcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      form.value.careAddr = data.roadAddress
      const geocoder = new window.kakao.maps.services.Geocoder()
      geocoder.addressSearch(data.roadAddress, (result, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
          form.value.lat = result[0].y
          form.value.lng = result[0].x
        }
      })
    }
  }).open()
}

const submitForm = () => {
  axios.post('/v1/api/shelter', form.value)
    .then(() => {
      alert('등록 완료!')
      router.push('/shelter')
    })
    .catch(err => {
      console.error('등록 실패', err)
      alert('등록 실패')
    })
}
</script>

<style scoped>
select.form-select {
  min-width: 160px;
}
</style>
