<template>
  <div class="pet-register-container">
    <div class="pet-register-wrapper">
      <div class="pet-register-header">
        <h1>반려동물 등록하시고<br />다양한 혜택을 누려보세요</h1>
        <p class="register-subtitle">
          우리 가족의 소중한 반려동물 정보를 등록해주세요
        </p>
      </div>

      <form @submit.prevent="registerPet" class="pet-register-form">
        <!-- 반려동물 사진 (맨 위로 이동) -->
        <div class="field-group">
          <label class="field-label">반려동물 사진</label>
          <div class="photo-upload-section">
            <div class="photo-preview" v-if="pet.petImageUrl">
              <img :src="pet.petImageUrl" alt="반려동물 사진" />
              <button
                type="button"
                class="photo-remove"
                @click="removePetPhoto"
              >
                ×
              </button>
            </div>
            <div class="photo-upload-area" v-else>
              <div class="upload-placeholder">
                <div class="upload-icon">📷</div>
                <div class="upload-text">
                  <p class="upload-main-text">사진을 업로드해주세요</p>
                  <p class="upload-sub-text">
                    귀여운 우리 아이의 모습을 보여주세요
                  </p>
                </div>
                <button
                  type="button"
                  class="upload-button"
                  @click="$refs.fileInput.click()"
                >
                  파일 선택
                </button>
              </div>
            </div>
          </div>
          <div class="url-input-section">
            <input
              type="file"
              ref="fileInput"
              @change="handleFileUpload"
              accept="image/*"
              style="display: none"
            />
          </div>
        </div>

        <!-- 축종 선택 -->
        <div class="field-group">
          <label class="field-label"
            >축종 <span class="required">*</span></label
          >
          <div class="input-wrapper">
            <select
              v-model="selectedUpKindCd"
              @change="fetchKinds"
              class="input-field select-field"
              required
            >
              <option value="">축종을 선택해주세요</option>
              <option value="417000">🐕 개</option>
              <option value="422400">🐱 고양이</option>
              <option value="429900">🐾 기타</option>
            </select>
          </div>
        </div>

        <!-- 품종 선택 -->
        <div class="field-group">
          <label class="field-label"
            >품종 <span class="required">*</span></label
          >
          <div class="input-wrapper">
            <select
              v-model="pet.kindCd"
              @change="handleKindChange"
              class="input-field select-field"
              :disabled="!selectedUpKindCd"
              required
            >
              <option value="">품종을 선택해주세요</option>
              <option v-for="k in kindList" :key="k.kindCd" :value="k.kindCd">
                {{ k.kindNm }}
              </option>
            </select>
          </div>
          <div v-if="!selectedUpKindCd" class="field-help">
            먼저 축종을 선택해주세요
          </div>
        </div>

        <!-- 반려동물 이름 -->
        <div class="field-group">
          <label class="field-label"
            >이름 <span class="required">*</span></label
          >
          <div class="input-wrapper">
            <input
              v-model="pet.petName"
              type="text"
              class="input-field"
              placeholder="반려동물의 이름을 입력해주세요"
              required
            />
          </div>
        </div>

        <!-- 성별과 중성화 여부 (가로 배치) -->
        <div class="field-row">
          <div class="field-group">
            <label class="field-label"
              >성별 <span class="required">*</span></label
            >
            <div class="input-wrapper">
              <select
                v-model="pet.petGender"
                class="input-field select-field"
                required
              >
                <option value="">성별 선택</option>
                <option value="M">♂ 수컷</option>
                <option value="F">♀ 암컷</option>
              </select>
            </div>
          </div>

          <div class="field-group">
            <label class="field-label"
              >중성화 여부 <span class="required">*</span></label
            >
            <div class="input-wrapper">
              <select
                v-model="pet.isNeutered"
                class="input-field select-field"
                required
              >
                <option value="">중성화 여부</option>
                <option value="Y">완료</option>
                <option value="N">미완료</option>
              </select>
            </div>
          </div>
        </div>

        <!-- 생년월일과 몸무게 (가로 배치) -->
        <div class="field-row">
          <div class="field-group">
            <label class="field-label">생년월일</label>
            <div class="input-wrapper">
              <input v-model="pet.petBirth" type="date" class="input-field" />
            </div>
          </div>

          <div class="field-group">
            <label class="field-label">몸무게 (kg)</label>
            <div class="input-wrapper">
              <input
                v-model="pet.petWeight"
                type="number"
                step="0.1"
                class="input-field"
                placeholder="예: 5.2"
              />
            </div>
          </div>
        </div>

        <!-- RFID 번호 -->
        <div class="field-group">
          <label class="field-label">RFID 번호</label>
          <div class="input-wrapper">
            <input
              v-model="pet.rfidNo"
              type="text"
              class="input-field"
              placeholder="RFID 번호를 입력해주세요 (선택사항)"
            />
          </div>
          <div class="field-help">
            병원에서 받은 RFID 번호가 있다면 입력해주세요
          </div>
        </div>

        <!-- 특이사항 -->
        <div class="field-group">
          <label class="field-label">특이사항</label>
          <div class="input-wrapper">
            <textarea
              v-model="pet.specialNotes"
              class="input-field textarea-field"
              rows="4"
              placeholder="알레르기, 질병, 특별히 주의할 점 등을 적어주세요"
            ></textarea>
          </div>
        </div>

        <!-- 등록 버튼 -->
      <!-- 기존 -->
<!-- <button :disabled="!isFormValid" class="register-button"> -->

<!-- 수정 후 -->
<button class="register-button">
  등록하기
</button>

        <!-- 에러 메시지 -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <!-- 성공 메시지 -->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const pet = ref({
  kindCd: "",
  kindNm: "",
  petName: "",
  petGender: "",
  isNeutered: "",
  rfidNo: "",
  petWeight: null,
  petBirth: "",
  specialNotes: ""
  // petImageUrl: ''
});

const isFormValid = computed(() => {
  return (
    pet.value.kindCd &&
    pet.value.petName &&
    pet.value.petGender &&
    pet.value.petBirth
  );
});

const selectedUpKindCd = ref("");
const kindList = ref([]);

const fetchKinds = () => {
  pet.value.kindCd = "";
  pet.value.kindNm = "";
  kindList.value = [];

  if (!selectedUpKindCd.value) return;

  axios
    .get("/v1/pet/kind", {
      params: { upKindCd: selectedUpKindCd.value },
    })
    .then((res) => {
      kindList.value = res.data.items;
    });
};

const handleKindChange = () => {
  const selected = kindList.value.find((k) => k.kindCd === pet.value.kindCd);
  if (selected) {
    pet.value.kindNm = selected.kindNm;
  }
};

// const registerPet = () => {
//   axios.post("/v1/pet", pet.value).then(() => {
//     alert("등록 성공");
//     router.push("/userPet");
//   });
// };

console.log("🐶 등록 요청 데이터:", pet.value);
console.log(JSON.stringify(pet.value, null, 2))

const registerPet = () => {
  axios.post('/v1/pet', pet.value)
    .then(() => {
      alert('등록 성공');
      router.push('/userPet');
    })
    .catch((err) => {
      console.error('등록 실패:', err);
      alert('등록 실패 - 목록으로 이동합니다');
      router.push('/userPet');  // 실패했지만 이동
    });
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

.pet-register-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
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

.required {
  color: #ee0000;
}

.input-wrapper {
  position: relative;
}

.input-field {
  width: 100%;
  height: 50px;
  padding: 6px 15px;
  background: white;
  border: none;
  border-radius: 5px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  font-family: "Pretendard", sans-serif;
  font-weight: 300;
  font-size: 15px;
  line-height: 21px;
  color: black;
  transition: all 0.2s ease;
}

.input-field:focus {
  outline: 1px solid #008be6;
  outline-offset: -1px;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.input-field:disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.input-field::placeholder {
  color: #999;
}

.select-field {
  cursor: pointer;
}

.textarea-field {
  height: auto;
  min-height: 100px;
  resize: vertical;
  padding: 15px;
  line-height: 1.5;
}

.field-help {
  font-size: 12px;
  color: #666;
  margin-left: 5px;
  margin-top: 4px;
}

.photo-upload-section {
  margin-bottom: 12px;
}

.photo-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-remove {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.photo-upload {
  width: 120px;
  height: 120px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-placeholder {
  text-align: center;
  color: #666;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-placeholder p {
  font-size: 12px;
  margin-bottom: 8px;
}

.upload-button {
  background: #008be6;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 11px;
  cursor: pointer;
}

.mt-2 {
  margin-top: 8px;
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

.register-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error-message {
  background: #fff5f5;
  color: #e53e3e;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid #fed7d7;
  font-size: 14px;
  text-align: center;
}

.success-message {
  background: #f0fff4;
  color: #38a169;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid #9ae6b4;
  font-size: 14px;
  text-align: center;
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