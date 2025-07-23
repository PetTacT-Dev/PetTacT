<template>
  <button @click="startPayment">결제</button>
</template>

<script setup>
import { useOrderStore } from '@/stores/order'

// UUID 생성 함수 (간단한 버전)
function generateUUID() {
  return 'xxxx-xxxx-xxxx-xxxx'.replace(/[x]/g, () =>
    ((Math.random() * 16) | 0).toString(16)
  )
}


const orderStore = useOrderStore()

const waitForToss = () =>
  new Promise(resolve => {
    const check = () => {
      if (window.TossPayments) {
        resolve()
      } else {
        setTimeout(check, 50)
      }
    }
    check()
  })

const startPayment = async () => {
  try {
    console.log("결제 시작")
    await waitForToss()

    // 주문번호를 동적으로 생성 (예: UUID + 타임스탬프 조합)
    const dynamicOrderId = `ORDER-${Date.now()}-${generateUUID()}`

    // ✅ TossPayments 인스턴스 생성 (클라이언트 키는 toss 개발자센터에서 발급됨)
    const tossPayments = window.TossPayments('test_ck_DpexMgkW36oAl1jdPad9VGbR5ozO') // 테스트용 클라이언트 키

    const tossResult = await tossPayments.requestPayment('카드', {
      amount: 5000,
      orderNo: dynamicOrderNo,
      orderName: '테스트 상품',
      customerEmail: 'test@example.com',
      successUrl: 'http://localhost:5173/order/payment-success',
      failUrl: 'http://localhost:5173/order/payment-fail',
    })

    const confirmResponse = await orderStore.confirmPayment({
      paymentKey: tossResult.paymentKey,
      orderNo: dynamicOrder,
      amount: 5000,
    })

    console.log('✅ 결제 승인 완료:', confirmResponse)
  } catch (err) {
    console.error('❌ 결제 실패:', err)
  }
}
</script>
