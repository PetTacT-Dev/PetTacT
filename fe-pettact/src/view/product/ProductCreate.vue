<template>
  <div class="container py-4 text-white">
    <h2 class="mb-4 text-center">상품 등록</h2>

    <form @submit.prevent="submitForm" class="row g-3">
      <!-- 상품명 -->
      <div class="col-md-6">
        <label for="productName" class="form-label">상품명</label>
        <input id="productName" v-model="form.productName" class="form-control" required />
      </div>

      <!-- 가격 -->
      <div class="col-md-3">
        <label for="productPrice" class="form-label">가격</label>
        <input id="productPrice" v-model.number="form.productPrice" type="number" min="1" class="form-control" required />
      </div>

      <!-- 수량 -->
      <div class="col-md-3">
        <label for="productStock" class="form-label">수량 🔒</label>
        <input id="productStock" v-model.number="form.productStock" type="number" min="1" max="1" @input="enforceOne" class="form-control" required/>
      </div>

      <!-- 카테고리 -->
      <div class="col-md-6">
        <label for="categoryNo" class="form-label">카테고리</label>
        <select id="categoryNo" v-model.number="form.categoryNo" class="form-select" required>
          <option value="">카테고리를 선택하세요</option>
          <option 
            v-for="category in categories" 
            :key="category.categoryNo" 
            :value="category.categoryNo"
          >
            {{ category.categoryName }}
          </option>
        </select>
      </div>

      <!-- 설명 -->
      <div class="col-md-12">
        <label for="productDescription" class="form-label">설명</label>
        <textarea id="productDescription" v-model="form.productDescription" rows="4" class="form-control" required></textarea>
      </div>

      <!-- 첨부파일 -->
      <div class="col-md-12">
        <label for="files" class="form-label">첨부파일</label>
        <input id="files" type="file" @change="handleFileChange" multiple class="form-control" />
      </div>

      <!-- 버튼 -->
      <div class="col-12 d-flex gap-2">
        <button type="submit" :disabled="isLoading" class="btn btn-secondary">
          {{ isLoading ? '등록 중...' : '등록하기' }}
        </button>
        <button @click="goToList" type="button" class="btn btn-secondary">
          상품 목록
        </button>
      </div>
    </form>
  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'; // ← computed 추가
import { useRouter } from 'vue-router';
import { useProductStore } from '@/stores/product';
import { useUserStore } from '@/stores/user'
//import { useFileStore } from '@/stores/file'; // ✅ 파일 스토어 추가

const router = useRouter();
const productStore = useProductStore();
const userStore = useUserStore(); // ✅ 추가
//const fileStore = useFileStore(); // ✅ 파일 스토어 인스턴스

const form = ref({
  productName: '',
  productDescription: '',
  productPrice: '',
  productStock: '',
  productStatus: true,
  categoryNo: 1,
  imageUrls: [],
});

const selectedFiles = ref([]);
const isLoading = ref(false);
const categories = computed(() => productStore.categories);

const goToList = () => {
  router.push('/product');
};

// 파일 선택 핸들링
const MAX_FILE_SIZE_MB = 10;
const MAX_FILE_SIZE = MAX_FILE_SIZE_MB * 1024 * 1024;

const handleFileChange = (event) => {
  const files = event?.target?.files;

  if (!files || files.length === 0) {
    console.warn('선택된 파일이 없습니다.');
    return;
  }

  const selected = Array.from(files);

  // 파일 중 하나라도 10MB 초과하는 경우 경고
  const oversized = selected.find(file => file.size > MAX_FILE_SIZE);
  if (oversized) {
    alert(`"${oversized.name}" 파일은 ${MAX_FILE_SIZE_MB}MB를 초과해서 업로드할 수 없습니다.`);
    selectedFiles.value = [];
    event.target.value = ''; // 파일 input 비워주기
    return;
  }

  selectedFiles.value = selected;
};

const enforceOne = () => {
  if (form.value.productStock !== 1) {
    alert('수량은 1만 입력 가능합니다.');
    form.value.productStock = 1;
  }
};

const submitForm = async () => {
  
  if (form.value.productPrice <= 0) {
    alert('가격은 1 이상이어야 합니다.');
    return;
  }
    if (selectedFiles.value.length === 0) {
    alert('이미지 1장은 필수입니다.');
    return;
  }

  isLoading.value = true;

  try {
    // 1. 상품 등록
    const { productNo } = await productStore.createProduct(form.value, selectedFiles.value);
    alert('상품이 등록되었습니다.');
    router.push('/product');
  } catch (error) {
    const message = error?.response?.data?.message || '상품 등록에 실패했습니다.';
    alert(message);
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  await userStore.fetchUser();

  console.log('유저 정보:', userStore.user);          // 유저 데이터 확인
  console.log('유저 역할:', userStore.user?.userRole); // 역할 값 확인

  const role = userStore.user?.userRole;
  if (role !== 'ROLE_SELLER' && role !== 'ROLE_ADMIN') {
    alert('접근 권한이 없습니다.');
    router.push('/');
    return;
  }
  productStore.fetchCategories();
});

</script>

