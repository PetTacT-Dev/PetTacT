<template>
  <div class="container mt-4">
    <h2>반려동물 상세정보</h2>
    <div v-if="pet">
      <p><strong>이름:</strong> {{ pet.petName }}</p>
      <p><strong>성별:</strong> {{ pet.petGender }}</p>
      <p><strong>중성화 여부:</strong> {{ pet.isNeutered }}</p>
      <p><strong>RFID:</strong> {{ pet.rfidNo }}</p>
      <p><strong>품종:</strong> {{ pet.kindNm }}</p>
      <p><strong>몸무게:</strong> {{ pet.petWeight }}kg</p>
      <p><strong>생일:</strong> {{ pet.petBirth }}</p>
      <p><strong>등록일:</strong> {{ pet.createdAt }}</p>
      <img v-if="pet.petImageUrl" :src="pet.petImageUrl" class="img-thumbnail mt-3" style="max-width: 300px;">

      <div class="mt-4">
        <button class="btn btn-warning me-2" @click="goEdit">수정</button>
        <button class="btn btn-danger" @click="deletePet">삭제</button>
        <button class="btn btn-danger" @click="list">반려동물 목록</button>
        <button class="btn btn-danger" @click="diaryList">{{ pet.petName }}의 일기장</button>
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
