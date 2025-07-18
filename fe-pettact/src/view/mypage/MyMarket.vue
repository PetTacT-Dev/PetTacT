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
