<template>
  <div class="payment-fail">
    <h1>결제에 실패했습니다</h1>
    <p>결제 과정에서 문제가 발생했습니다.</p>
    <p>잠시 후 다시 시도하시거나, 다른 결제 수단을 이용해 주세요.</p>
    
    <button @click="goBack">다시 시도하기</button>
    <button @click="goHome">쇼핑 계속하기</button>
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

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css');

.payment-fail {
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

.payment-fail h1 {
  font-size: 26px;
  color: #dc3545;
  margin-bottom: 24px;
}

.payment-fail p {
  font-size: 16px;
  color: #333;
  margin: 8px 0;
}

.payment-fail p:nth-of-type(3) {
  margin-bottom: 28px;
  color: #666;
}

.payment-fail button {
  font-size: 15px;
  font-family: 'Pretendard', sans-serif;
  padding: 12px 24px;
  margin: 10px;
  border-radius: 25px;
  border: 2px solid #dc3545;
  background-color: white;
  color: #dc3545;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.payment-fail button:hover {
  background-color: #dc3545;
  color: white;
}
</style>
