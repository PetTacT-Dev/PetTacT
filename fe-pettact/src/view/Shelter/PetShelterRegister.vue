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
          <label class="field-label">보호소명 <span class="required">*</span></label>
          <div class="input-wrapper">
            <input v-model="form.careNm" type="text" class="input-field" placeholder="보호소명을 입력해주세요" required />
          </div>
        </div>

        <!-- 보호소 코드 -->
        <div class="field-group">
          <label class="field-label">보호소 코드 <span class="required">*</span></label>
          <div class="input-wrapper">
            <input v-model="form.careRegNo" type="text" class="input-field" placeholder="보호소 코드를 입력해주세요" required />
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
          <label class="field-label">도로명 주소 <span class="required">*</span></label>
          <div class="address-group">
            <input
              v-model="form.careAddr"
              type="text"
              class="input-field address-input"
              placeholder="주소 검색 버튼을 클릭해주세요"
              readonly
              required
              @click="openDaumPostcode"
            />
            <button type="button" class="address-btn" @click="openDaumPostcode">주소 검색</button>
          </div>
        </div>

        <!-- 전화번호 -->
        <div class="field-group">
          <label class="field-label">전화번호 <span class="required">*</span></label>
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

        <!-- 숨김 필드 -->
        <input type="hidden" v-model="form.orgNm" />
        <input type="hidden" v-model="form.lat" />
        <input type="hidden" v-model="form.lng" />

        <!-- 등록 버튼 -->
        <button type="submit" class="submit-btn">등록하기</button>
      </form>
    </div>

    <!-- 다음 주소 레이어 -->
    <div id="daum-post-layer">
      <div class="daum-post-close">
        <button type="button" class="address-btn" @click="closeDaumPostcode">닫기</button>
      </div>
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
const isKakaoReady = ref(false);

onMounted(() => {
  axios.get("/v1/pet/sido").then((res) => {
    sidoList.value = res.data.items;
  });

  const kakaoScript = document.createElement("script");
  kakaoScript.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=becfe069cca65d679dc79f6ef0a6cee7&libraries=services";
  kakaoScript.async = true;
  kakaoScript.onload = () => { isKakaoReady.value = true };
  document.head.appendChild(kakaoScript);

  const postcodeScript = document.createElement("script");
  postcodeScript.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
  postcodeScript.async = true;
  document.head.appendChild(postcodeScript);
});

const handleSidoChange = () => {
  selectedSigungu.value = "";
  sigunguList.value = [];
  form.value.orgNm = "";

  if (selectedSido.value) {
    axios.get("/v1/pet/sigungu", {
      params: { uprCd: selectedSido.value.orgCd },
    }).then((res) => {
      sigunguList.value = res.data.items;
    });
  }
};

const handleSigunguChange = () => {
  if (selectedSido.value && selectedSigungu.value) {
    form.value.orgNm = selectedSido.value.orgdownNm + " " + selectedSigungu.value.orgdownNm;
  }
};

const openDaumPostcode = () => {
  if (!isKakaoReady.value) {
    alert("지도 로딩 중입니다. 잠시 후 다시 시도해주세요.");
    return;
  }

  const element_layer = document.getElementById("daum-post-layer");

  new window.daum.Postcode({
    oncomplete: function (data) {
      form.value.careAddr = data.roadAddress;
      element_layer.style.display = "none";

      const geocoder = new window.kakao.maps.services.Geocoder();
      geocoder.addressSearch(data.roadAddress, (result, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
          form.value.lat = result[0].y;
          form.value.lng = result[0].x;
        }
      });
    },
    width: "100%",
    height: "100%",
  }).embed(element_layer);

  element_layer.style.display = "block";
};

const closeDaumPostcode = () => {
  const element_layer = document.getElementById("daum-post-layer");
  element_layer.style.display = "none";
};

const submitForm = () => {
  axios.post("/v1/api/shelter", form.value)
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
/* 기존 Pretendard 스타일 + 다음 주소 레이어 */@import url('https://cdn.jsdelivr.net/npm/pretendard@1.3.6/dist/web/static/pretendard.css');

.pet-register-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: 'Pretendard', sans-serif;
}

.pet-register-wrapper {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
}

.pet-register-header {
  text-align: center;
  margin-bottom: 2rem;
}

.pet-register-header h1 {
  font-size: 1.8rem;
  font-weight: 700;
}

.register-subtitle {
  font-size: 1rem;
  color: #777;
}

.pet-register-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.field-group {
  display: flex;
  flex-direction: column;
}

.field-label {
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.required {
  color: red;
  margin-left: 0.3rem;
}

.input-wrapper,
.address-group {
  display: flex;
  gap: 0.5rem;
}

.input-field,
.select-field {
  flex: 1;
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
}

.select-field {
  background-color: #fff;
}

.address-input {
  cursor: pointer;
}

.address-btn {
  padding: 0.6rem 1rem;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.address-btn:hover {
  background-color: #e0e0e0;
}

.field-row {
  display: flex;
  gap: 1rem;
}

.submit-btn {
  padding: 0.8rem;
  font-size: 1.1rem;
  font-weight: bold;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #0069d9;
}

/* 다음 주소검색 레이어 */
#daum-post-layer {
  display: none;
  position: fixed;
  z-index: 1000;
  border: 1px solid #ccc;
  background: #fff;
  width: 100%;
  max-width: 500px;
  height: 600px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  border-radius: 10px;
  overflow: hidden;
}

.daum-post-close {
  text-align: right;
  padding: 10px;
  background-color: #f7f7f7;
  border-bottom: 1px solid #ddd;
}


/* 나머지 .pet-register-* 클래스들은 그대로 유지하면 되고, 필요 시 추가로 조정 가능 */
</style>
