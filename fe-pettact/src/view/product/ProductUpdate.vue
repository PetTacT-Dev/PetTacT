<template>
  <div v-if="form" class="container py-4">
    <h2 class="mb-4 text-center">상품 수정</h2>

    <!-- form wrapper 추가 -->
    <div class="product-edit-wrapper">
      <form @submit.prevent="submitForm" class="row g-3">

        <!-- 기존 이미지 리스트 -->
        <div v-if="form.imageUrls && form.imageUrls.length" class="col-12">
          <h5>기존 이미지</h5>
          <div class="d-flex flex-wrap gap-3">
            <div
              v-for="(url, idx) in form.imageUrls"
              :key="idx"
              class="position-relative product-image-wrapper"
            >
              <img
                :src="getImageUrl(url)"
                alt="상품 이미지"
                class="img-fluid rounded product-image"
              />
              <button
                type="button"
                @click="removeExistingImage(idx)"
                class="btn btn-danger btn-sm position-absolute top-0 end-0 rounded-circle btn-delete-image"
                aria-label="이미지 삭제"
              >
                &times;
              </button>
            </div>
          </div>
        </div>

        <!-- 새로 선택한 파일 리스트 -->
        <div v-if="selectedFiles.length" class="col-12">
          <h5>새로 추가할 이미지</h5>
          <ul>
            <li v-for="(file, idx) in selectedFiles" :key="idx">{{ file.name }}</li>
          </ul>
          <button type="button" @click="clearSelectedFiles" class="btn btn-secondary btn-sm">선택 취소</button>
        </div>

        <div class="col-md-6">
          <label for="productName" class="form-label">상품명</label>
          <input id="productName" v-model="form.productName" required class="form-control" />
        </div>

        <div class="col-md-3">
          <label for="productPrice" class="form-label">가격</label>
          <input id="productPrice" v-model.number="form.productPrice" type="number" min="1" required class="form-control" />
        </div>

        <div class="col-md-3">
          <label for="productStock" class="form-label">수량</label>
          <input id="productStock" v-model.number="form.productStock" type="number" min="0" required class="form-control" />
        </div>

        <!-- 카테고리 -->
        <div class="col-md-6">
          <label for="categoryNo" class="form-label">카테고리</label>
          <select id="categoryNo" v-model.number="form.categoryNo" class="form-select" required>
            <option
              v-for="category in categories"
              :key="category.categoryNo"
              :value="category.categoryNo"
            >
              {{ category.categoryName }}
            </option>
          </select>
        </div>

        <div class="col-12">
          <label for="productDescription" class="form-label">설명</label>
          <textarea id="productDescription" v-model="form.productDescription" required class="form-control" rows="4"></textarea>
        </div>

        <div class="col-12">
          <label for="files" class="form-label">첨부파일</label>
          <input id="files" ref="fileInputRef" type="file" @change="handleFileChange" multiple class="form-control" />
        </div>

        <div class="col-12 d-flex gap-2">
          <!-- 수정 버튼: 셀러이면서 본인 글인 경우만 표시 -->
          <button
            v-if="isSeller && isOwner"
            type="submit"
            :disabled="isLoading"
            class="btn btn-secondary"
          >
            {{ isLoading ? '수정 중...' : '수정하기' }}
          </button>
          <button @click="goToList" type="button" class="btn btn-secondary">상품 목록</button>
        </div>

      </form>
    </div>
  </div>

  <div v-else class="container py-4 text-center">
    상품 정보를 불러오는 중...
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useProductStore } from '@/stores/product';
import { useFileStore } from '@/stores/file';
import { useUserStore } from '@/stores/user'

const route = useRoute();
const router = useRouter();

const productStore = useProductStore();
const userStore = useUserStore();
const fileStore = useFileStore();

const productNo = Number(route.params.id);

const form = ref(null);
const isLoading = ref(false);
const selectedFiles = ref([]);
const fileInputRef = ref(null);
const categories = computed(() => productStore.categories);

// 유저 권한 체크
const isSeller = computed(() => userStore.user?.userRole === 'ROLE_SELLER')
const isOwner = computed(() => userStore.user?.userNo === productStore.productDetail?.userNo)

// 상품 상세 데이터로 form 초기화
const initializeForm = (data) => {
  // data.imageUrls가 객체 배열일 경우 처리
  const images = data.imageUrls
    ? data.imageUrls.map(img => (typeof img === 'string' ? img : img.imageUrl || `/files/${img.fileNo}`))
    : [];

  form.value = {
    productName: data.productName,
    productPrice: data.productPrice,
    productStock: data.productStock,
    categoryNo: Number(data.categoryNo),
    productDescription: data.productDescription,
    productStatus: data.productStatus ?? true,
    imageUrls: images,
  };
};

const getImageUrl = (url) => {
  if (!url) return '/default-product.png';
  const baseUrl = 'http://localhost:8080'; // 실제 주소로 변경하세요
  return baseUrl + url;
};


// 기존 이미지 삭제
function removeExistingImage(index) {
  form.value.imageUrls.splice(index, 1);
}

// 새로 선택한 파일 리스트 초기화
function clearSelectedFiles() {
  selectedFiles.value = [];
}

const goToList = () => {
  router.push('/product/list');
};

const fetchProduct = async () => {
  try {
    await productStore.fetchProductDetail(productNo);
    initializeForm(productStore.productDetail);
  } catch (error) {
    alert('상품 정보를 불러오는데 실패했습니다.');
    console.error(error);
  }
};

const MAX_FILE_SIZE_MB = 10;
const MAX_FILE_SIZE = MAX_FILE_SIZE_MB * 1024 * 1024;

// ✅ 파일 선택 시 용량 제한 검사
const handleFileChange = (event) => {
  const files = Array.from(event.target.files);

  const oversized = files.find(file => file.size > MAX_FILE_SIZE);
  if (oversized) {
    alert(`"${oversized.name}" 파일은 ${MAX_FILE_SIZE_MB}MB를 초과하여 업로드할 수 없습니다.`);
    selectedFiles.value = [];
    event.target.value = '';
    return;
  }

  selectedFiles.value = files;
};

const submitForm = async () => {
  if (form.value.productPrice < 1) {
    alert('가격은 1 이상이어야 합니다.');
    return;
  }
  if (form.value.productStock < 0) {
    alert('수량은 0 이상이어야 합니다.');
    return;
  }
  if (!(form.value.categoryNo >= 1 && form.value.categoryNo <= 10)) {
    alert('카테고리는 1부터 10 사이여야 합니다.');
    return;
  }

    // ✅ 이미지가 1장도 없으면 막기
  const totalImages = form.value.imageUrls.length + selectedFiles.value.length;
  if (totalImages < 1) {
    alert('이미지는 최소 1장 이상 등록해야 합니다.');
    return;
  }

  isLoading.value = true;

  try {
    let uploadedFiles = [];

    // 새로 추가한 파일 업로드
    if (selectedFiles.value.length > 0) {
      uploadedFiles = await fileStore.uploadFiles({
        files: selectedFiles.value,
        referenceTable: 'PRODUCT',
        referenceNo: productNo,
      });

      // 업로드된 파일들에서 이미지 URL 리스트 생성
      const newImageUrls = uploadedFiles.map(
        (f) => f.imageUrl || `/files/${f.fileNo}`
      );

      // 기존 이미지에 새 이미지 추가
      form.value.imageUrls = [...form.value.imageUrls, ...newImageUrls];
    }

    // 업데이트 요청 (form.value 에 최종 이미지 배열 포함됨)
    await productStore.updateProduct(productNo, form.value, selectedFiles.value);

    alert('상품이 수정되었습니다.');

    router.push(`/product/${productNo}`);
  } catch (error) {
    alert(error.response?.data?.message || '상품 수정에 실패했습니다.');
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  productStore.fetchCategories();
  fetchProduct();
});
</script>

<style scoped>


@import url('https://fonts.googleapis.com/css2?family=Paytone+One&display=swap');
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css');

.container {
  font-family: 'Pretendard', sans-serif;
  color: #333;
}

/* 중앙 정렬 및 max-width 제한 */
form {
  max-width: 1000px;
  margin: 0 auto;
  background-color: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* 제목 정렬 */
h2 {
  font-weight: bold;
  font-family: 'Pretendard', sans-serif;
  color: #222;
}

/* 라벨 */
.form-label {
  font-weight: 500;
  color: #444;
}

/* 인풋/셀렉트/텍스트에어리어 */
.form-control,
.form-select {
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 14px;
}

/* 버튼 스타일 통일 */
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

/* 기존 이미지 썸네일 */
.position-relative img {
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

/* 이미지 삭제 버튼 */
.position-absolute.btn-danger {
  background-color: #dc3545;
  border: none;
  font-size: 14px;
  font-weight: bold;
  padding: 0;
}

/* 새로 추가한 이미지 목록 */
ul {
  padding-left: 1rem;
}

li {
  font-size: 14px;
  line-height: 1.6;
}

/* 모바일 대응 */
@media (max-width: 768px) {
  form {
    padding: 16px;
  }

  .form-label {
    font-size: 14px;
  }

  h2 {
    font-size: 20px;
  }
}

</style>
