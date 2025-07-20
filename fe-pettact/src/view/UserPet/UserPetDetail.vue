<template>
  <div class="pet-register-container">
    <div class="pet-register-wrapper">
      <div class="pet-register-header">
        <h1>반려동물 정보</h1>
        <p class="register-subtitle" v-if="pet">{{ pet.petName }}의 등록 정보입니다</p>
        <p class="register-subtitle" v-else>정보를 불러오는 중입니다...</p>
      </div>

      <div v-if="pet" class="pet-detail-view">
        <div class="photo-preview" v-if="pet.petImageUrl">
          <img :src="pet.petImageUrl" alt="반려동물 사진" />
        </div>

        <div class="field-group">
          <label class="field-label">이름</label>
          <p class="field-value light">{{ pet.petName }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">성별</label>
          <p class="field-value light">{{ pet.petGender === 'M' ? '♂ 수컷' : '♀ 암컷' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">중성화 여부</label>
          <p class="field-value light">{{ pet.isNeutered === 'Y' ? '완료' : '미완료' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">품종</label>
          <p class="field-value light">{{ pet.kindNm }}</p>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label class="field-label">몸무게 (kg)</label>
            <p class="field-value light">{{ pet.petWeight }}kg</p>
          </div>
          <div class="field-group">
            <label class="field-label">생년월일</label>
            <p class="field-value light">{{ pet.petBirth }}</p>
          </div>
        </div>

        <div class="field-group">
          <label class="field-label">RFID 번호</label>
          <p class="field-value light">{{ pet.rfidNo || '없음' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">특이사항</label>
          <p class="field-value light">{{ pet.specialNotes || '없음' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">등록일</label>
          <p class="field-value light">{{ pet.createdAt }}</p>
        </div>

        <div class="button-group mt-4">
          <button class="register-button" @click="goEdit">수정</button>
          <button class="register-button" style="background: #e53e3e;" @click="deletePet">삭제</button>
          <button class="register-button" style="background: #666;" @click="list">반려동물 목록</button>
          <button class="register-button" style="background: #38a169;" @click="diaryList">
            {{ pet.petName }}의 일기장
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const pet = ref(null);
const route = useRoute();
const router = useRouter();

onMounted(() => {
  axios.get(`/v1/pet/${route.params.petId}`).then((res) => {
    pet.value = res.data;
  }).catch((err) => {
    console.error("상세 조회 실패:", err);
  });
});

const diaryList = () => {
  router.push(`/userPet/diary/${route.params.petId}`);
};

const goEdit = () => {
  router.push(`/userPet/update/${route.params.petId}`);
};

const list = () => {
  router.push('/userPet');
};

const deletePet = () => {
  if (confirm('정말 삭제하시겠습니까?')) {
    axios.delete(`/v1/pet/${route.params.petId}`).then(() => {
      alert('삭제 완료');
      router.push('/userPet');
    }).catch(err => {
      console.error('삭제 실패', err);
      alert('삭제 중 오류 발생');
    });
  }
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.pet-register-container {
  width: 90%;
  max-width: 600px;
  margin: 0 auto;
  background: white;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, sans-serif;
}

.pet-register-wrapper {
  width: 100%;
  max-width: 540px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.pet-register-header {
  text-align: center;
}

.pet-register-header h1 {
  font-family: "Inter", sans-serif;
  font-weight: 700;
  font-size: 24px;
  line-height: 33.6px;
  color: black;
  margin-bottom: 12px;
}

.register-subtitle {
  color: #666;
  font-size: 16px;
  line-height: 1.5;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.field-label {
  font-family: "Pretendard", sans-serif;
  font-weight: 500;
  font-size: 14px;
  color: #333;
  margin-left: 5px;
}

.field-value {
  font-size: 15px;
  padding: 12px 15px;
  border-radius: 5px;
}

.field-value.light {
  background-color: #fafafa;
  color: #444;
  border: 1px solid #eee;
}

.photo-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 12px;
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.register-button {
  width: 100%;
  height: 50px;
  background: #008be6;
  border: none;
  border-radius: 7px;
  color: white;
  font-family: "Pretendard", sans-serif;
  font-weight: 600;
  font-size: 18px;
  line-height: 28px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 10px;
}

.register-button:hover:not(:disabled) {
  background: #0066cc;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .pet-register-container {
    padding: 20px 15px;
  }

  .field-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .pet-register-header h1 {
    font-size: 20px;
  }
}
</style>
