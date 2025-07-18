<template>
  <div>
    <h1>결제 실패 ❌</h1>
    <p>결제 처리에 실패했습니다.</p>
    <p>잠시 후 다시 시도해주세요.</p>
    <button @click="goBack">다시 시도</button>
    <button @click="goHome">홈으로</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const goBack = () => {
  router.push('/order/payment-success')  // 다시 승인 처리 페이지로 이동하거나 결제 페이지로 이동할 수 있음
}

const goHome = () => {
  router.push('/product')  // 홈 페이지 이동
}
</script>
