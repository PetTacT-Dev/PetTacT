<template>
  <div class="container mt-4">
    <h2>AI 반려동물 일기 작성</h2>

    <!-- 1. 펫 선택 -->
    <select v-model="selectedPetId" class="form-control mb-3" required>
      <option value="">반려동물을 선택해주세요</option>
      <option v-for="p in petList" :key="p.petId" :value="p.petId">
        {{ p.petName }} ({{ p.kindNm }})
      </option>
    </select>

    <!-- 2. 프롬프트 입력 -->
    <textarea v-model="prompt" class="form-control mb-3" rows="4" placeholder="오늘 어떤 일이 있었는지 입력해주세요" required />

    <!-- 3. 일기 생성 버튼 -->
    <button class="btn btn-primary mb-3" @click="createDiary" :disabled="loading">
      {{ loading ? '생성 중...' : '일기 생성' }}
    </button>

    <!-- 4. 생성된 일기 결과 -->
    <div v-if="generated.diaryContent" class="alert alert-success mt-3">
      <h5>📝 생성된 일기</h5>
      <p>{{ generated.diaryContent }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const petList = ref([]);
const selectedPetId = ref('');
const prompt = ref('');
const loading = ref(false);
const generated = ref({
  petId: null,
  prompt: '',
  diaryContent: ''
});

// 1. 사용자 펫 목록 가져오기
const fetchUserPets = () => {
  axios.get('/v1/pet/list') // 로그인한 유저 기준으로 펫 목록 조회
    .then(res => {
      petList.value = res.data.content || res.data; // Page 타입 또는 일반 리스트
    })
    .catch(err => {
      console.error(err);
      alert('반려동물 목록을 불러오는 데 실패했습니다.');
    });
};

// 2. 일기 생성 및 저장 요청
const createDiary = () => {
  if (!selectedPetId.value || !prompt.value.trim()) {
    alert('반려동물과 프롬프트를 모두 입력해주세요.');
    return;
  }

  loading.value = true;

  axios.post('/v1/diary/create', {
    petId: selectedPetId.value,
    prompt: prompt.value.trim()
  })
  .then(res => {
    generated.value = res.data;
    alert('일기가 성공적으로 작성되었습니다.');
  })
  .catch(err => {
    console.error(err);
    alert('일기 생성에 실패했습니다.');
  })
  .finally(() => {
    loading.value = false;
  });
};

onMounted(() => {
  fetchUserPets();
});
</script>
