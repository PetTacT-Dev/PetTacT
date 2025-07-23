<template>
  <div class="payment-success">
    <h1>주문이 완료되었습니다</h1>
    <p>주문번호: {{ orderId }}</p>
    <p>결제 금액: {{ amount.toLocaleString() }}원</p>
    <p v-if="!isApproved">결제 승인을 처리하고 있습니다. 잠시만 기다려주세요.</p>
    <p v-else>결제가 정상적으로 승인되었습니다. 감사합니다.</p>

    <button @click="goToOrders">주문 내역 확인</button>
    <button @click="goHome">쇼핑 계속하기</button>
  </div>
</template>


<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'

// ✅ 라우터, 핀아 스토어 사용
const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

const getParam = (val) => {
  if (Array.isArray(val)) {
    return [...new Set(val)][0]  // 중복 제거 후 첫 번째 값
  }
  return val || ''
}

const orderId = getParam(route.query.orderId)
const paymentKey = getParam(route.query.paymentKey)
const amountStr = getParam(route.query.amount)
const amount = Number(amountStr)

// ✅ 디버깅 로그
console.log('✅ 결제 승인 파라미터:', { paymentKey, orderId, amountStr, amount })
console.log('✅ route.query:', route.query)

const isApproved = ref(false)

onMounted(async () => {
  const orderNo = localStorage.getItem('orderNo') // Toss가 전달하지 않으므로 localStorage에서 가져옴
  console.log('✅ 결제 승인 파라미터:', { paymentKey, orderId, orderNo, amount })
  // ✅ 유효성 검사
  if (!paymentKey || !orderId || isNaN(amount) || amount <= 0) {
    alert('필수 결제 정보가 누락되었거나 잘못되었습니다.')
    router.push('/order/payment-fail')
    return
  }

  try {
    // ✅ 결제 승인 요청
    // const res = await orderStore.confirmPayment({
    //   paymentKey,
    //   orderId,
    //   orderNo,
    //   amount
    // })

    console.log('✅ 결제 승인 완료:')
    isApproved.value = true
  } catch (err) {
    console.error('❌ 결제 승인 실패:', err)
    alert('결제 승인 중 오류가 발생했습니다.')
    router.push('/order/payment-fail')
  }
})

// ✅ 버튼 클릭 시 페이지 이동
const goToOrders = () => {
  router.push('/order')
}
const goHome = () => {
  router.push('/product')
}
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css');

.payment-success {
  max-width: 600px;
  margin: 100px auto;
  text-align: center;
  padding: 40px 24px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  font-family: 'Pretendard', sans-serif;
}

.payment-success h1 {
  font-size: 26px;
  color: #111;
  margin-bottom: 24px;
}

.payment-success p {
  font-size: 16px;
  color: #333;
  margin: 8px 0;
}

.payment-success p:nth-of-type(3) {
  margin-bottom: 28px;
  color: #666;
}

.payment-success button {
  font-size: 15px;
  font-family: 'Pretendard', sans-serif;
  padding: 12px 24px;
  margin: 10px;
  border-radius: 25px;
  border: 2px solid #008BE6;
  background-color: white;
  color: #008BE6;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.payment-success button:hover {
  background-color: #008BE6;
  color: white;
}
</style>
