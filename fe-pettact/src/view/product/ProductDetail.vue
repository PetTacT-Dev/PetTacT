<template>
  <div class="container py-4">
  <div class="product-detail-wrapper">
    <div v-if="productStore.productDetail" class="card shadow p-4">
      <h1 class="card-title mb-4">{{ productStore.productDetail.productName }}</h1>

      <!-- 이미지 영역 -->
      <div v-if="productStore.productDetail.imageUrls?.length" class="d-flex overflow-auto gap-3 mb-4">
        <img
          v-for="(url, index) in productStore.productDetail.imageUrls"
          :key="index"
          :src="getImageUrl(url)"
          alt="상품 이미지"
          class="img-thumbnail"
          style="width: 450px; height: 450px; object-fit: cover;"
        />
      </div>

      <!-- 상품 정보 -->
      <ul class="list-group list-group-flush mb-4">
        <li class="list-group-item">👤 판매자 : {{ productStore.productDetail.userName }}</li>
        <li class="list-group-item">📂 카테고리 : {{ productStore.productDetail.categoryName }}</li>
        <li class="list-group-item">1️⃣ 수량 : {{ productStore.productDetail.productStock }} 개</li>
        <li class="list-group-item">💰 가격 : {{ getFormattedPrice(productStore.productDetail.productPrice) }} 원</li>
        <li class="list-group-item">📦 설명 : {{ productStore.productDetail.productDescription }}</li>
      </ul>

      <!-- 버튼들 -->
      <div class="d-flex flex-wrap gap-2">
        <button @click="goToList" class="btn btn-secondary me-2 mb-2">상품 목록</button>

        <!-- 수정 버튼 -->
        <button
          v-if="isAdmin || (isSeller && isOwner)"
          @click="goToEdit"
          class="btn btn-secondary me-2 mb-2"
        >
          수정하기
        </button>

        <!-- 장바구니 버튼 -->
        <button
          v-if="isLoggedIn"
          @click="addToCart"
          class="btn btn-secondary me-2 mb-2"
        >
          장바구니에 추가
        </button>

        <!-- 삭제 버튼 -->
        <button
          v-if="isAdmin || (isSeller && isOwner)"
          @click="deleteProduct"
          class="btn btn-secondary me-2 mb-2"
        >
          삭제하기
        </button>
      </div>

      </div>

    <div v-else-if="productStore.loading" class="text-center py-5">⏳ 상품 정보를 불러오는 중...</div>
    <div v-else-if="productStore.error" class="text-center text-danger py-5">❌ 상품 정보를 불러오는데 실패했습니다.</div>
  </div>
  </div>
</template>

<script setup>
import { onMounted , computed } from 'vue'
import { useRoute, useRouter } from 'vue-router' 
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'


const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()
const userStore = useUserStore()

//버튼 권한 검증
const isLoggedIn = computed(() => !!userStore.user)
const isAdmin = computed(() => userStore.user?.userRole === 'ROLE_ADMIN')
const isSeller = computed(() => userStore.user?.userRole === 'ROLE_SELLER')
const isOwner = computed(() => userStore.user?.userNo === productStore.productDetail?.userNo)

const productNo = route.params.id

function getFormattedPrice(price) {
  return new Intl.NumberFormat('ko-KR').format(price);
}

function getImageUrl(url) {
  if (!url) return '/default-product.png';
  const baseUrl = 'http://localhost:8080';
  return baseUrl + url;
}

const goToList = () => {
  const page = route.query.page || 0; // URL에서 ?page= 값을 읽음
  router.push(`/product?page=${page}`);
}

const goToEdit = () => {
  router.push({ name: 'ProductUpdate', params: { productNo } })
}

const addToCart = async () => {
  try {
    const productNo = productStore.productDetail.productNo
    const productStock = 1
    const userNo = productStore.productDetail.userNo

    if (!productNo) {
      alert('상품 정보가 올바르지 않습니다.')
      return
    }

    await cartStore.addToCart(productNo, productStock)
    alert(`${productStore.productDetail.productName}을(를) 장바구니에 추가했습니다.`)
    if (!confirm('장바구니으로 이동하시겠습니까?')) return
    router.push('/cart')
  } catch (err) {
    console.error('장바구니 추가 실패:', err)
    alert('장바구니에 추가하지 못했습니다.')
  }
}

const deleteProduct = async () => {
  if (!confirm('정말 이 상품을 삭제하시겠습니까?')) return

  await productStore.deleteProduct(productNo)

  if (productStore.error) {
    alert('상품 삭제에 실패했습니다.')
  } else {
    alert('상품이 삭제되었습니다.')
    router.push('/product')
  }
}

onMounted(() => {
  productStore.fetchProductDetail(productNo)
})

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Paytone+One&display=swap');
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css');

.product-detail-wrapper {
  max-width: 1000px;
  margin: 0 auto;
}
/* 전체 폰트 및 카드 정리 */
.container {
  font-family: 'Pretendard', sans-serif;
  color: #333;
}

.card {
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: none;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  font-family: 'Pretendard', sans-serif;
  margin-bottom: 1rem;
}

/* 이미지 썸네일 정리 */
.img-thumbnail {
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

/* 리스트 그룹 스타일 통일 */
.list-group-item {
  font-size: 15px;
  border: none;
  border-bottom: 1px solid #f0f0f0;
  padding: 12px 0;
  background-color: transparent;
}

/* 마지막 항목 border 제거 */
.list-group-item:last-child {
  border-bottom: none;
}

/* 버튼 스타일 재사용 */
.btn-secondary {
  background-color: #008BE6;
  border: none;
  color: white;
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.btn-secondary:hover {
  background-color: #0074c7;
}

/* 에러 / 로딩 텍스트 */
.text-danger {
  font-weight: 500;
}

.text-center {
  font-family: 'Pretendard', sans-serif;
}
</style>
