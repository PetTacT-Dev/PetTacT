<template>
  <div class="container mt-4">
    <div class="card shadow-sm rounded">
      <div class="card-body">
        <h4 class="card-title mb-4">나의 마켓</h4>

        <!-- 로딩 상태 -->
        <div v-if="isLoading" class="text-center my-3">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">로딩 중...</span>
          </div>
        </div>

        <div v-else>
          <!-- 오류 메시지 -->
          <div v-if="error" class="alert alert-danger">
            {{ error }}
          </div>

          <div v-else>
            <!-- 승인 대기 상태 -->
            <div v-if="status === 'STATUS_PENDING'" class="alert alert-info">
              판매자 권한 승인 대기중입니다.
            </div>

            <!-- 승인 완료 상태 -->
            <div v-else-if="status === 'STATUS_ACTIVE' && role === 'ROLE_SELLER'">
              <h5 class="mb-3">내 상품 목록</h5>

              <div v-if="pageData.content.length > 0">
                <MyProductCard
                  v-for="product in pageData.content"
                  :key="product.productNo"
                  :product="product"
                  @deleted="handleDelete"
                />
              </div>
              <div v-else class="text-muted">
                등록한 상품이 없습니다.
                <div class="mt-3">
                  <button @click="goToProductForm" class="btn btn-primary">상품 등록하기</button>
                </div>
              </div>

              <!-- ✅ 페이징 컴포넌트 -->
              <Pagination
                :totalElements="pageData.totalElements"
                :currentPage="pageData.currentPage"
                :pageSize="pageData.pageSize"
                @change="onPageChange"
              />
            </div>

            <!-- 권한 요청 버튼 -->
            <div v-else>
              <!-- 베네핏 섹션 추가 -->
  <div class="benefits-section">
    <h3 class="benefits-title">판매자 혜택</h3>
    <div class="benefits-grid">
      <div class="benefit-item">
        <div class="benefit-icon">💰</div>
        <div class="benefit-content">
          <h4>수수료 혜택</h4>
          <p>첫 달 수수료 무료</p>
        </div>
      </div>
      <div class="benefit-item">
        <div class="benefit-icon">📱</div>
        <div class="benefit-content">
          <h4>간편한 관리</h4>
          <p>모바일에서 쉽게 관리</p>
        </div>
      </div>
      <div class="benefit-item">
        <div class="benefit-icon">🚚</div>
        <div class="benefit-content">
          <h4>배송 지원</h4>
          <p>택배 연동 서비스</p>
        </div>
      </div>
      <div class="benefit-item">
        <div class="benefit-icon">📊</div>
        <div class="benefit-content">
          <h4>판매 분석</h4>
          <p>상세한 매출 리포트</p>
        </div>
      </div>
    </div>
  </div>

  <button @click="requestSellerRole" class="btn btn-success">판매자 권한 요청하기</button>
</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import MyProductCard from './components/myProduct/MyProductCard.vue'
import Pagination from '@/components/common/Pagination.vue'

const status = ref('')
const role = ref('')
const error = ref('')
const isLoading = ref(true)

const pageData = ref({
  content: [],
  totalElements: 0,
  currentPage: 1,
  pageSize: 10
})

const fetchSellerStatus = async () => {
  try {
    const res = await axios.get('/v1/user/seller/request/status')
    status.value = res.data.status

    const userRes = await axios.get('/v1/user/me')
    role.value = userRes.data.userRole

    if (status.value === 'STATUS_ACTIVE' && role.value === 'ROLE_SELLER') {
      await fetchMyProducts(1)
    }
  } catch (err) {
    console.error(err)
    error.value = '상태를 불러오는 데 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}

const fetchMyProducts = async (page = 1) => {
  try {
    const res = await axios.get('/v1/user/mypage/my-products', {
      params: {
        page: page - 1, // 0-based index
        size: pageData.value.pageSize,
        sort: 'createdAt'
      }
    })

    pageData.value = {
      ...res.data,
      currentPage: page // ⬅️ 클라이언트 기준으로 1-based 저장
    }

  } catch (err) {
    console.error(err)
    error.value = '상품 목록을 불러오는 데 실패했습니다.'
  }
}

const onPageChange = (page) => {
  fetchMyProducts(page)
}

const handleDelete = (deletedId) => {
  pageData.value.content = pageData.value.content.filter(
    (product) => product.productNo !== deletedId
  )
  pageData.value.totalElements--
}

const requestSellerRole = async () => {
  try {
    await axios.post('/v1/user/seller/request')
    alert('판매자 권한 요청이 접수되었습니다.')
    fetchSellerStatus()
  } catch (err) {
    alert(err.response?.data || '요청에 실패했습니다.')
  }
}

onMounted(() => {
  fetchSellerStatus()
})
</script>


<style scoped>
/* 컨테이너 스타일링 */
.container.mt-4 {
  max-width: 800px;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
}

.card.shadow-sm.rounded {
  border: none;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-body {
  padding: 40px;
}

.card-title.mb-4 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

/* 판매자 권한 요청 버튼 */
.btn.btn-success {
  background: linear-gradient(135deg, #4A90E2 0%, #008BE6 100%);
  border: none;
  padding: 16px 32px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
  display: block;
  margin: 40px auto;
  min-width: 200px;
}

.btn.btn-success:hover {
  background: linear-gradient(135deg, #3A80D2 0%, #0066CC 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(74, 144, 226, 0.3);
}

/* 상품 등록 버튼 */
.btn.btn-primary {
  background: #008BE6;
  border-color: #008BE6;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.btn.btn-primary:hover {
  background: #0066CC;
  border-color: #0066CC;
}

/* 알림 메시지들 */
.alert.alert-info {
  background: linear-gradient(135deg, #f0f8ff 0%, #e8f4fd 100%);
  border: 1px solid #008BE6;
  border-radius: 12px;
  color: #0066CC;
  text-align: center;
  padding: 20px;
}

.alert.alert-danger {
  border-radius: 12px;
  background: #fff5f5;
  border-color: #fed7d7;
  color: #e53e3e;
}

/* 로딩 스피너 */
.spinner-border.text-primary {
  color: #008BE6 !important;
}

/* 텍스트 스타일링 */
.text-muted {
  color: #666 !important;
  text-align: center;
  font-size: 16px;
  line-height: 1.6;
}

h5.mb-3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.benefits-section {
  margin-bottom: 30px;
}

.benefits-title {
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.benefit-item {
  text-align: center;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  transition: transform 0.2s ease;
}

.benefit-item:hover {
  transform: translateY(-4px);
}

.benefit-icon {
  font-size: 28px;
  margin-bottom: 12px;
}

.benefit-content h4 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.benefit-content p {
  font-size: 12px;
  color: #666;
  margin: 0;
}
</style>