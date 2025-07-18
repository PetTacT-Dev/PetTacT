<template>
  <div class="container py-4 text-black">
    <h2 class="mb-4">주문서</h2>

    <div v-if="!orderStore.orderDraft.length" class="alert alert-warning">
      주문할 상품이 없습니다. 다시 장바구니로 이동해주세요.
    </div>

    <div v-else>
      <!-- 상품 목록 테이블 -->
      <table class="table table-bordered text-black">
        <thead>
          <tr>
            <th>상품명</th>
            <th>수량</th>
            <th>가격</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in orderStore.orderDraft" :key="item.cartNo">
            <td>{{ item.productName }}</td>
            <td>{{ item.productStock }}</td>
            <td>{{ getFormattedPrice(item.productPrice * item.productStock) }}원</td>
          </tr>
        </tbody>
      </table>

      <!-- 배송 정보 -->
      <h5 class="mt-4">배송 정보 <span class="text-danger">*</span> 필수입력</h5>

      <!-- 기존 배송지 / 신규입력 선택 -->
      <div class="form-check form-check-inline mt-2">
        <input class="form-check-input" type="radio" id="existing" value="existing" v-model="addressType" />
        <label class="form-check-label" for="existing">기존 배송지</label>
      </div>
      <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" id="new" value="new" v-model="addressType" />
        <label class="form-check-label" for="new">신규입력</label>
      </div>

      <!-- 배송지명 -->
      <div class="mb-3 mt-3">
        <label class="form-label">배송지명</label>
        <input v-model="deliveryName" class="form-control" placeholder="예: 우리집, 회사 등" />
      </div>

      <!-- 수령인 -->
      <div class="mb-3">
        <label class="form-label">수령인 <span class="text-danger">*</span></label>
        <input v-model="receiver" class="form-control" required />
      </div>

      <!-- 배송지 주소 -->
      <div class="mb-3">
        <label class="form-label">배송지 <span class="text-danger">*</span></label>
        <div class="input-group mb-2">
          <input v-model="zipcode" class="form-control" placeholder="우편번호" readonly />
          <button class="btn btn-secondary" type="button" @click="searchZipcode">우편번호 검색</button>
        </div>
        <input v-model="address1" class="form-control mb-2" placeholder="기본 주소" />
        <input v-model="address2" class="form-control" placeholder="상세 주소" />
      </div>

      <!-- 연락처1 -->
      <div class="mb-3">
        <label class="form-label">연락처1 <span class="text-danger">*</span></label>
        <input v-model="phone" class="form-control" placeholder="예: 010-1234-5678" />
      </div>

      <!-- 결제 버튼 -->
      <button v-if="isLoggedIn" @click="submitOrder" class="btn btn-secondary">결제하기</button>

      <!-- 이동 버튼 -->
      <div class="d-flex gap-2 mt-3">
        <button v-if="isLoggedIn" @click="goToCart" class="btn btn-secondary-light">장바구니</button>
        <button v-if="isLoggedIn" @click="goToProductList" class="btn btn-secondary-light">상품 목록</button>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { useOrderStore } from '@/stores/order';
import { useRouter } from 'vue-router';
import { ref,computed } from 'vue';
import { useUserStore } from '@/stores/user'

const orderStore = useOrderStore();
const router = useRouter();
const userStore = useUserStore()

//버튼 권한 검증
const isLoggedIn = computed(() => !!userStore.user)

//===================샘플데이터================

const deliveryName = ref('김철수');
const receiver = ref('집');
const zipcode = ref('');
const address1 = ref('');
const address2 = ref('');
const phone = ref('010-1111-1111');
const addressType = ref('existing');

const totalPrice = computed(() =>
  orderStore.orderDraft.reduce((sum, item) => sum + item.productPrice * item.productStock, 0)
);

const searchZipcode = () => {
  alert('우편번호 검색 모듈과 연동하세요.');
  zipcode.value = '04000';
  address1.value = '서울시 마포구 예제로 123';
  address2.value = '1층 101호';
};

const getFormattedPrice = (price) => new Intl.NumberFormat().format(price);

const goToCart = () => router.push('cart');
const goToProductList = () => router.push('/product');

// ✅ UUID 생성 (간단한 방식)
function generateUUID() {
  return 'xxxx-xxxx-xxxx-xxxx'.replace(/[x]/g, () =>
    ((Math.random() * 16) | 0).toString(16)
  );
}

// ✅ TossPayments 로딩 대기
const waitForToss = () =>
  new Promise((resolve) => {
    const check = () => {
      if (window.TossPayments) {
        resolve();
      } else {
        setTimeout(check, 50);
      }
    };
    check();
  });

const submitOrder = async () => {
  if (!isLoggedIn.value) {
  alert('로그인 후 결제를 진행해 주세요.');
  return;
}
  // 1. 필수값 확인
  if (!receiver.value || !address1.value || !address2.value || !phone.value) {
    alert('배송 정보를 모두 입력해주세요.');
    return;
  }

  try {
    // 2. 주문 상품 데이터 구성
    const orderDetails = orderStore.orderDraft.map(item => ({
      productNo: item.productNo,
      productStock: item.productStock
    }));

    // 3. 주문 ID 생성
    const orderId = `ORDER-${Date.now()}-${generateUUID()}`; // ✅ 여기서 고유한 주문번호를 생성
    console.log('✅ 생성된 orderId:', orderId); // 이 부분 중요

    // 4. TossPayments 초기화 대기
    await waitForToss(); // ✅ TossPayments 로딩 대기

    const toss = window.TossPayments('test_ck_DpexMgkW36oAl1jdPad9VGbR5ozO'); // ✅ Toss 클라이언트 키
    const successUrl = `${window.location.origin}/order/payment-success?orderId=${orderId}&amount=${totalPrice.value}`;
    const failUrl = `${window.location.origin}/order/payment-fail`;

    // 5. Toss 결제 요청
    const result = await toss.requestPayment('카드', {
      amount: totalPrice.value,
      orderId: orderId,
      orderName: orderStore.orderDraft[0]?.productName || '장바구니 상품',
      customerEmail: userStore.user?.email || 'guest@example.com',
      successUrl: successUrl,
      failUrl: failUrl
    });
      console.log('✅ 생성된 successUrl:', successUrl); // 이 부분도 중요
      console.log('✅ 생성된 failUrl:', failUrl); // 이 부분도 중요
      console.log('✅ totalPrice.value:', totalPrice.value)
      console.log('✅ orderStore.orderDraft:', orderStore.orderDraft)

    //6. 결제 승인 요청
    // await orderStore.confirmPayment({
    //   paymentKey: result.paymentKey,
    //   orderId,
    //   amount: totalPrice.value
    // });
// 7. 주문 생성
const orderNo = orderResponse.orderNo;
//localStorage.setItem('orderNo', orderNo);
const orderResponse = await orderStore.createOrder({
  orderId: orderId,
  orderNo: orderNo,
  deliveryName: deliveryName.value,
  receiver: receiver.value,
  zipcode: zipcode.value,
  address1: address1.value,
  address2: address2.value,
  phone: phone.value,
  orderDetails: orderDetails
});

    alert('결제 및 주문 완료!');
    orderStore.clearOrderDraft();
    router.push('/order');

  } catch (err) {
    alert('결제 또는 주문 중 오류 발생');
    console.error(err);
  }
};

</script>
