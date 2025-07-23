<template>
  <div>
    <h2>주문</h2>
    <button v-if="isLoggedIn" @click="create">주문하기</button>
  </div>
</template>

<script setup>
import { ref, onMounted ,computed } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'


const router = useRouter()
const orderStore = useOrderStore()
const userStore = useUserStore()

const isLoggedIn = computed(() => !!userStore.user)

onMounted(async () => {
  await userStore.fetchUser()
  console.log('👤 사용자 정보:', userStore.user)
})

const create = async () => {
  if (!isLoggedIn.value) {
    alert('로그인 후 주문 가능합니다.')
    return
  }

  const orderRequest = {
    userNo: user.userNo,  // ✅ 사용자 번호 추가
    deliveryName: '우리집',
    receiver: user.userNickname || '홍길동', // 사용자 이름으로 대체 가능
    zipcode: '04000',
    address1: '서울시 마포구',
    address2: '101호',
    phone: '010-1234-5678',
    orderDetails: [
      {
        productNo: productNo,
        productStock: productStock
      }
    ]
  }

  try {
    const res = await orderStore.createOrder(orderRequest)
    alert('주문 완료: ' + res.orderNo)
    router.push(`/order/${res.orderNo}`)
  } catch (err) {
    alert('주문 실패: ' + (err?.response?.data?.message || err.message || '알 수 없는 오류'))
  }
}
</script>


<style>
</style>
