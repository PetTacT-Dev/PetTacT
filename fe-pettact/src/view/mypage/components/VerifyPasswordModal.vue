<template>
  <div class="modal fade show" style="display:block;" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">비밀번호 확인</h5>
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>
        <div class="modal-body">
          <p>보안을 위해 비밀번호를 다시 입력해주세요.</p>
          
          <input type="password" v-model="inputPassword" class="form-control mb-2" placeholder="비밀번호 입력" />
          
          <button class="btn btn-primary" @click="verifyPassword">확인</button>
          
          <p v-if="message" :class="isVerified ? 'text-success' : 'text-danger'">
            {{ message }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref } from 'vue';
import axios from 'axios';

const props = defineProps({
  onVerified: Function,
});

const emits = defineEmits(['close']);

const inputPassword = ref('');
const message = ref('');
const isVerified = ref(false);

const verifyPassword = async () => {
  try {
    const res = await axios.post('/v1/user/mypage/verify-password', {
      inputPassword: inputPassword.value,
    });
    message.value = res.data;
    isVerified.value = true;
    
    if (props.onVerified) props.onVerified();

    emits('close');
  } catch (err) {
    message.value = err.response?.data || '오류가 발생했습니다.';
    isVerified.value = false;
  }
};
</script>

<style scoped>
/* 모달 오버레이 */
.modal.fade.show {
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
}

/* 모달 다이얼로그 */
.modal-dialog {
  margin: 5% auto;
  max-width: 440px;
  transform: translateY(0);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 모달 컨텐츠 */
.modal-content {
  border: none;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 모달 헤더 */
.modal-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 24px 30px 20px;
  position: relative;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
  text-align: center;
  width: 100%;
}

/* 닫기 버튼 */
.btn-close {
  position: absolute;
  top: 20px;
  right: 24px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.05);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #666;
}

.btn-close::before {
  content: "×";
  font-weight: 300;
}

.btn-close:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
  transform: scale(1.05);
}

/* 모달 바디 */
.modal-body {
  padding: 30px;
  text-align: center;
}

.modal-body p {
  font-size: 16px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 24px;
}

/* 입력 필드 */
.form-control {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 400;
  color: #333;
  background: #fff;
  transition: all 0.3s ease;
  outline: none;
  margin-bottom: 20px;
}

.form-control:focus {
  border-color: #008be6;
  box-shadow: 0 0 0 3px rgba(0, 139, 230, 0.1);
  background: #f8fcff;
}

.form-control::placeholder {
  color: #999;
  font-weight: 300;
}

/* 확인 버튼 */
.btn.btn-primary {
  width: 100%;
  padding: 14px 24px;
  background: linear-gradient(135deg, #008be6 0%, #0066cc 100%);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.btn.btn-primary:hover {
  background: linear-gradient(135deg, #0066cc 0%, #004499 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 139, 230, 0.3);
}

.btn.btn-primary:active {
  transform: translateY(0);
}

/* 메시지 */
.modal-body p:last-child {
  margin-bottom: 0;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.text-success {
  background: #f0fff4;
  color: #38a169;
  border: 1px solid #9ae6b4;
}

.text-danger {
  background: #fff5f5;
  color: #e53e3e;
  border: 1px solid #fed7d7;
}

/* 반응형 */
@media (max-width: 576px) {
  .modal-dialog {
    margin: 10px;
    max-width: calc(100% - 20px);
  }
  
  .modal-header {
    padding: 20px 24px 16px;
  }
  
  .modal-title {
    font-size: 18px;
  }
  
  .modal-body {
    padding: 24px 20px;
  }
  
  .btn-close {
    top: 16px;
    right: 20px;
    width: 28px;
    height: 28px;
  }
}
</style>