<template>
  <div class="container mt-4">
    <h2>반려동물 등록</h2>
    <form @submit.prevent="registerPet">
      <!-- 축종 선택 -->
      <select v-model="selectedUpKindCd" @change="fetchKinds" class="form-control mb-2" required>
        <option value="">축종 선택</option>
        <option value="417000">개</option>
        <option value="422400">고양이</option>
        <option value="429900">기타</option>
      </select>

      <!-- 품종 선택 -->
      <select v-model="pet.kindCd" @change="handleKindChange" class="form-control mb-2" required>
        <option value="">품종 선택</option>
        <option v-for="k in kindList" :key="k.kindCd" :value="k.kindCd">
          {{ k.kindNm }}
        </option>
      </select>

      <!-- 나머지 입력 항목들 -->
      <input v-model="pet.petName" class="form-control mb-2" placeholder="이름" required />

      <select v-model="pet.petGender" class="form-control mb-2" required>
        <option value="">성별 선택</option>
        <option value="M">수컷 (M)</option>
        <option value="F">암컷 (F)</option>
      </select>

      <select v-model="pet.isNeutered" class="form-control mb-2" required>
        <option value="">중성화 여부</option>
        <option value="Y">예 (Y)</option>
        <option value="N">아니오 (N)</option>
      </select>

      <input v-model="pet.rfidNo" class="form-control mb-2" placeholder="RFID 번호" />
      <input v-model="pet.petWeight" class="form-control mb-2" type="number" step="0.1" placeholder="몸무게" />
      <input v-model="pet.petBirth" class="form-control mb-2" type="date" placeholder="생일" />
      <input v-model="pet.petImageUrl" class="form-control mb-2" placeholder="이미지 URL" />

      <button type="submit" class="btn btn-primary">등록</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const pet = ref({
  kindCd: '',
  kindNm: '',
  speciesCode: '',
  petName: '',
  petGender: '',
  isNeutered: '',
  rfidNo: '',
  petWeight: null,
  petBirth: '',
  petImageUrl: ''
});

const selectedUpKindCd = ref('');
const kindList = ref([]);

const fetchKinds = () => {
  pet.value.kindCd = '';
  pet.value.kindNm = '';
  pet.value.speciesCode = '';
  kindList.value = [];

  if (!selectedUpKindCd.value) return;

  axios.get('/v1/pet/kind', {
    params: { upKindCd: selectedUpKindCd.value }
  }).then(res => {
    kindList.value = res.data.items;
  });
};

const handleKindChange = () => {
  const selected = kindList.value.find(k => k.kindCd === pet.value.kindCd);
  if (selected) {
    pet.value.kindNm = selected.kindNm;
    pet.value.speciesCode = selectedUpKindCd.value;
  }
};

const registerPet = () => {
  axios.post('/v1/pet', pet.value).then(() => {
    alert('등록 성공');
    router.push('/userPet');
  });
};
</script>
