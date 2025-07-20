<template>
  <div class="container py-4 text-dark cart-view">
    <div class="mb-3">
      <p v-if="user">
        👤 사용자: {{ user.userNickname }} ({{ user.userEmail }})
      </p>
    </div>
    <h2 class="mb-4">장바구니</h2>

    <div class="mb-3 d-flex gap-2">
      <button @click="goToList" class="btn btn-outline-primary">상품 목록</button>
      <button v-if="isLoggedIn" @click="goToOrderList" class="btn btn-outline-primary">주문 내역</button>
    </div>

    <div v-if="cartStore.loading" class="mb-3">불러오는 중...</div>
    <div v-else-if="cartStore.cartItems.length === 0" class="alert alert-info">
      장바구니가 비어 있습니다.
    </div>

    <table v-else class="table table-striped table-bordered align-middle">
      <thead class="table-primary text-center">
        <tr>
          <th style="width: 40px;"></th>
          <th>상품 정보</th>
          <th style="width: 100px;">수량</th>
          <th style="width: 120px;">주문 금액</th>
          <th style="width: 80px;"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in cartStore.cartItems" :key="item.cartNo">
          <td class="text-center align-middle">
            <input
              type="checkbox"
              :value="item.cartNo"
              v-model="selectedItems"
              :disabled="!item.productName"
              class="custom-checkbox"
            />
          </td>
          <td>
            <div class="d-flex align-items-center gap-3">
              <img
                v-if="item.imageUrl"
                :src="getImageUrl(item.imageUrl)"
                alt="상품 이미지"
                class="product-image"
              />
              <div>
                <div v-if="item.productName" class="product-name-item">
                  <router-link :to="`/product/${item.productNo}`" class="text-decoration-none">
                    {{ item.productName }}
                  </router-link>
                </div>
                <div v-else class="text-warning small">⚠️ 판매 중지 상품</div>
              </div>
            </div>
          </td>
          <td class="text-center">
            <div v-if="item.productName" class="input-group input-group-sm justify-content-center">
              <button
                class="btn btn-light text-dark"
                @click="changeQuantity(item, -1)"
                :disabled="item.productStock <= 1"
              >−</button>
              <input
                class="form-control text-center"
                :value="item.productStock"
                readonly
                style="max-width: 50px;"
              />
              <button
                class="btn btn-light text-dark"
                @click="changeQuantity(item, 1)"
              >+</button>
            </div>
          </td>
          <td class="text-end">
            <span v-if="item.productName">
              {{ getFormattedPrice(item.productPrice * item.productStock) }} 원
            </span>
          </td>
          <td class="text-center">
            <button v-if="isLoggedIn" @click="removeItem(item.cartNo)" class="btn btn-outline-danger btn-sm">
              삭제
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="mt-3">
      <button
        @click="orderSelectedItems"
        :disabled="selectedItems.length === 0"
        class="btn btn-primary"
        type="button"
      >
        구매하기 ({{ selectedItems.length }}개 선택됨)
      </button>
    </div>
  </div>

  <nav v-if="cartStore.totalPages > 1" class="mt-4 d-flex justify-content-center">
    <ul class="pagination mb-0">
      <li class="page-item" :class="{ disabled: cartStore.currentPage === 0 }">
        <button class="page-link" @click="changePage(cartStore.currentPage - 1)">이전</button>
      </li>
      <li
        v-for="page in cartStore.totalPages"
        :key="page"
        class="page-item"
        :class="{ active: page - 1 === cartStore.currentPage }"
      >
        <button class="page-link" @click="changePage(page - 1)">{{ page }}</button>
      </li>
      <li class="page-item" :class="{ disabled: cartStore.currentPage + 1 >= cartStore.totalPages }">
        <button class="page-link" @click="changePage(cartStore.currentPage + 1)">다음</button>
      </li>
    </ul>
  </nav>
</template>



<script setup>
import { ref, onMounted , computed , watch } from 'vue';
import { useCartStore } from '@/stores/cart';
import { useOrderStore } from '@/stores/order';  // order 스토어 임포트
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const cartStore = useCartStore();
const orderStore = useOrderStore();  // order 스토어 인스턴스 생성
const router = useRouter();
const userStore = useUserStore();

const isLoggedIn = computed(() => !!userStore.user);
const user = computed(() => userStore.user)

// 선택된 cartNo들을 저장하는 배열
const selectedItems = ref([]);

// ✅ [추가] 선택된 상품의 상세 정보를 저장하는 Map
const selectedItemDetails = ref(new Map());

const getFormattedPrice = (price) => {
  return new Intl.NumberFormat('ko-KR').format(price);
};

const goToOrderList = () => {
  router.push('/order')
};

const changePage = (pageNum) => {
  if (pageNum >= 0 && pageNum < cartStore.totalPages) {
    cartStore.fetchCart(pageNum);

    // 페이지 이동 시 선택 초기화
    //selectedItems.value = [];
    
    // ✅ 대신 현재 페이지에서 체크되지 않은 항목만 제거하도록 처리
    selectedItems.value = selectedItems.value.filter(cartNo =>
      selectedItemDetails.value.has(cartNo)
    );
  }
};

const changeQuantity = (item, delta) => { // 수량 변경
  const newQty = item.productStock + delta;
  if (newQty < 1) return;

  item.productStock = newQty;
  cartStore.updateCartItem(item.productNo, item.productStock);
};

function getImageUrl(imageUrl) {
  if (!imageUrl) return '/default-product.png';
  const baseUrl = 'http://localhost:8080'; // 실제 백엔드 이미지 URL 경로
  return baseUrl + imageUrl;
};

const goToList = () => {
  router.push('/product') 
};

const removeItem = async (cartNo) => {
  if (confirm('삭제하시겠습니까?')) {
    try {
      // 선택 목록에서도 제거
      const index = selectedItems.value.indexOf(cartNo);
      if (index !== -1) selectedItems.value.splice(index, 1);

      // ✅ 상세 정보에서도 제거
      selectedItemDetails.value.delete(cartNo);

      // 삭제 요청 & 상태 반영
      await cartStore.deleteCartItem(cartNo);
    } catch (err) {
      alert('삭제 중 오류가 발생했습니다.');
      console.error(err);
    }
  }
};

onMounted(async () => {
  await userStore.fetchUser();
  console.log('👤 유저 정보:', userStore.user);
  cartStore.fetchCart().then(() => {
    console.log(cartStore.cartItems);
  });
});

// ✅ selectedItems에 변화가 생기면 상세정보 Map을 업데이트
watch(selectedItems, (newSelected) => {
  newSelected.forEach(cartNo => {
    const item = cartStore.cartItems.find(item => item.cartNo === cartNo);
    if (item) {
      selectedItemDetails.value.set(cartNo, { ...item });
    }
  });

  // 선택 해제된 항목은 제거
  for (const cartNo of selectedItemDetails.value.keys()) {
    if (!newSelected.includes(cartNo)) {
      selectedItemDetails.value.delete(cartNo);
    }
  }
});

const orderSelectedItems = async () => {
  if (selectedItems.value.length === 0) {
    alert('주문할 상품을 선택해주세요.')
    return
  }

  // 1️⃣ 주문 전에 cart 재조회해서 최신 수량 반영
  await cartStore.fetchCart()

  // const itemsToOrder = cartStore.cartItems.filter(item =>
  //   selectedItems.value.includes(item.cartNo)
  // )

  // ✅ 수정: 선택된 모든 상세정보에서 주문 목록 생성
  const itemsToOrder = Array.from(selectedItemDetails.value.values());

  const orderDetails = itemsToOrder.map(item => ({
    productNo: item.productNo,
    productName: item.productName,      // ✅ 추가
    productPrice: item.productPrice,    // ✅ 추가
    productStock: item.productStock
  }))

  try {
    //const result = await orderStore.createOrder(orderDetails)
    alert('주문서가 성공적으로 생성되었습니다.')
    selectedItems.value = []
    selectedItemDetails.value.clear(); // ✅ 주문 완료 후 상세 정보 초기화
    await cartStore.fetchCart()
    orderStore.setOrderDraft(itemsToOrder);  
    router.push('/order-sheet')
  } catch (error) {
    console.error('주문 생성 실패:', error)
    alert('주문서 생성에 실패했습니다.')
  }
};
</script>

<style scoped>
.product-image {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #ddd;
}

/* 버튼 스타일 */
.btn-primary {
  background-color: #008be6;
  border-color: #008be6;
  font-weight: 600;
  transition: background-color 0.2s ease;
  border-radius: 8px;
  padding: 10px 18px;
}

.btn-primary:hover {
  background-color: #006bb3;
  border-color: #006bb3;
  color: white;
}

.btn-outline-primary {
  background-color: transparent;
  border: 2px solid #008be6;
  color: #008be6;
  font-weight: 600;
  border-radius: 8px;
  padding: 8px 16px;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.btn-outline-primary:hover {
  background-color: #008be6;
  color: white;
}

/* 삭제 버튼 */
.btn-outline-danger {
  color: #dc3545;
  border-color: #dc3545;
  border-radius: 8px;
  padding: 6px 12px;
  font-weight: 600;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  color: white;
}

/* 테이블 */
.table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgb(0 0 0 / 0.08);
  font-size: 14px;
}

.table-primary {
  background-color: #008be6;
  color: white;
  font-weight: 600;
  font-size: 14px;
  text-align: center;
  vertical-align: middle;
}

.cart-view {
  max-width: 940px;
  margin: 0 auto;
  color: #2e2e2e;
}

/* 체크박스 */
.custom-checkbox {
  cursor: pointer;
  width: 18px;
  height: 18px;
}

/* 페이징 */
.pagination {
  --bs-pagination-color: #008be6;
  --bs-pagination-hover-color: #0056b3;
  --bs-pagination-active-bg: #008be6;
  --bs-pagination-active-color: white;
  border-radius: 8px;
  font-weight: 600;
}

</style>
