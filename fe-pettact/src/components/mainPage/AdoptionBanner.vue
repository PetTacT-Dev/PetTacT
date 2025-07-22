<template>
  <section class="adoption-section">
    <div class="adoption-container">
      <div class="adoption-bg" v-if="!loading && pets.length > 0">
        <div class="pet-image-container">
          <button class="nav-button nav-button-left" @click="prevPet">←</button>
          <img class="pet-image" :src="pets[currentIndex].popfile1 || 'https://placehold.co/448x458'"
            :alt="pets[currentIndex].kindNm || '유기동물 이미지'" />
          <button class="nav-button nav-button-right" @click="nextPet">→</button>
        </div>
        <div class="adoption-info" @click="goToPetDetail(pets[currentIndex].desertionNo)">
          <h2 class="adoption-title">입양안내</h2>
          <p class="adoption-subtitle">새로운 가족을 기다리는 유기동물들의 정보를<br />간편하게 확인해보세요.</p>
          <div class="pet-info">
            <h3 class="pet-name">{{ pets[currentIndex].kindNm }}</h3>
            <p class="pet-details">성별: {{ formatGender(pets[currentIndex].sexCd) }}</p>
            <p class="pet-location">📍 발견 장소: {{ pets[currentIndex].happenPlace }}</p>
            <p class="pet-description">특이사항: {{ pets[currentIndex].specialMark || '없음' }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-5">
        <div v-if="loading">로딩 중...</div>
        <div v-else>표시할 동물이 없습니다.</div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const pets = ref([])
const loading = ref(true)
const currentIndex = ref(0)
const router = useRouter()

const fetchEndingSoonPets = async () => {
  try {
    const res = await axios.get('/v1/pet/abandonment/ending-soon/main', {
      params: { count: 5 }
    })
    pets.value = res.data
  } catch (error) {
    console.error('API 오류:', error)
  } finally {
    loading.value = false
  }
}

const formatGender = (code) => {
  switch (code) {
    case 'M': return '수컷';
    case 'F': return '암컷';
    case 'Q': return '미상';
    default: return '알 수 없음';
  }
}

const goToPetDetail = (desertionNo) => {
  if (desertionNo) { // desertionNo가 존재하는지 확인
    router.push({ name: 'abandonmentDetail', params: { desertionNo: desertionNo } });
  } else {
    console.warn("desertionNo가 없어 상세 페이지로 이동할 수 없습니다.");
  }
};

const nextPet = () => {
  currentIndex.value = (currentIndex.value + 1) % pets.value.length
}

const prevPet = () => {
  currentIndex.value = currentIndex.value === 0
    ? pets.value.length - 1
    : currentIndex.value - 1
}

onMounted(fetchEndingSoonPets)
</script>

<style scoped>
/* Adoption Section */
.adoption-section {
  background-color: #F8F6F2;
}

.adoption-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.adoption-bg {
  background: #F8F6F2;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  display: flex;
  align-items: center;
  gap: 60px;
  min-height: 500px;
}

.pet-image-container {
  flex: 1;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 입체감을 위한 효과 추가 */
  perspective: 1000px;
}

.pet-image {
  width: 400px;
  height: 400px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  /* 강화된 그림자 효과 */
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.2),
    0 8px 16px rgba(0, 0, 0, 0.1),
    0 0 0 8px rgba(255, 255, 255, 0.1);
  /* 3D 효과 */
  transform: translateZ(50px) rotateX(-5deg);
  transition: all 0.3s ease;
}

.nav-button {
  width: 48px;
  height: 48px;
  background: rgba(0, 0, 0, 0.4);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  transition: all 0.3s ease;
}

.nav-button:hover {
  background: rgba(0, 0, 0, 0.6);
  transform: scale(1.1);
}

.nav-button-left {
  left: -24px;
}

.nav-button-right {
  right: -24px;
}

.adoption-info {
  flex: 1;
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.adoption-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.adoption-subtitle {
  font-size: 14px;
  color: #767676;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 32px;
}

.pet-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pet-name {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.pet-details {
  font-size: 18px;
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
}

.pet-location {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.pet-description {
  font-size: 14px;
  font-weight: 400;
  color: #777;
  line-height: 1.5;
}
</style>
