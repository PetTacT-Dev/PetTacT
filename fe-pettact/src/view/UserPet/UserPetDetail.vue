<template>
  <div class="pet-register-container">
    <div class="pet-register-wrapper">
      <div class="pet-register-header">
        <h1>반려동물 정보</h1>
        <p class="register-subtitle">{{ pet.petName }}의 등록 정보입니다</p>
      </div>

      <div v-if="pet" class="pet-detail-view">
        <!-- 반려동물 이미지 -->
        <div class="photo-preview" v-if="pet.petImageUrl">
          <img :src="pet.petImageUrl" alt="반려동물 사진" />
        </div>

        <!-- 각 필드 표시 -->
        <div class="field-group">
          <label class="field-label">이름</label>
          <p class="field-value">{{ pet.petName }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">성별</label>
          <p class="field-value">{{ pet.petGender === 'M' ? '수컷' : '암컷' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">중성화 여부</label>
          <p class="field-value">{{ pet.isNeutered === 'Y' ? '완료' : '미완료' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">품종</label>
          <p class="field-value">{{ pet.kindNm }}</p>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label class="field-label">몸무게</label>
            <p class="field-value">{{ pet.petWeight }}kg</p>
          </div>
          <div class="field-group">
            <label class="field-label">생년월일</label>
            <p class="field-value">{{ pet.petBirth }}</p>
          </div>
        </div>

        <div class="field-group">
          <label class="field-label">RFID 번호</label>
          <p class="field-value">{{ pet.rfidNo || '없음' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">특이사항</label>
          <p class="field-value">{{ pet.specialNotes || '없음' }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">등록일</label>
          <p class="field-value">{{ pet.createdAt }}</p>
        </div>

        <!-- 버튼 그룹 -->
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
아니 
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
/* 기본 초기화 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Pretendard', sans-serif;
  background-color: #f8f9fa;
  color: #212529;
  line-height: 1.6;
}

/* 전체 컨테이너 */
.pet-register-container {
  max-width: 720px;
  margin: 40px auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

/* 제목 */
.pet-register-header {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 32px;
  color: #333;
}

/* 필드 그룹 */
.field-group {
  margin-bottom: 20px;
}

.field-label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
  color: #444;
}

.input-field,
.select-field,
.textarea-field {
  width: 100%;
  padding: 12px 14px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  transition: border-color 0.2s;
}

.input-field:focus,
.select-field:focus,
.textarea-field:focus {
  border-color: #007bff;
  outline: none;
}

.textarea-field {
  resize: vertical;
  min-height: 100px;
}

/* 라디오 그룹 */
.radio-group {
  display: flex;
  gap: 12px;
}

/* 필드 열 병렬 */
.field-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.field-row > .field-group {
  flex: 1;
}

/* 사진 미리보기 */
.photo-preview {
  margin-top: 20px;
  text-align: center;
}

.photo-preview img {
  max-width: 200px;
  border-radius: 10px;
  border: 1px solid #ddd;
}

/* 버튼 */
.register-button,
.detail-action-button {
  display: inline-block;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  background-color: #007bff;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.register-button:hover,
.detail-action-button:hover {
  background-color: #0056b3;
}

/* 버튼 조합 스타일 */
.detail-edit {
  background-color: #ffc107;
}

.detail-delete {
  background-color: #dc3545;
}

.detail-list {
  background-color: #6c757d;
}

.detail-diary {
  background-color: #17a2b8;
}

.detail-action-button + .detail-action-button {
  margin-left: 10px;
}

/* 메시지 */
.message {
  margin-top: 16px;
  font-size: 14px;
}

.success-message {
  color: #28a745;
}

.error-message {
  color: #dc3545;
}

/* 상세 데이터용 */
.pet-detail-label {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.pet-detail-value {
  font-size: 15px;
  color: #555;
  margin-bottom: 16px;
}

</style>