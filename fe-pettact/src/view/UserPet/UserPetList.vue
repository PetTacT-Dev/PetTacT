<template>
  <div class="pet-list-container">
    <!-- 헤더 섹션 -->
    <div class="header-section">
      <h1 class="main-title">내 반려동물</h1>
      <p class="subtitle">소중한 반려동물들을 관리해주세요</p>
    </div>

    <!-- 액션 버튼 그룹 -->
    <div class="action-buttons">
      <router-link to="/userPet/register" class="btn-primary">
        <span class="btn-icon">+</span>
        반려동물 등록
      </router-link>
      <router-link to="/userPet/diary/register" class="btn-secondary">
        <span class="btn-icon">📝</span>
        일기 작성
      </router-link>
    </div>

    <!-- 반려동물 카드 그리드 -->
    <div class="pet-grid" v-if="petList.length > 0">
      <div class="pet-card" v-for="pet in petList" :key="pet.petId">
        <div class="pet-avatar">
          <div class="avatar-placeholder">🐕</div>
        </div>
        
        <div class="pet-info">
          <h3 class="pet-name">{{ pet.petName }}</h3>
          
          <div class="pet-details">
            <div class="detail-item">
              <span class="detail-label">품종</span>
              <span class="detail-value">{{ pet.kindNm }}</span>
            </div>
            
            <div class="detail-item">
              <span class="detail-label">생일</span>
              <span class="detail-value">{{ pet.petBirth }}</span>
            </div>
            
            <div class="detail-item">
              <span class="detail-label">RFID</span>
              <span class="detail-value">{{ pet.rfidNo }}</span>
            </div>
            
            <div class="detail-item">
              <span class="detail-label">중성화</span>
              <span class="detail-value">{{ pet.isNeutered ? '예' : '아니오' }}</span>
            </div>
          </div>
        </div>

        <div class="card-actions">
          <router-link :to="`/userPet/detail/${pet.petId}`" class="detail-btn">
            상세보기
          </router-link>
        </div>
      </div>
    </div>

    <!-- 빈 상태 -->
    <div class="empty-state" v-else>
      <div class="empty-icon">🐾</div>
      <h3>등록된 반려동물이 없습니다</h3>
      <p>첫 번째 반려동물을 등록해보세요!</p>
      <router-link to="/userPet/register" class="btn-primary">
        반려동물 등록하기
      </router-link>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination-wrapper" v-if="totalPages > 1">
      <Pagination
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

<style scoped>
.pet-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  min-height: 100vh;
  background: #f8f9fa;
}

/* 헤더 섹션 */
.header-section {
  text-align: center;
  margin-bottom: 2rem;
}

.main-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 1rem;
  color: #6c757d;
  margin: 0;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.btn-primary, .btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
  transform: translateY(-1px);
  color: white;
  text-decoration: none;
}

.btn-secondary {
  background: white;
  color: #6c757d;
  border: 2px solid #dee2e6;
}

.btn-secondary:hover {
  background: #f8f9fa;
  border-color: #007bff;
  color: #007bff;
  text-decoration: none;
  transform: translateY(-1px);
}

.btn-icon {
  font-size: 1.1rem;
}

/* 펫 카드 그리드 */
.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.pet-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  border: 1px solid #e9ecef;
}

.pet-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.pet-avatar {
  text-align: center;
  margin-bottom: 1rem;
}

.avatar-placeholder {
  width: 60px;
  height: 60px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 auto;
  border: 3px solid #e9ecef;
}

.pet-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: #2c3e50;
  text-align: center;
  margin: 0 0 1rem 0;
}

.pet-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.9rem;
}

.detail-value {
  font-weight: 600;
  color: #2c3e50;
  font-size: 0.9rem;
}

/* 카드 액션 */
.card-actions {
  text-align: center;
}

.detail-btn {
  display: inline-block;
  padding: 0.5rem 1.5rem;
  background: #f8f9fa;
  color: #007bff;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  border: 2px solid #007bff;
  transition: all 0.2s ease;
}

.detail-btn:hover {
  background: #007bff;
  color: white;
  text-decoration: none;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.empty-state p {
  color: #6c757d;
  margin-bottom: 2rem;
}

/* 페이지네이션 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .pet-list-container {
    padding: 1rem;
  }
  
  .main-title {
    font-size: 1.5rem;
  }
  
  .pet-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .btn-primary, .btn-secondary {
    width: 100%;
    max-width: 300px;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .pet-card {
    padding: 1rem;
  }
  
  .empty-state {
    padding: 2rem 1rem;
  }
  
  .empty-icon {
    font-size: 3rem;
  }
}
</style>