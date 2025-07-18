<template>
  <div class="container mt-4">
    <h2>반려동물 수정</h2>
    <form @submit.prevent="updatePet">
      <!-- 이름 -->
      <input v-model="pet.petName" class="form-control mb-2" placeholder="이름" required />

      <!-- 성별 -->
      <select v-model="pet.petGender" class="form-control mb-2" required>
        <option value="">성별 선택</option>
        <option value="M">수컷 (M)</option>
        <option value="F">암컷 (F)</option>
      </select>

      <!-- 중성화 여부 -->
      <select v-model="pet.isNeutered" class="form-control mb-2" required>
        <option value="">중성화 여부</option>
        <option value="Y">예 (Y)</option>
        <option value="N">아니오 (N)</option>
      </select>

      <!-- RFID -->
      <input v-model="pet.rfidNo" class="form-control mb-2" placeholder="RFID 번호 (선택)" />

      <!-- 몸무게 -->
      <input v-model="pet.petWeight" class="form-control mb-2" type="number" step="0.1" placeholder="몸무게" />

      <!-- 생일 -->
      <input v-model="pet.petBirth" class="form-control mb-2" type="date" />

      <!-- 이미지 URL -->
      <input v-model="pet.petImageUrl" class="form-control mb-2" placeholder="이미지 URL (선택)" />


      <!-- 수정 버튼 -->
      <button type="submit" class="btn btn-success">수정</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const pet = ref({});

onMounted(() => {
  axios.get(`/v1/pet/${route.params.petId}`).then((res) => {
    pet.value = res.data;
  });
});

const updatePet = () => {
  if (!validatePet()) return;

  axios.put(`/v1/pet/${route.params.petId}`, pet.value).then(() => {
    alert('수정 완료');
    router.push('/userPet');
  }).catch(err => {
    console.error(err);
    alert('수정 실패');
  });
};

const validatePet = () => {
  const name = pet.value.petName?.trim();
  const gender = pet.value.petGender;
  const neutered = pet.value.isNeutered;
  const weight = pet.value.petWeight;
  const birth = pet.value.petBirth;
  const imageUrl = pet.value.petImageUrl?.trim();
  const rfid = pet.value.rfidNo?.trim();

  if (!name || name.length < 2) {
    alert('이름은 2자 이상 입력해주세요.');
    return false;
  }
  if (!['M', 'F'].includes(gender)) {
    alert('성별을 정확히 선택해주세요.');
    return false;
  }
  if (!['Y', 'N'].includes(neutered)) {
    alert('중성화 여부를 선택해주세요.');
    return false;
  }
  if (!weight || weight <= 0) {
    alert('몸무게는 0보다 커야 합니다.');
    return false;
  }
  if (birth && new Date(birth) > new Date()) {
    alert('생일은 오늘보다 이전 날짜여야 합니다.');
    return false;
  }
  if (rfid && rfid.length > 30) {
    alert('RFID는 30자 이하로 입력해주세요.');
    return false;
  }
  return true;
};
</script>
