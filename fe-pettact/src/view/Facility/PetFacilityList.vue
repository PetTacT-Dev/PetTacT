<template>
  <div class="list-container">
    <div class="list-wrapper">
      <!-- 헤더 -->
      <div class="page-header">
        <h1>반려동물 동반입장 가능시설 목록</h1>
      </div>

      <!-- 필터 및 액션 섹션 -->
      <div class="filter-section">
        <div class="filter-row">
          <div class="filter-group">
            <label class="filter-label">시도</label>
            <select v-model="selectedSido" class="select-field" @change="fetchSigungu">
              <option value="">시도 선택</option>
              <option v-for="s in sidoList" :key="s.orgCd" :value="s.orgCd">{{ s.orgdownNm }}</option>
            </select>
          </div>
          <div class="filter-group">
            <label class="filter-label">시군구</label>
            <select v-model="selectedSigungu" class="select-field" :disabled="!sigunguList.length">
              <option value="">시군구 선택</option>
              <option v-for="g in sigunguList" :key="g.orgCd" :value="g.orgCd">{{ g.orgdownNm }}</option>
            </select>
          </div>
          <div class="filter-group">
            <label class="filter-label">시설명 검색</label>
            <input v-model="facilityName" class="search-input" placeholder="시설명을 입력하세요" @keyup.enter="goPage(1)"/>
          </div>
          <button class="search-btn" @click="goPage(1)">조회</button>
        </div>

        <div class="action-row">
          <p v-if="searched" class="total-count">
            총 <strong>{{ totalElements.toLocaleString() }}</strong>건
          </p>
        </div>
      </div>

      <!-- 리스트 컨텐츠 -->
      <div class="list-content">
        <div 
          v-for="facility in facilityList" 
          :key="facility.facilityNo"
          class="list-item"
        >
          <div class="item-content">
            <div class="item-main">
              <!-- <h3 class="item-title">{{ facility.facilityName }}</h3> -->
              <router-link 
  :to="`/facility/${facility.facilityNo}`"
  class="item-title" 
  style="text-decoration: none; color: inherit;"
>
  {{ facility.facilityName }}
</router-link>
              <p class="item-subtitle">{{ facility.roadAddress }}</p>
              <div class="item-info">
                <div class="info-item">
                  <span class="info-icon">📍</span>
                  <span>{{ facility.sigunguName }}</span>
                </div>
                <div class="info-item">
                  <span class="info-icon">📞</span>
                  <span>{{ facility.phone }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="action-buttons">
            <router-link 
              :to="`/facility/${facility.facilityNo}`"
              class="action-btn detail-btn" 
              title="보기"
            >
              상세보기
            </router-link>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <Pagination
        v-if="totalPages > 1"
        :current-page="page"
        :total-pages="totalPages"
        @change="goPage"
      />
    </div>
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
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.list-container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  min-height: 100vh;
  padding: 40px 20px;
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, sans-serif;
}

.list-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 헤더 */
.page-header {
  text-align: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-family: "Inter", sans-serif;
  font-weight: 700;
  font-size: 24px;
  color: black;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

/* 필터 및 액션 섹션 */
.filter-section {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-row {
  display: flex;
  align-items: end;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-left: 5px;
}

.select-field {
  width: 200px;
  height: 50px;
  padding: 6px 15px;
  background: white;
  border: none;
  border-radius: 5px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  font-family: "Pretendard", sans-serif;
  font-weight: 300;
  font-size: 15px;
  color: black;
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 12px center;
  background-repeat: no-repeat;
  background-size: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.select-field:focus {
  outline: 1px solid #008be6;
  outline-offset: -1px;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.select-field:disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.search-input {
  width: 300px;
  height: 50px;
  padding: 6px 15px;
  background: white;
  border: none;
  border-radius: 5px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  font-family: "Pretendard", sans-serif;
  font-weight: 300;
  font-size: 15px;
  color: black;
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: 1px solid #008be6;
  outline-offset: -1px;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.search-input::placeholder {
  color: #999;
}

.search-btn {
  height: 50px;
  padding: 0 24px;
  background: #008be6;
  color: white;
  border: none;
  border-radius: 5px;
  font-family: "Pretendard", sans-serif;
  font-weight: 500;
  font-size: 15px;
  cursor: pointer;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.search-btn:hover {
  background: #007acc;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.register-btn {
  height: 50px;
  padding: 0 24px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  font-family: "Pretendard", sans-serif;
  font-weight: 600;
  font-size: 15px;
  text-decoration: none;
  cursor: pointer;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.register-btn:hover {
  background: #218838;
  color: white;
  text-decoration: none;
  transform: translateY(-1px);
}

.total-count {
  color: #666;
  font-size: 15px;
  margin: 0;
}

.total-count strong {
  color: #008be6;
  font-weight: 600;
}

/* 리스트 컨테이너 */
.list-content {
  background: white;
  border-radius: 8px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 리스트 아이템 */
.list-item {
  padding: 20px 24px;
  background: white;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.list-item:last-child {
  border-bottom: none;
}

.list-item:hover {
  background: #f8f9fa;
  transform: translateX(4px);
  box-shadow: 4px 0 12px rgba(0, 139, 230, 0.1);
}

.item-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
}

.item-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
}

.item-subtitle {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.info-icon {
  font-size: 14px;
  color: #999;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  white-space: nowrap;
}

.detail-btn {
  background: #e3f2fd;
  color: #1976d2;
}

.detail-btn:hover {
  background: #bbdefb;
  transform: scale(1.1);
  color: #1976d2;
  text-decoration: none;
}

/* 반응형 */
@media (max-width: 768px) {
  .list-container {
    padding: 20px 15px;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .select-field,
  .search-input {
    width: 100%;
  }

  .action-row {
    flex-direction: column;
    align-items: stretch;
  }

  .list-item {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }

  .item-content {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }

  .item-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .action-buttons {
    justify-content: flex-start;
  }
}
</style>
