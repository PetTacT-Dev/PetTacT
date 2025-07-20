<template>
  <div>
    <h2>결제 처리 중입니다...</h2>
  </div>
</template>

<script setup>

import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

onMounted(async () => {
  const paymentKey = route.query.paymentKey
  const orderId = route.query.orderId;
  const amount = Number(route.query.amount)

    console.log('✅ 결제 승인 파라미터:', { paymentKey, orderId, amount })

    try {
      const result = await orderStore.confirmPayment({ paymentKey, orderId, amount })
      console.log('✅ 결제 승인 완료:', result)
      router.push({path: '/order/payment-complete', query: {paymentKey, orderId, amount}})
    } catch (err) {
      console.error('❌ 결제 승인 실패:', err)
      router.push('/order/payment-fail')
    }
})
</script>
