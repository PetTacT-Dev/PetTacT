<template>
  <div class="edit-container">
    <div class="edit-wrapper">
      <div class="edit-header">
        <h1>회원 정보 수정</h1>
      </div>

      <form class="edit-form" @submit.prevent="handleUpdate">
        <!-- 이메일 -->
        <div class="input-group email-group">
          <label for="userEmail">이메일 <span class="text-danger">*</span></label>
          <input type="email" id="userEmail" class="input-field" :value="userEmail" readonly />
        </div>

        <!-- 닉네임 -->
        <div class="input-group nickname-group">
          <label for="userNickname">닉네임 <span class="text-danger">*</span></label>
          <div class="nickname-input-button">
            <input type="text" id="userNickname" v-model="form.userNickname" class="input-field" ref="nicknameInput" />
            <button type="button" class="nickname-verify-button" @click="checkNickname">
              중복 확인
            </button>
          </div>
          <div v-if="nicknameAvailable === true" class="nickname-status text-success">사용 가능</div>
          <div v-else-if="nicknameAvailable === false" class="nickname-status text-danger">이미 사용 중</div>
        </div>

        <!-- 전화번호 -->
        <div class="input-group">
          <label for="userTel">전화번호 <span class="text-danger">*</span></label>
          <input type="tel" id="userTel" v-model="form.userTel" class="input-field" />
        </div>

        <!-- 생년월일 -->
        <div class="input-group">
          <label for="userBirth">생년월일</label>
          <input type="date" id="userBirth" :value="userBirth" class="input-field" readonly />
        </div>

        <!-- 주소 -->
        <div class="input-group address-group">
          <label for="userStreet1">주소 <span class="text-danger">*</span></label>
          <div class="address-input-button">
            <button type="button" class="address-search-button" @click="openPostcode">
              우편번호 검색
            </button>
            <input type="text" id="userZipcode" v-model="form.userZipcode" class="input-field" readonly />
          </div>
        </div>

        <!-- 주소 -->
        <div class="input-group">
          <label for="userDetailAddress">주소</label>
          <input type="text" id="userStreet1" v-model="form.userStreet1" class="input-field" readonly />
        </div>
        <!-- 상세주소 -->
        <div class="input-group">
          <label for="userDetailAddress">상세주소</label>
          <input type="text" id="userDetailAddress" v-model="form.userDetailAddress" class="input-field" />
        </div>

        <div v-if="error" class="text-danger" style="font-size: 14px; margin-top: -12px;">
          {{ error }}
        </div>

        <!-- 버튼 -->
        <div class="action-buttons">
          <button type="submit" class="form-button btn-submit">저장</button>
          <button type="button" class="form-button btn-cancel" @click="router.push({ name: 'myInfo' })">취소</button>
        </div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  userNickname: '',
  userTel: '',
  userZipcode: '',
  userStreet1: '',
  userDetailAddress: ''
})

const initialForm = ref({})
const userEmail = ref('')
const userBirth = ref('')
const error = ref('')
const success = ref('')
const nicknameAvailable = ref(null)
const nicknameInput = ref(null)

onMounted(async () => {
  try {
    const res = await axios.get('/v1/user/mypage/myInfo')
    const data = res.data

    userEmail.value = data.userEmail
    userBirth.value = data.userBirth

    form.value.userNickname = data.userNickname
    form.value.userTel = data.userTel
    form.value.userZipcode = data.userZipcode
    form.value.userStreet1 = data.userStreet1
    form.value.userDetailAddress = data.userDetailAddress

    initialForm.value = { ...form.value }
  } catch (err) {
    console.error(err)
    error.value = '회원 정보를 불러오는데 실패했습니다.'
  }
})

// 닉네임 입력 감지 → 중복확인 상태 초기화
watch(() => form.value.userNickname, () => {
  nicknameAvailable.value = null
})

// 닉네임 중복 확인
const checkNickname = async () => {
  if (!form.value.userNickname) {
    alert('닉네임을 입력하세요.')
    return
  }

  try {
    const res = await axios.get(`/v1/user/nickname/check?nickname=${form.value.userNickname}`)
    nicknameAvailable.value = !res.data // 사용 가능하면 true
  } catch (err) {
    console.error(err)
    error.value = '닉네임 중복 확인 실패'
  }
}

// 우편번호 API
const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      form.value.userZipcode = data.zonecode
      form.value.userStreet1 = data.address
      document.getElementById("userDetailAddress").focus()
    }
  }).open()
}

// 저장 처리
const handleUpdate = async () => {
  error.value = ''
  success.value = ''

  const nicknameChanged = form.value.userNickname !== initialForm.value.userNickname

  // 닉네임을 수정했는데 중복 확인 안했을 경우
  if (nicknameChanged && nicknameAvailable.value !== true) {
    error.value = '닉네임 중복확인을 해주세요.'
    nicknameInput.value?.focus()
    return
  }

  try {
    await axios.patch('/v1/user/mypage/update', form.value)
    success.value = '회원 정보가 성공적으로 수정되었습니다.'
    router.push({ name: 'myInfo' })
  } catch (err) {
    console.error(err)
    error.value = err.response?.data || '수정 실패'
  }
}

</script>

<style scoped>
/* 여기에 첫 번째에서 제공한 스타일 그대로 사용 */
.edit-container {
  width: 100%;
  min-height: 100vh;
  max-width: 1173px;
  margin: 0 auto;
  padding: 40px;
  display: flex;
  justify-content: center;
}

.edit-wrapper {
  width: 100%;
  max-width: none;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.edit-header h1 {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: black;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 26px;
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
  flex: 7;
}

.nickname-input-button .nickname-verify-button {
  flex: 3;
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

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.form-button {
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

/* 저장 버튼 */
.btn-submit {
  background: #008BE6;
  color: white;
}

.btn-submit:hover {
  background: #0066CC;
}

/* 취소 버튼 */
.btn-cancel {
  background: #dc3545;
  color: white;
}

.btn-cancel:hover {
  background: #c82333;
}
</style>
