<template>
  <div class="container py-4" style="background-color: #494949; min-height: 100vh;">
    <div v-if="order">
      <h2 class="text-white mb-3">주문 내역 (번호: {{ order.orderNo }})</h2>

      <p class="text-white mb-1"><strong>주문 상태: </strong>{{ getStatusText(order.status) }}</p>
      <p class="text-white mb-1"><strong>받는 사람: </strong> {{ order.receiver }}</p>
      <p class="text-white mb-1"><strong>배송지:</strong> {{ order.zipcode }} {{ order.address1 }} {{ order.address2 }}</p>
      <p class="text-white mb-1"><strong>연락처:</strong> {{ order.phone }}</p>

      <p class="text-white mb-1"><strong>주문 시간:</strong> {{ formatDate(order.createdAt) }}</p>
      <p v-if="order.deletedAt" class="text-white mb-1"><strong>삭제 시간:</strong> {{ formatDate(order.deletedAt) }}</p>
      <p class="text-white mb-3"><strong>총 금액:</strong> {{ order.totalPrice.toLocaleString() }}원</p>

      <h3 class="text-white mb-3">주문 상품 내역</h3>

      <ul class="list-unstyled">
        <li 
          v-for="item in order.products" 
          :key="item.orderDetailNo" 
          class="d-flex align-items-center mb-3 text-white"
        >
          <img
            v-if="item.imageUrl"
            :src="getImageUrl(item.imageUrl)" 
            alt="상품 이미지" 
            class="me-3 rounded" 
            style="width: 60px; height: 60px; object-fit: cover;" 
          />
          <div>
            <router-link 
              :to="`/product/${item.productNo}`" 
              class="text-white text-decoration-none fw-semibold"
            >
              {{ item.productName }}
            </router-link>
            <div>수량: {{ item.productStock }} | 가격: {{ item.productPrice.toLocaleString() }}원</div>
          </div>
        </li>
      </ul>

      <div class="mt-4">
        <button v-if="isLoggedIn" @click="goToList" class="btn btn-secondary me-2">상품 목록</button>
        <button v-if="isLoggedIn" @click="goToOrderList" class="btn btn-secondary me-2">주문 내역</button>
          <!-- ✅ 주문 상태가 "주문완료"일 경우에만 취소 가능 -->
        <button v-if="isLoggedIn" @click="cancelOrder" class="btn btn-secondary me-2">주문 취소</button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { computed, onMounted } from 'vue'
import { useRoute , useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()  // 여기 추가!
const orderStore = useOrderStore()
const userStore = useUserStore()


//버튼 권한 검증
const isLoggedIn = computed(() => !!userStore.user)

onMounted(async () => {
  await userStore.fetchUser(); // 🔹 사용자 정보 불러오기
  await orderStore.fetchOrderDetail(route.params.orderNo)
})

const order = computed(() => orderStore.orderDetail)
const user = computed(() => userStore.user) // 사용자 정보 접근


const goToOrderList = () => {
  router.push('/order')
}


const goToList = () => {
  router.push('/product') 
}

function getStatusText(status) {
  switch (status) {
    case 'PENDING':
      return '결제 대기'
    case 'COMPLETE':
      return '주문 완료'
    case 'CANCELLED':
      return '주문 취소'
    default:
      return status || '-'
  }
}

// 이미지 URL 기본 경로 붙이는 함수
function getImageUrl(imageUrl) {
  if (!imageUrl) return '/default-product.png'  // 기본 이미지 경로
  const baseUrl = 'http://localhost:8080'       // 서버 이미지 기본 URL (필요에 따라 변경)
  return baseUrl + imageUrl
}

// 날짜 포맷 함수 (간단하게 yyyy-mm-dd HH:mm:ss로 포맷)
function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mi = String(date.getMinutes()).padStart(2, '0')
  const ss = String(date.getSeconds()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`
}
// 취소버튼
const cancelOrder = async () => {
  if (!confirm(`주문번호 ${order.value.orderNo} 을(를) 정말 취소하시겠습니까?`)) return;

  try {
    await orderStore.cancelOrder(order.value.orderNo);
    alert(`주문번호 ${order.value.orderNo} 가 취소되었습니다.`);
    router.push('/order'); // 주문 리스트로 이동
  } catch (err) {
    alert('주문 취소 실패: ' + (err?.response?.data?.message || err.message || '알 수 없는 오류'));
  }
};
</script>

<style>
.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 8px;
  vertical-align: middle;
}
</style>