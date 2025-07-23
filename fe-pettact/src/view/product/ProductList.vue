<template>
  <div class="container py-4 text-white">
    <div class="product-wrapper">

    <!-- ✅ 슬라이드 배너 -->
    <div id="productBannerCarousel" class="carousel slide mb-4" data-bs-ride="carousel" data-bs-interval="3000">
      <div class="carousel-inner rounded shadow">
        <div class="carousel-item active">
          <img :src="banner1" class="d-block w-100" alt="배너1" style="max-height: 300px; object-fit: cover;">
        </div>
        <div class="carousel-item">
          <img :src="banner2" class="d-block w-100" alt="배너2" style="max-height: 300px; object-fit: cover;">
        </div>
      </div>
      <button class="carousel-control-prev" type="button" data-bs-target="#productBannerCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon"></span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#productBannerCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon"></span>
      </button>
      
    </div>

    <h1 class="text-black">상품 목록</h1>
    <div class="mb-3">
      <!-- 상품 등록 버튼 (SELLER만 노출) -->
      <button v-if="userStore.user?.userRole === 'ROLE_SELLER' || userStore.user?.userRole === 'ROLE_ADMIN'" @click="goToCreateProduct" class="btn-custom me-2">상품 등록</button>
      <!-- 장바구니 버튼 (로그인한 사용자만 노출) -->
      <button v-if="userStore.user" @click="goToCart" class="btn-custom me-2">장바구니</button>
      <!-- 주문 내역 버튼 (로그인한 사용자만 노출) -->
      <button v-if="userStore.user" @click="goToOrderList" class="btn-custom me-2">주문 내역</button>
    </div>
    <div class="mb-3 d-flex">
        <select v-model="selectedCategory" class="form-select me-2">
          <option value="">카테고리</option>
          <option v-for="category in categories" :key="category.categoryNo" :value="category.categoryNo">{{ category.categoryName }}</option>
        </select>
      <input 
        v-model="searchKeyword" 
        type="text" 
        class="form-control me-2" 
        placeholder="상품명을 입력하세요" 
        @keyup.enter="searchProducts"
      />
      <button class="btn-outline-custom me-2" @click="searchProducts">검색</button>
      <button class="btn-outline-custom me-2" @click="resetSearch">초기화</button>
    </div>
      <div class="mb-4">
        <select v-model="sortOption" @change="searchProducts" class="form-select" style="max-width: 200px;">
          <option value="">정렬 없음</option>
          <option value="priceAsc">가격 낮은순</option>
          <option value="priceDesc">가격 높은순</option>
        </select>
      </div>
          <div v-if="productStore.productList.length" class="row g-3">
            <div 
              v-for="product in productStore.productList" 
              :key="product.productNo" 
              class="col-sm-6 col-md-4 col-lg-3"
            >
              <router-link :to="`/product/${product.productNo}?page=${currentPage}`" class="text-decoration-none text-dark">
                <div class="card h-100">
                  <img 
                    :src="getImageUrl(product.imageUrl)" 
                    alt="상품 이미지" 
                    class="card-img-top" 
                    style="object-fit: cover; height: 200px;"
                  />
                  <div class="card-body">
                    <h5 class="card-title">{{ product.productName }}</h5>
                    <h5 class="card-title">{{ getFormattedPrice(product.productPrice) }} 원</h5>
                    <!-- <p class="text-muted small">등록일: {{ formatDate(product.createdAt) }}</p> -->
                  </div>
                </div>
              </router-link>
            </div>
          </div>
          <nav v-if="productStore.totalPages > 1" class="mt-4">
        <ul class="pagination justify-content-center">
          <li 
            class="page-item" 
            :class="{ disabled: currentPage === 0 }"
            @click="changePage(currentPage - 1)"
          >
            <a class="page-link" href="#">이전</a>
          </li>

          <li 
            class="page-item" 
            v-for="page in productStore.totalPages" 
            :key="page"
            :class="{ active: currentPage === page - 1 }"
            @click="changePage(page - 1)"
          >
            <a class="page-link" href="#">{{ page }}</a>
          </li>

          <li 
            class="page-item" 
            :class="{ disabled: currentPage === productStore.totalPages - 1 }"
            @click="changePage(currentPage + 1)"
          >
            <a class="page-link" href="#">다음</a>
          </li>
        </ul>
      </nav>

    <div v-else>
      상품이 없습니다.
    </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter,useRoute  } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import { useProductStore } from '@/stores/product'
import { useUserStore } from '@/stores/user'

//import banner1 from '@/assets/반려동물 용품1.jpg'//
//import banner2 from '@/assets/반려동물 용품3.jpg'

const productStore = useProductStore()
const router = useRouter()
const userStore = useUserStore()
const route = useRoute(); // ✅ 추가

const searchKeyword = ref('') // 검색어 상태
const selectedCategory = ref('')
const sortOption = ref('');
const categories = computed(() => productStore.categories)
const currentPage = ref(0); // 0부터 시작
const pageSize = 12; // 한 페이지당 10개

// 콘솔로 확인
console.log('현재 로그인 유저:', userStore.user);
console.log('현재 로그인 유저:', JSON.stringify(userStore.user, null, 2));
console.log('유저 권한:', userStore.user?.userRole);

const changePage = (page) => {
  if (page >= 0 && page < productStore.totalPages) {
    currentPage.value = page;
    searchProducts(); // 재검색
  }
};

// 초기화 버튼 눌렀을 때
const resetSearch = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  productStore.fetchProductList('', '')
}

// 검색 버튼 클릭 or Enter 키 눌렀을 때
const searchProducts = () => {
  productStore.fetchProductList(
    searchKeyword.value,
    selectedCategory.value,
    sortOption.value,
    currentPage.value,
    pageSize
  )
}

function getFormattedPrice(price) {
  return new Intl.NumberFormat('ko-KR').format(price);
}

function getImageUrl(imageUrl) {
  if (!imageUrl) return '/default-product.png';
  const baseUrl = 'http://localhost:8080';
  return baseUrl + imageUrl;
}

const goToCreateProduct = () => {
  router.push({ name: 'ProductCreate' }) 
}

const goToOrderList = () => {
  router.push({ name: 'OrderList' })  
}

const goToCart = () => {
  router.push({ name: 'Cart' })
}

onMounted(async () => {

    await userStore.fetchUser();  // ✅ 정상 작동
    console.log('가져온 유저 정보:', userStore.user);

    productStore.fetchCategories();

    const page = parseInt(route.query.page) || 0;
    currentPage.value = page;

    productStore.fetchProductList(
      searchKeyword.value,
      selectedCategory.value,
      sortOption.value,
      currentPage.value,
      pageSize
    );
  // productStore.fetchCategories()
  // productStore.fetchProductList('', '', '', 0, 12) // ✅ 페이지 0, 사이즈 10 명시
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Paytone+One&display=swap');
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css');

.product-wrapper {
  max-width: 1000px; /* ✅ 적절한 최대 너비 (원하면 1000px로도 가능) */
  margin: 0 auto;
}

/* 위에서 제공한 스타일 복붙 */

.btn-custom {
  background-color: #008BE6;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.btn-custom:hover {
  background-color: #0074c7;
}

.btn-outline-custom {
  background-color: transparent;
  border: 2px solid #008BE6;
  color: #008BE6;
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  border-radius: 8px;
  padding: 8px 16px;
}

.btn-outline-custom:hover {
  background-color: #008BE6;
  color: white;
}

.form-select,
.form-control {
  font-family: 'Pretendard', sans-serif;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.form-select:focus,
.form-control:focus {
  border-color: #008BE6;
  box-shadow: 0 0 0 0.2rem rgba(0, 139, 230, 0.25);
}

.card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.card-title {
  font-family: 'Pretendard', sans-serif;
  font-size: 16px;
  font-weight: 600;
}

.pagination .page-link {
  color: #008BE6;
  font-family: 'Pretendard', sans-serif;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  transition: background-color 0.2s;
}

.pagination .page-item.active .page-link {
  background-color: #008BE6;
  border-color: #008BE6;
  color: white;
}

.pagination .page-link:hover {
  background-color: #e6f4ff;
  color: #0074c7;
}
</style>

