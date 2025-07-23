<template>
    <div class="border-bottom py-3">
        <!-- 상단: 등록일 + 상세보기 -->
        <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="text-muted small">등록일: {{ formatDate(product.createdAt) }}</div>
            <div>
                <button class="btn btn-sm btn-link text-decoration-none p-0" @click="goToDetail">
                    상세보기 &gt;
                </button>
            </div>
        </div>

        <!-- 본문: 이미지 + 정보 + 액션 -->
        <div class="d-flex justify-content-between align-items-start">
            <!-- 왼쪽: 이미지 -->
            <img :src="getImageUrl(product.imageUrl)" alt="상품 이미지" style="width: 100px; height: 100px; object-fit: cover;"
                class="me-3 rounded" />

            <!-- 가운데: 상품 정보 -->
            <div class="flex-grow-1">
                <h6 class="fw-bold mb-1">{{ product.productName }}</h6>
                <div class="text-muted small mb-1">
                    카테고리: {{ product.categoryName }} | 재고: {{ product.productStock }}개
                </div>
                <div class="fw-semibold">{{ product.productPrice.toLocaleString() }}원</div>
            </div>

            <!-- 오른쪽: 수정/삭제 버튼 -->
            <div class="text-end">
                <button class="btn btn-sm btn-outline-primary me-1" @click="goToUpdate">수정</button>
                <button class="btn btn-sm btn-outline-danger me-1" @click="deleteProduct">삭제</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from 'axios'
import { useRouter } from 'vue-router'
import { inject } from 'vue'

const router = useRouter()
const { product } = defineProps({ product: Object })
const emit = defineEmits(['deleted'])


const formatDate = (str) => new Date(str).toLocaleDateString('ko-KR')

// ✅ 이미지 URL을 완성하는 함수 추가
const getImageUrl = (imageUrl) => {
  if (!imageUrl) return '/default-product.png' // 기본 이미지 설정 (선택)
  const baseUrl = 'http://localhost:8080' // 백엔드 주소에 맞게 수정
  return imageUrl.startsWith('http') ? imageUrl : baseUrl + imageUrl
}

const goToUpdate = () => {
    router.push({ name: 'ProductUpdate', params: { id: product.productNo } })
}

const goToDetail = () => {
    router.push({ name: 'ProductDetail', params: { id: product.productNo } })
}

const deleteProduct = async () => {
    if (!confirm('해당 상품을 삭제하시겠습니까?')) return;

    try {
        await axios.patch(`/v1/product/delete/${product.productNo}`)
        alert('삭제 완료되었습니다.')
        emit('deleted', product.productNo)
   } catch (err) {
        console.error(err)
        alert('삭제에 실패했습니다.')
    }
}
</script>
