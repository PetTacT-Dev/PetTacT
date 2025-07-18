<template>
  <div>
    <h1>결제 성공 🎉</h1>
    <p>주문번호: {{ orderId }}</p>
    <p>결제 금액: {{ amount }}원</p>
    <p v-if="!isApproved">결제 승인 처리 중입니다...</p>
    <p v-else>결제가 정상적으로 완료되었습니다.</p>

    <button @click="goToOrders">주문 내역 보기</button>
    <button @click="goHome">홈으로</button>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

const rawOrderId = route.query.orderId
const orderId = Array.isArray(rawOrderId) ? rawOrderId[0] : rawOrderId
const amount = Number(route.query.amount) || 0
const paymentKey = route.query.paymentKey || ''
const orderNo = localStorage.getItem('orderNo') // ✅ 수정 포인트

console.log('✅ 결제 승인 파라미터:', { paymentKey, orderId, orderNo , amount })
console.log('✅ route.query:', route.query)
console.log('✅ orderId:', orderId)

const isApproved = ref(false)

onMounted(async () => {
  
  if (!paymentKey || !orderId || !amount) {
    alert('필수 결제 정보가 누락되었습니다.')
    router.push('/order/payment-fail')
    return
  }

  try {
    const orderNo = localStorage.getItem('orderNo'); // 프론트에서 저장한 주문번호
    const res = await orderStore.confirmPayment({
      paymentKey,
      orderId,
      orderNo,
      amount
    })
    console.log('✅ 결제 승인 완료:', res)
    isApproved.value = true
  } catch (err) {
    console.error('❌ 결제 승인 실패:', err)
    alert('결제 승인 중 오류가 발생했습니다.')
    router.push('/order/payment-fail')
  }
})

const goToOrders = () => {
  router.push('/order')
}
const goHome = () => {
  router.push('/product')
}
</script>
