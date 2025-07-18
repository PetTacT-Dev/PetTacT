<template>
  <div class="container mt-4">
    <h2>📓 {{ petName }}의 일기 목록</h2>

    <!-- 일기 리스트 -->
    <div v-if="diaryList.length > 0">
      <div v-for="diary in diaryList" :key="diary.diaryId" class="card mb-3">
        <div class="card-body">
          <p><strong>프롬프트:</strong> {{ diary.prompt }}</p>
          <p><strong>일기번호:</strong> {{ diary.diaryId }}</p>
          <p><strong>내용:</strong> {{ diary.diaryContent }}</p>
          <p><small class="text-muted">작성일: {{ formatDate(diary.createdAt) }}</small></p>
          <button class="btn btn-danger btn-sm" @click="deleteDiary(diary.diaryId)">삭제</button>
          <button class="btn btn-danger btn-sm" @click="goToPetList">반려동물 목록으로</button>
        </div>
      </div>
    </div>

    <div v-else class="alert alert-info">
      작성된 일기가 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute , useRouter} from 'vue-router';

const route = useRoute();
const router = useRouter();
const petId = route.params.petId;

const diaryList = ref([]);
const petName = ref('');

// 1. 일기 목록 조회
const fetchDiaryList = () => {
  axios.get(`/v1/diary/${petId}`)
    .then(res => {
      diaryList.value = res.data;
    })
    .catch(err => {
      console.error(err);
      alert('일기 목록을 불러오는 데 실패했습니다.');
    });
};

// 2. petName 조회
const fetchPetName = () => {
  axios.get(`/v1/pet/${petId}`)
    .then(res => {
      petName.value = res.data.petName || '반려동물';
    })
    .catch(err => {
      console.error(err);
      petName.value = '반려동물';
    });
};

// 3. 일기 삭제
const deleteDiary = (diaryId) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    axios.put(`/v1/diary/${diaryId}`)
      .then(() => {
        alert('삭제 완료');
        fetchDiaryList();
      })
      .catch(err => {
        console.error(err);
        alert('삭제 중 오류 발생');
      });
  }
};

const formatDate = (datetime) => {
  if (!datetime) return '날짜 없음';
  return new Date(datetime).toLocaleString();
};
const goToPetList = () => {
  router.push('/userPet');
};
onMounted(() => {
  fetchPetName();
  fetchDiaryList();
});
</script>
