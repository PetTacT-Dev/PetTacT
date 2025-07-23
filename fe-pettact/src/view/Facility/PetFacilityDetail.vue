<template>
  <div class="shelter-detail-container">
    <div class="shelter-detail-wrapper">
      <div class="shelter-detail-header">
        <h1>시설 상세 정보</h1>
      </div>
      
      <div v-if="facility" class="shelter-detail-content">
        <div class="detail-section">
          <div class="field-group">
            <label class="field-label">시설명</label>
            <div class="detail-field">{{ facility.facilityName }}</div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">도로명 주소</label>
              <div class="detail-field">{{ facility.roadAddress }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">지번 주소</label>
              <div class="detail-field">{{ facility.lotAddress }}</div>
            </div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">시군구</label>
              <div class="detail-field">{{ facility.sigunguName }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">시도</label>
              <div class="detail-field">{{ facility.sidoName }}</div>
            </div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">운영시간</label>
              <div class="detail-field">{{ facility.openHours }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">휴무일</label>
              <div class="detail-field">{{ facility.closedDays }}</div>
            </div>
          </div>
          
          <div class="field-group">
            <label class="field-label">전화번호</label>
            <div class="detail-field">{{ facility.phone }}</div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">주차 가능 여부</label>
              <div class="detail-field">{{ facility.parkingAvailable }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">위치</label>
              <div class="detail-field">위도 {{ facility.latitude }}, 경도 {{ facility.longitude }}</div>
            </div>
          </div>
          
          <div class="field-group">
            <label class="field-label">카테고리</label>
            <div class="detail-field">{{ facility.category1 }} / {{ facility.category2 }} / {{ facility.category3 }}</div>
          </div>
          
          <div class="field-group">
            <label class="field-label">홈페이지</label>
            <div class="detail-field">{{ facility.homepage }}</div>
          </div>
          
          <div class="field-group">
            <label class="field-label">마지막 수정일</label>
            <div class="detail-field">{{ facility.lastUpdatedDate }}</div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">반려동물 동반</label>
              <div class="detail-field">{{ facility.petAllowed }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">반려동물 전용 여부</label>
              <div class="detail-field">{{ facility.petOnly }}</div>
            </div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">반려동물 제한사항</label>
              <div class="detail-field">{{ facility.petLimit }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">입장 가능 동물 크기</label>
              <div class="detail-field">{{ facility.allowedPetSize }}</div>
            </div>
          </div>
          
          <div class="field-group">
            <label class="field-label">입장료</label>
            <div class="detail-field">{{ facility.entranceFeeInfo }}</div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">실내 여부</label>
              <div class="detail-field">{{ facility.isIndoor }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">실외 여부</label>
              <div class="detail-field">{{ facility.isOutdoor }}</div>
            </div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">우편번호</label>
              <div class="detail-field">{{ facility.postalCode }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">리 명칭</label>
              <div class="detail-field">{{ facility.riName }}</div>
            </div>
          </div>
          
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">번지</label>
              <div class="detail-field">{{ facility.lotNumber }}</div>
            </div>
            <div class="field-group">
              <label class="field-label">법정동</label>
              <div class="detail-field">{{ facility.legalDongName }}</div>
            </div>
          </div>
        </div>
        
        <div class="action-buttons">
          <router-link to="/facility" class="btn btn-secondary">
            목록으로
          </router-link>
        </div>
      </div>
      
      <div v-else class="loading-state">
        <p>시설 정보를 불러오는 중...</p>
      </div>
    </div>
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

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.shelter-detail-container {
  width: 90%;
  max-width: 800px;
  margin: 0 auto;
  background: white;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, sans-serif;
}

.shelter-detail-wrapper {
  width: 100%;
  max-width: 740px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.shelter-detail-header {
  text-align: center;
}

.shelter-detail-header h1 {
  font-family: "Inter", sans-serif;
  font-weight: 700;
  font-size: 24px;
  line-height: 33.6px;
  color: black;
  margin-bottom: 12px;
}

.shelter-detail-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.detail-section {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.field-label {
  font-family: "Pretendard", sans-serif;
  font-weight: 500;
  font-size: 14px;
  color: #333;
  margin-left: 5px;
}

.detail-field {
  width: 100%;
  min-height: 50px;
  padding: 12px 15px;
  background: #f8f9fa;
  border: none;
  border-radius: 5px;
  font-family: "Pretendard", sans-serif;
  font-weight: 400;
  font-size: 15px;
  line-height: 21px;
  color: #333;
  display: flex;
  align-items: center;
  word-break: break-all;
}

.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  height: 50px;
  padding: 0 24px;
  border: none;
  border-radius: 5px;
  font-family: "Pretendard", sans-serif;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 120px;
}

.btn-primary {
  background: #008be6;
  color: white;
}

.btn-primary:hover {
  background: #007acc;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
  transform: translateY(-1px);
  color: white;
  text-decoration: none;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
  box-shadow: 4px 4px 4px rgba(108, 117, 125, 0.25);
  transform: translateY(-1px);
  color: white;
  text-decoration: none;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
}

.loading-state p {
  font-size: 16px;
  color: #666;
}

/* 반응형 */
@media (max-width: 768px) {
  .shelter-detail-container {
    padding: 20px 15px;
  }

  .field-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>
