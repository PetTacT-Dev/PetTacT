<template>
  <div class="container py-4 product-list-wrapper">
    <h2 class="mb-4 text-center">주문 목록</h2>

    <div v-if="user" class="mb-4 p-3 bg-light rounded user-info-box">
      <ul class="mb-0 user-info-list">
        <li><strong>이메일:</strong> {{ user.userEmail }}</li>
        <li><strong>닉네임:</strong> {{ user.userNickname }}</li>
      </ul>
    </div>

    <div class="mb-3 d-flex gap-2 justify-content-center">
      <button v-if="isLoggedIn" @click="goToList" class="btn btn-primary btn-sm">상품 목록</button>
      <button v-if="isLoggedIn" @click="goToCart" class="btn btn-primary btn-sm">장바구니</button>
    </div>

    <div class="table-responsive shadow-sm rounded">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-primary">
          <tr>
            <th scope="col">주문일</th>
            <th scope="col">상품명</th>
            <th scope="col">주문 번호</th>
            <th scope="col">주문 금액</th>
            <th scope="col">주문 관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderNo">
            <td>{{ formatDate(order.createdAt) }}</td>
            <td>
              <ul class="list-unstyled mb-0">
                <li
                  v-for="product in order.orderDetailList"
                  :key="product.orderDetailNo"
                  class="d-flex align-items-center mb-2 product-name-item"
                >
                  <img
                    v-if="product.imageUrl"
                    :src="getImageUrl(product.imageUrl)"
                    alt="상품 이미지"
                    class="product-image rounded"
                  />
                  <router-link :to="`/order/${order.orderNo}`" class="text-decoration-none fw-semibold text-dark">
                    {{ product.productName }}
                  </router-link>
                </li>
              </ul>
            </td>
            <td>
              <router-link :to="`/order/${order.orderNo}`" class="text-decoration-none fw-semibold text-primary">
                {{ order.orderNo }}
              </router-link>
            </td>
            <td class="text-end fw-bold">{{ order.totalPrice.toLocaleString() }} 원</td>
            <td>
              <button
                v-if="isLoggedIn"
                @click="cancelOrder(order.orderNo)"
                :disabled="orderStore.loading"
                class="btn btn-outline-danger btn-sm"
              >
                주문 취소
              </button>
            </td>
          </tr>
          <tr v-if="orders.length === 0">
            <td colspan="5" class="text-center text-muted py-4">주문 내역이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 -->
    <nav v-if="totalPages > 1" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button class="page-link" @click="fetchPage(currentPage - 1)">이전</button>
        </li>
        <li
          class="page-item"
          v-for="page in totalPages"
          :key="page"
          :class="{ active: page - 1 === currentPage }"
        >
          <button class="page-link" @click="fetchPage(page - 1)">{{ page }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <button class="page-link" @click="fetchPage(currentPage + 1)">다음</button>
        </li>
      </ul>
    </nav>
  </div>
</template>


<script setup>
import { onMounted, computed } from 'vue'
import { useOrderStore } from '@/stores/order'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const orderStore = useOrderStore()
const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.user)


//버튼 권한 검증
const isLoggedIn = computed(() => !!userStore.user)

const currentPage = computed(() => orderStore.page)
const totalPages = computed(() => orderStore.totalPages)

const fetchPage = async (page) => {
  if (page < 0 || page >= orderStore.totalPages) return
  await orderStore.fetchOrders(page)
}

const goToCart = () => {
  router.push('/cart') // 장바구니 페이지 경로로 이동
}


const goToList = () => {
  router.push('/product') 
}

onMounted(async () => {
  try {
    await userStore.fetchUser();
    console.log('✅ 사용자 정보:', userStore.user); // ← 여기!
    await orderStore.fetchOrders();
  } catch (err) {
    console.error('회원 또는 주문 정보 로딩 실패:', err);
  }
});

const orders = computed(() => orderStore.orderList)

function getImageUrl(imageUrl) {
  if (!imageUrl) return '/default-product.png';
  const baseUrl = 'http://localhost:8080'; // 실제 서버 주소로 바꾸세요
  return baseUrl + imageUrl;
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

// ✅ 주문 취소 함수
const cancelOrder = async (orderNo) => {
  if (!confirm(`주문번호 ${orderNo} 을 정말 취소하시겠습니까?`)) return

  try {
    await orderStore.cancelOrder(orderNo)
    alert(`주문번호 ${orderNo} 가 취소되었습니다.`)
  } catch (err) {
    alert('주문 취소 실패: ' + (err?.response?.data?.message || err.message || '알 수 없는 오류'))
  }
}
</script>

<style scoped>
.product-list-wrapper {
  max-width: 900px;
  margin: 0 auto;
  color: #222;
  font-family: 'Pretendard', sans-serif;
}

.user-info-box {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
}

.user-info-list strong {
  color: #555;
}

.product-image {
  width: 48px;
  height: 48px;
  object-fit: cover;
  margin-right: 12px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.product-name-item {
  font-weight: 500;
}

.table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.1);
}

.table th,
.table td {
  vertical-align: middle !important;
}

.table-primary {
  background-color: #008be6;
  color: white;
}

.btn-primary {
  background-color: #008be6;
  border-color: #008be6;
  font-weight: 600;
  transition: background-color 0.2s ease;
}

.btn-primary:hover {
  background-color: #006bb3;
  border-color: #006bb3;
}

.btn-outline-danger {
  color: #dc3545;
  border-color: #dc3545;
  font-weight: 600;
}

.btn-outline-danger:hover:not(:disabled) {
  background-color: #dc3545;
  color: white;
  border-color: #dc3545;
}

.pagination .page-item.active .page-link {
  background-color: #008be6;
  border-color: #008be6;
  color: white;
}

.pagination .page-link {
  color: #008be6;
  font-weight: 600;
}

.pagination .page-item.disabled .page-link {
  color: #ccc;
  cursor: not-allowed;
  pointer-events: none;
}
</style>
