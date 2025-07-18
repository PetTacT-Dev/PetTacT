<template>
  <div class="container py-4">
    <h2 class="mb-4">주문 목록</h2>
    <div v-if="user" class="mb-4 p-3 bg-light rounded">
      <ul class="mb-0">
        <li><strong>이메일:</strong> {{ user.userEmail }}</li>
        <li><strong>닉네임:</strong> {{ user.userNickname }}</li>
        <!-- <li><strong>권한:</strong> {{ user.value.userRole }}</li> -->
      </ul>
    </div>
    <div class="mb-3">
      <button v-if="isLoggedIn" @click="goToList" class="btn btn-secondary me-2">상품 목록</button>
      <button v-if="isLoggedIn" @click="goToCart" class="btn btn-secondary">장바구니</button>
    </div>


    <div class="table-responsive">
      <table class="table table-striped table-bordered align-middle">
        <thead class="table-light">
          <tr>
            <th>주문일</th>
            <th>상품명</th>
            <th>주문 번호</th>
            <th>주문 금액</th>
            <th>주문 관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderNo">
            <td>{{ formatDate(order.createdAt) }}</td>
            <td>
              <ul class="list-unstyled mb-0">
                <li v-for="product in order.orderDetailList" :key="product.orderDetailNo" class="d-flex align-items-center mb-2">
                  <img
                    v-if="product.imageUrl"
                    :src="getImageUrl(product.imageUrl)"
                    alt="상품 이미지"
                    class="me-2"
                    style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px;"
                  />
                  <router-link :to="`/order/${order.orderNo}`" class="text-decoration-none">
                    {{ product.productName }}
                  </router-link>
                </li>
              </ul>
            </td>
            <td>
              <router-link :to="`/order/${order.orderNo}`" class="text-decoration-none">
                {{ order.orderNo }}
              </router-link>
            </td>
            <td>{{ order.totalPrice.toLocaleString() }} 원</td>
            <td>
              <button v-if="isLoggedIn"
                @click="cancelOrder(order.orderNo)" 
                :disabled="orderStore.loading" 
                class="btn btn-secondary btn-sm"
              >
                주문 취소
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
        <!-- ✅ 페이지네이션 -->
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
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: left;
}
ul {
  padding-left: 16px;
  margin: 0;
  list-style-type: disc;
}
button[disabled] {
  background-color: #ccc;
  cursor: not-allowed;
}
.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 8px;
  vertical-align: middle;
}
.pagination {
  list-style: none;
  padding-left: 0; /* 부트스트랩에서는 이거도 초기화되어 있어야 정렬이 맞음 */
}
</style>
