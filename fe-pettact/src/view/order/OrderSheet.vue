<template>
  <div class="container py-4 text-black">
    <div class="order-wrapper">
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

const deliveryName = ref('우리집');
const receiver = ref('');
const zipcode = ref('');
const address1 = ref('');
const address2 = ref('');
const phone = ref('');
const addressType = ref('existing');

const totalPrice = computed(() =>
  orderStore.orderDraft.reduce((sum, item) => sum + item.productPrice * item.productStock, 0)
);

const searchZipcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      zipcode.value = data.zonecode;

      // 기본 주소 구성
      let fullAddress = data.address;
      let extraAddress = '';

      if (data.addressType === 'R') {
        if (data.bname !== '') extraAddress += data.bname;
        if (data.buildingName !== '') {
          extraAddress += (extraAddress ? ', ' : '') + data.buildingName;
        }
        if (extraAddress) fullAddress += ` (${extraAddress})`;
      }

      address1.value = fullAddress;
      address2.value = ''; // 상세 주소는 사용자 입력
    }
  }).open();
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

   const orderResponse = await orderStore.createOrder({
  //orderId: orderId,
  //orderNo: orderNo,
  deliveryName: deliveryName.value,
  receiver: receiver.value,
  zipcode: zipcode.value,
  address1: address1.value,
  address2: address2.value,
  phone: phone.value,
  orderDetails: orderDetails
});
// const orderNo = orderResponse.orderNo;
// localStorage.setItem('orderNo', orderNo);

//     alert('결제 및 주문 완료!');
//     orderStore.clearOrderDraft();
//     router.push('/order');

    // 3. 주문 ID 생성
    // const orderId = `orderNo-${Date.now()}-${generateUUID()}`; // ✅ 여기서 고유한 주문번호를 생성
    const orderId = `orderNo-${orderResponse.orderNo}`; // ✅ 여기서 고유한 주문번호를 생성

    // console.log('✅ 생성된 orderId:', orderId); // 이 부분 중요

    // 4. TossPayments 초기화 대기
    await waitForToss(); // ✅ TossPayments 로딩 대기

    const toss = window.TossPayments('test_ck_DpexMgkW36oAl1jdPad9VGbR5ozO'); // ✅ Toss 클라이언트 키
    const successUrl = `${window.location.origin}/order/payment-success`;
    const failUrl = `${window.location.origin}/order/payment-fail`;

    // 5. Toss 결제 요청
    const result = await toss.requestPayment('카드', {
      amount: totalPrice.value,
      orderId,
      orderName: orderStore.orderDraft[0]?.productName || '장바구니 상품',
      customerEmail: userStore.user?.email || 'guest@example.com',
      successUrl: successUrl,
      failUrl: failUrl
    });
      console.log('✅ 생성된 successUrl:', successUrl); // 이 부분도 중요
      console.log('✅ 생성된 failUrl:', failUrl); // 이 부분도 중요
      console.log('✅ totalPrice.value:', totalPrice.value)
      console.log('✅ orderStore.orderDraft:', orderStore.orderDraft)
      } catch (err) {
    alert('결제 또는 주문 중 오류 발생');
    console.error(err);
  }
    //6. 결제 승인 요청
    // await orderStore.confirmPayment({
    //   paymentKey: result.paymentKey,
    //   orderId,
    //   amount: totalPrice.value
    // });
// 7. 주문 생성
// const orderResponse = await orderStore.createOrder({
//   orderId: orderId,
//   //orderNo: orderNo,
//   deliveryName: deliveryName.value,
//   receiver: receiver.value,
//   zipcode: zipcode.value,
//   address1: address1.value,
//   address2: address2.value,
//   phone: phone.value,
//   orderDetails: orderDetails
// });
// const orderNo = orderResponse.orderNo;
// localStorage.setItem('orderNo', orderNo);

//     alert('결제 및 주문 완료!');
//     orderStore.clearOrderDraft();
//     router.push('/order');

//   } catch (err) {
//     alert('결제 또는 주문 중 오류 발생');
//     console.error(err);
//   }
};

</script>

<style scoped >
.order-wrapper {
  max-width: 900px;
  margin: 0 auto;
  font-family: 'Pretendard', sans-serif;
  color: #222;
}

.table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  font-size: 14px;
}

.table th,
.table td {
  vertical-align: middle !important;
}

.form-label {
  font-weight: 600;
  margin-bottom: 4px;
  font-size: 14px;
}

.form-control {
  border-radius: 6px;
  font-size: 14px;
  font-family: 'Pretendard', sans-serif;
}

.form-control:focus {
  border-color: #008BE6;
  box-shadow: 0 0 0 0.2rem rgba(0, 139, 230, 0.2);
}

.btn {
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  border-radius: 6px;
}

.btn-secondary {
  background-color: #008BE6;
  border-color: #008BE6;
  color: white;
}

.btn-secondary:hover {
  background-color: #006bb3;
  border-color: #006bb3;
}

.btn-secondary-light {
  background-color: #f1f5f9;
  border: 1px solid #ddd;
  color: #333;
  font-weight: 500;
}

.btn-secondary-light:hover {
  background-color: #e2e8f0;
  color: #000;
}

.input-group .form-control {
  flex: 1;
}

.alert-warning {
  background-color: #fff3cd;
  border-color: #ffeeba;
  color: #856404;
  font-size: 14px;
}

</style>