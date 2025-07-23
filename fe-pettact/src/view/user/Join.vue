<template>
  <div class="signup-container">
    <div class="signup-wrapper">
      <div class="signup-header">
        <h1>회원가입</h1>
      </div>
      <form class="signup-form" @submit.prevent="handleJoin">
        <!-- 이메일 -->
        <div class="input-group email-group">
          <label for="userEmail"
            >이메일 <span class="text-danger">*</span></label
          >
          <div class="email-input-button">
            <input
              type="email"
              v-model="userEmail"
              id="userEmail"
              class="input-field"
              placeholder="이메일을 입력해주세요"
              required
            />

            <button
              v-if="!isEmailChecked"
              type="button"
              class="verify-button"
              @click="checkEmailDuplication"
            >
              중복 확인
            </button>

            <button
              v-else-if="!emailSent"
              type="button"
              class="verify-button"
              @click="sendVerificationEmail"
            >
              인증메일 발송
            </button>

            <button
              v-else-if="!emailVerified"
              type="button"
              class="verify-button"
              @click="refreshEmailStatus"
            >
              인증완료 확인
            </button>
          </div>

          <div class="email-status">
            <span v-if="!isEmailChecked" class="text-muted"
              >이메일 중복 여부를 확인해주세요.</span
            >
            <span v-else-if="!emailSent" class="text-success"
              >중복확인이 완료되었습니다. 인증메일을 발송해주세요.</span
            >
            <span v-else-if="!emailVerified" class="text-warning"
              >인증메일을 발송했습니다. 메일함에서 인증 후 '인증완료 확인'을
              눌러주세요.</span
            >
            <span v-else class="text-success"
              >✅ 이메일 인증이 완료되었습니다.</span
            >
          </div>
        </div>

        <!-- 비밀번호 -->
        <div class="input-group">
          <label for="userPassword"
            >비밀번호 <span class="text-danger">*</span></label
          >
          <input
            type="password"
            id="userPassword"
            v-model="userPassword"
            class="input-field"
            placeholder="비밀번호를 입력해주세요"
            required
          />
        </div>

        <!-- 이름 -->
        <div class="input-group">
          <label for="userName">이름 <span class="text-danger">*</span></label>
          <input
            type="text"
            id="userName"
            v-model="userName"
            class="input-field"
            required
          />
        </div>

        <!-- 닉네임 -->
        <div class="input-group">
          <label for="userNickname"
            >닉네임 <span class="text-danger">*</span></label
          >
          <div class="nickname-input-button">
            <input
              type="text"
              id="userNickname"
              v-model="userNickname"
              class="input-field"
              placeholder="닉네임을 입력해주세요"
              required
            />
            <button
              type="button"
              class="nickname-verify-button"
              @click="checkNicknameDuplication"
            >
              중복 확인
            </button>
          </div>
        </div>

        <!-- 휴대폰 번호 -->
        <div class="input-group">
          <label for="userTel"
            >휴대폰 번호 <span class="text-danger">*</span></label
          >
          <input
            type="tel"
            id="userTel"
            v-model="userTel"
            class="input-field"
            placeholder="숫자만 입력해주세요"
            required
          />
        </div>

        <!-- 생년월일 -->
        <div class="input-group">
          <label for="userBirth"
            >생년월일 <span class="text-danger">*</span></label
          >
          <input
            type="date"
            id="userBirth"
            v-model="userBirth"
            class="input-field"
            required
          />
        </div>

        <div class="address-group">
          <label for="userStreet1"
            >주소 <span class="text-danger">*</span></label
          >
          <div class="address-input-button">
            <input
              type="text"
              id="userStreet1"
              v-model="userStreet1"
              class="input-field"
              placeholder="주소를 입력해주세요"
              readonly
            />

            <button
              type="button"
              @click="openPostcode"
              class="address-search-button"
            >
              주소 검색
            </button>
          </div>
        </div>

        <!-- 상세주소 -->
        <div class="input-group">
          <label for="userDetailAddress"
            >상세주소 <span class="text-danger">*</span></label
          >
          <input
            type="text"
            id="userDetailAddress"
            v-model="userDetailAddress"
            class="input-field"
            placeholder="상세주소를 입력해주세요"
          />
        </div>

        <!-- 반려동물 여부 -->
        <div class="input-group">
          <label>
            <input
              type="checkbox"
              v-model="userHasPet"
              class="form-check-input"
            />
            반려동물이 있습니다.
          </label>
        </div>

        <!-- 이메일 수신 여부 -->
        <div class="input-group">
          <label>
            <input
              type="checkbox"
              v-model="userEmailReceiveAgree"
              class="form-check-input"
            />
            이메일 수신에 동의합니다.
          </label>
        </div>

        <!-- 약관동의 -->
        <div class="agreement-section">
          <label class="section-title"
            >약관동의 <span class="text-danger">*</span></label
          >

          <!-- 전체 동의 -->
          <div class="checkbox-wrapper">
            <input
              type="checkbox"
              id="allAgree"
              v-model="allAgree"
              @change="toggleAll"
            />
            <label for="allAgree" class="checkbox-label bold"
              >전체 동의합니다.</label
            >
          </div>

          <!-- 이용약관 동의 -->
          <div class="checkbox-wrapper">
            <input type="checkbox" id="agreeTerms" v-model="agreeTerms" />
            <label for="agreeTerms" class="checkbox-label"
              >이용약관 동의 (필수)</label
            >
          </div>
          <div class="description-box">
            본 서비스는 연습용으로 제공되며, 회원 정보는 학습 목적으로만
            사용됩니다.
          </div>

          <!-- 개인정보 처리방침 동의 -->
          <div class="checkbox-wrapper">
            <input
              type="checkbox"
              id="agreePrivacy"
              v-model="userEmailReceiveAgree"
            />
            <label for="agreePrivacy" class="checkbox-label"
              >개인정보 처리방침 동의 (필수)</label
            >
          </div>
          <div class="description-box">
            수집된 정보는 연습용 DB에 저장되며, 제3자에게 제공되지 않습니다.
          </div>

          <!-- 마케팅 정보 수신 동의 -->
          <div class="checkbox-wrapper">
            <input
              type="checkbox"
              id="agreeMarketing"
              v-model="agreeMarketing"
            />
            <label for="agreeMarketing" class="checkbox-label"
              >마케팅 정보 수신 동의 (선택)</label
            >
          </div>
          <div class="description-box">
            이벤트, 공지사항, 프로모션 정보를 이메일로 수신합니다.
          </div>
        </div>

        <!-- 회원가입 버튼 -->
        <button type="submit" class="signup-button">회원가입</button>

        <!-- 에러 메시지 -->
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </form>
    </div>
  </div>
</template>


<script setup>
import { ref, watch } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const userEmail = ref("");
const userPassword = ref("");
const userName = ref("");
const userNickname = ref("");
const userTel = ref("");
const userBirth = ref("");
const userZipcode = ref("");
const userStreet1 = ref("");
const userDetailAddress = ref("");
const userHasPet = ref(false);

const isEmailChecked = ref(false);
const emailVerified = ref(false);
const emailSent = ref(false);
const isNicknameChecked = ref(false);

const allAgree = ref(false);
const agreeTerms = ref(false);
const agreePrivacy = ref(false);
const userEmailReceiveAgree = ref(false); // 이메일 수신여부

const errorMessage = ref("");

// 이메일 변경 시 모든 상태 초기화
watch(userEmail, () => {
  isEmailChecked.value = false;
  emailVerified.value = false;
  emailSent.value = false;
});

// 닉네임 변경 시 중복확인 초기화
watch(userNickname, () => {
  isNicknameChecked.value = false;
});

const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// 이메일 중복확인
const checkEmailDuplication = async () => {
  if (!isValidEmail(userEmail.value)) {
    alert("올바른 이메일 형식을 입력해주세요.");
    return;
  }
  try {
    const response = await axios.get(
      `/v1/user/email/check?email=${userEmail.value}`
    );
    if (response.data) {
      alert("이미 사용 중인 이메일입니다.");
      isEmailChecked.value = false;
    } else {
      alert("사용 가능한 이메일입니다.");
      isEmailChecked.value = true;
    }
  } catch (error) {
    console.error(error);
    alert("이메일 중복 확인 실패");
    isEmailChecked.value = false;
  }
};

// 이메일 인증 메일 발송
const sendVerificationEmail = async () => {
  if (!isValidEmail(userEmail.value)) {
    alert("올바른 이메일 형식을 입력해주세요.");
    return;
  }
  if (!isEmailChecked.value) {
    alert("중복확인을 먼저 진행해주세요.");
    return;
  }
  try {
    await axios.post("/v1/user/email/send", null, {
      params: { userEmail: userEmail.value },
    });
    alert("인증 메일을 발송했습니다.");
    emailSent.value = true;
  } catch (error) {
    console.error(error);
    alert("인증 메일 발송 실패");
  }
};

// 이메일 인증 상태 갱신
const refreshEmailStatus = async () => {
  try {
    const response = await axios.get(
      `/v1/user/email/verified?email=${userEmail.value}`
    );
    emailVerified.value = response.data;

    if (emailVerified.value) {
      alert("이메일 인증이 확인되었습니다!");
    } else {
      alert("아직 인증이 완료되지 않았습니다. 메일함을 다시 확인해주세요.");
    }
  } catch (error) {
    console.error(error);
    alert("인증 상태 확인 실패");
  }
};

// 닉네임 중복확인
const checkNicknameDuplication = async () => {
  try {
    const response = await axios.get(
      `/v1/user/nickname/check?nickname=${userNickname.value}`
    );
    if (response.data) {
      alert("이미 사용 중인 닉네임입니다.");
      isNicknameChecked.value = false;
    } else {
      alert("사용 가능한 닉네임입니다.");
      isNicknameChecked.value = true;
    }
  } catch (error) {
    console.error(error);
    alert("닉네임 중복 확인 실패");
    isNicknameChecked.value = false;
  }
};

// 우편번호 api
const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      userZipcode.value = data.zonecode;
      userStreet1.value = data.address;
      document.getElementById("userDetailAddress").focus();
    },
  }).open();
};

// 회원가입 핸들러
const handleJoin = async () => {
  errorMessage.value = "";

  // 입력값 검증
  if (!isEmailChecked.value) {
    errorMessage.value = "이메일 중복확인을 해주세요.";
    return;
  }

  if (!emailVerified.value) {
    errorMessage.value = "이메일 인증을 완료해주세요.";
    return;
  }

  if (!isNicknameChecked.value) {
    errorMessage.value = "닉네임 중복확인을 해주세요.";
    return;
  }

  // 비밀번호 정규식 검증
  const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;
  if (!pwRegex.test(userPassword.value)) {
    errorMessage.value =
      "비밀번호는 영문자, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.";
    return;
  }

  try {
    await axios.post("/v1/user/join", {
      userEmail: userEmail.value,
      userPassword: userPassword.value,
      userName: userName.value,
      userNickname: userNickname.value,
      userTel: userTel.value,
      userBirth: userBirth.value,
      userZipcode: userZipcode.value,
      userStreet1: userStreet1.value,
      userDetailAddress: userDetailAddress.value,
      userHasPet: userHasPet.value,
      userEmailChecked: userEmailReceiveAgree.value,
    });
    alert("회원가입이 완료되었습니다.");

    router.push({ name: "login" });
  } catch (error) {
    console.error("회원가입 실패:", error);
    errorMessage.value =
      error.response?.data || "회원가입 실패. 입력값을 확인하세요.";
  }
};

// 전체 동의 토글
const toggleAll = () => {
  agreeTerms.value = allAgree.value;
  agreePrivacy.value = allAgree.value;
  userEmailReceiveAgree.value = allAgree.value;
};

// 개별 동의 변경 시 전체 동의 체크 여부 갱신
watch(
  [agreeTerms, agreePrivacy, userEmailReceiveAgree],
  ([terms, privacy, marketing]) => {
    if (terms && privacy && marketing) {
      allAgree.value = true;
    } else {
      allAgree.value = false;
    }
  }
);
</script>

<style scoped>
.signup-container {
  width: 90%;
  max-width: 1173px;
  margin: 0 auto;
  padding: 70px 317px;
  background: white;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.signup-wrapper {
  width: 100%;
  max-width: 539px;
  display: flex;
  flex-direction: column;
  gap: 39px;
}
.signup-form-section {
  display: flex;
  flex-direction: column;
  gap: 39px;
}
.signup-header h1 {
  font-family: "Inter", sans-serif;
  font-weight: 700;
  font-size: 24px;
  line-height: 33.6px;
  color: black;
  text-align: center;
  margin: 0;
}
.input-group.email-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.email-input-button {
  display: flex;
  gap: 10px;
  align-items: center;
}

.email-input-button .input-field {
  flex: 7; /* 70% */
}

.email-input-button .verify-button {
  flex: 3; /* 30% */
  height: 50px;
  border: none;
  border-radius: 5px;
  background: #008be6;
  color: white;
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
  transition: background-color 0.2s;
}

.email-input-button .verify-button:hover {
  background: #0066cc;
}

.email-status {
  font-size: 13px;
}

.nickname-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nickname-input-button {
  display: flex;
  gap: 10px;
  align-items: center;
}

.nickname-input-button .input-field {
  flex: 7; /* 70% */
}

.nickname-input-button .nickname-verify-button {
  flex: 3; /* 30% */
  height: 50px;
  border: none;
  border-radius: 5px;
  background: #008be6;
  color: white;
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
  transition: background-color 0.2s;
}

.nickname-input-button .nickname-verify-button:hover {
  background: #0066cc;
}

.nickname-status {
  font-size: 13px;
  padding-left: 4px;
}

.address-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 20px;
}

.address-input-button {
  display: flex;
  gap: 10px;
  align-items: center;
}

.address-input-button .input-field {
  flex: 7;
}

.address-search-button {
  flex: 3;
  height: 50px;
  padding: 0 12px;
  background-color: #008be6;
  color: white;
  font-weight: 500;
  font-size: 14px;
  border: none;
  border-radius: 5px;
  white-space: nowrap;
  cursor: pointer;
  transition: background-color 0.2s;
}

.address-search-button:hover {
  background-color: #0066cc;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 26px;
}
.input-group {
  display: flex;
  flex-direction: column;
  gap: 25px;
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
  box-sizing: border-box;
}
.input-field:focus,
.input-field.focused {
  outline: 1px solid #008be6;
  outline-offset: -1px;
  box-shadow: 4px 4px 4px rgba(0, 138.76, 230.43, 0.25);
}

.agreement-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  background: #f7f7fb;
  border-radius: 12px;
  border: 1px solid #ddd;
}

.section-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 4px;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-label {
  font-size: 14px;
  color: #333;
}

.checkbox-label.bold {
  font-weight: 600;
}

.description-box {
  background: #f1f1f1;
  padding: 12px;
  font-size: 13px;
  color: #666;
  border-radius: 8px;
}

.error-message {
  color: #ee0000;
  font-family: "Pretendard", sans-serif;
  font-weight: 300;
  font-size: 13px;
  line-height: 18.2px;
  text-align: center;
  padding: 5px 0;
}
.signup-button {
  width: 340px;
  height: 50px;
  background: #008be6;
  border: none;
  border-radius: 7px;
  color: white;
  font-family: "Pretendard", sans-serif;
  font-weight: 600;
  font-size: 20px;
  line-height: 28px;
  cursor: pointer;
  align-self: center;
  transition: background-color 0.2s;
}
.signup-button:hover {
  background: #0066cc;
}
</style>



