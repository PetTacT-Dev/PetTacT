<template>
  <div class="pet-register-container">
    <div class="pet-register-wrapper">
      <!-- 헤더 -->
      <div class="pet-register-header">
        <h1>보호소 등록</h1>
        <p class="register-subtitle">새로운 보호소 정보를 등록해주세요</p>
      </div>

      <!-- 등록 폼 -->
      <form @submit.prevent="submitForm" class="pet-register-form">
        <!-- 보호소명 -->
        <div class="field-group">
          <label class="field-label"
            >보호소명 <span class="required">*</span></label
          >
          <div class="input-wrapper">
            <input
              v-model="form.careNm"
              type="text"
              class="input-field"
              placeholder="보호소명을 입력해주세요"
              required
            />
          </div>
        </div>

        <!-- 보호소 코드 -->
        <div class="field-group">
          <label class="field-label"
            >보호소 코드 <span class="required">*</span></label
          >
          <div class="input-wrapper">
            <input
              v-model="form.careRegNo"
              type="text"
              class="input-field"
              placeholder="보호소 코드를 입력해주세요"
              required
            />
          </div>
        </div>

        <!-- 시도 / 시군구 선택 -->
        <div class="field-row">
          <div class="field-group">
            <label class="field-label">시도 <span class="required">*</span></label>
            <select v-model="selectedSido" @change="handleSidoChange" class="select-field" required>
              <option value="">시도 선택</option>
              <option v-for="s in sidoList" :key="s.orgCd" :value="s">{{ s.orgdownNm }}</option>
            </select>
          </div>
          <div class="field-group">
            <label class="field-label">시군구</label>
            <select v-model="selectedSigungu" @change="handleSigunguChange" class="select-field">
              <option value="">시군구 선택</option>
              <option v-for="g in sigunguList" :key="g.orgCd" :value="g">{{ g.orgdownNm }}</option>
            </select>
          </div>
        </div>

        <!-- 주소 검색 -->
        <div class="field-group">
          <label class="field-label"
            >도로명 주소 <span class="required">*</span></label
          >
          <div class="address-group">
            <input
              v-model="form.careAddr"
              type="text"
              class="input-field address-input"
              placeholder="주소 검색 버튼을 클릭해주세요"
              readonly
              required
            />
            <button type="button" class="address-btn" @click="openDaumPostcode">
              주소 검색
            </button>
          </div>
        </div>

        <!-- 전화번호 -->
        <div class="field-group">
          <label class="field-label"
            >전화번호 <span class="required">*</span></label
          >
          <div class="input-wrapper">
            <input
              v-model="form.careTel"
              type="text"
              class="input-field"
              placeholder="전화번호를 입력해주세요 (예: 02-1234-5678)"
              required
            />
          </div>
        </div>

        <!-- orgNm (자동입력) -->
        <input type="hidden" v-model="form.orgNm" />
        <!-- 위도/경도 (자동입력) -->
        <input type="hidden" v-model="form.lat" />
        <input type="hidden" v-model="form.lng" />

        <!-- 등록 버튼 -->
        <button type="submit" class="submit-btn">등록하기</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const form = ref({
  careNm: "",
  careRegNo: "",
  orgNm: "",
  careAddr: "",
  careTel: "",
  lat: "",
  lng: "",
});

const sidoList = ref([]);
const sigunguList = ref([]);
const selectedSido = ref("");
const selectedSigungu = ref("");

onMounted(() => {
  axios.get("/v1/pet/sido").then((res) => {
    sidoList.value = res.data.items;
  });
});

// 시도 선택 시 시군구 조회
const handleSidoChange = () => {
  selectedSigungu.value = "";
  sigunguList.value = [];
  form.value.orgNm = "";
  if (selectedSido.value) {
    axios
      .get("/v1/pet/sigungu", {
        params: { uprCd: selectedSido.value.orgCd },
      })
      .then((res) => {
        sigunguList.value = res.data.items;
      });
  }
};

// 시군구 선택 시 orgNm 설정
const handleSigunguChange = () => {
  if (selectedSido.value && selectedSigungu.value) {
    form.value.orgNm =
      selectedSido.value.orgdownNm + " " + selectedSigungu.value.orgdownNm;
  }
};

// 다음 주소 API + 카카오 좌표 변환
const openDaumPostcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      form.value.careAddr = data.roadAddress;
      const geocoder = new window.kakao.maps.services.Geocoder();
      geocoder.addressSearch(data.roadAddress, (result, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
          form.value.lat = result[0].y;
          form.value.lng = result[0].x;
        }
      });
    },
  }).open();
};
// 등록
const submitForm = () => {
  console.log("제출 데이터:", form.value); // 이 줄 추가

  axios
    .post("/v1/api/shelter", form.value)
    .then(() => {
      alert("등록 완료!");
      router.push("/shelter");
    })
    .catch((err) => {
      console.error("등록 실패", err);
      alert("등록 실패");
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
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 12px center;
  background-repeat: no-repeat;
  background-size: 16px;
  cursor: pointer;
}

.select-field:focus {
  outline: 1px solid #008be6;
  outline-offset: -1px;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.address-group {
  display: flex;
  gap: 10px;
}

.address-input {
  flex: 1;
}

.address-btn {
  width: 100px;
  height: 50px;
  background: #008be6;
  color: white;
  border: none;
  border-radius: 5px;
  font-family: "Pretendard", sans-serif;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.address-btn:hover {
  background: #007acc;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.submit-btn {
  width: 100%;
  height: 60px;
  background: #008be6;
  color: white;
  border: none;
  border-radius: 5px;
  font-family: "Pretendard", sans-serif;
  font-weight: 600;
  font-size: 18px;
  cursor: pointer;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  margin-top: 20px;
}

.submit-btn:hover {
  background: #007acc;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
  transform: translateY(-1px);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .field-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .address-group {
    flex-direction: column;
  }

  .address-btn {
    width: 100%;
  }
}
</style>