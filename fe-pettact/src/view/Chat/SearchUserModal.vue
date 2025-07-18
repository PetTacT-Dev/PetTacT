<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-box">
      <h5>닉네임으로 사용자 검색</h5>
      <input v-model="nickName" placeholder="닉네임 입력" @keyup.enter="search" />
      <button @click="search">검색</button>
      <div v-if="errorMessage" style="color: red; margin-top: 10px;">
        {{ errorMessage }}
      </div>
      <div v-if="result">
        <p>이름: {{ result.userName }}</p>
        <p>생년월일: {{ (result.userBirth) }}</p>
        <button @click="requestChat">채팅 신청</button>
      </div>

      <button @click="emit('close')">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios'

const emit = defineEmits(['close', 'roomOpen']);

const nickName = ref('');
const result = ref(null);
const currentUserNo = 1;
const errorMessage = ref('');
function search() {
  errorMessage.value = ''; 
  if (!nickName.value.trim()) {
    alert('닉네임을 입력해주세요');
    return;
  }


  axios.get(`/v1/chat/user/${nickName.value}`)
    .then(res => {
      result.value = res.data;
    })
    .catch(() => {
      errorMessage.value = '해당 회원을 찾을 수 없습니다.';
    });
}

function requestChat() {
  axios.post(`/v1/chat/room/create/${result.value.userNo}`, null).then(res => {
    emit('roomOpen', res.data.roomNo); // 채팅방 번호 전달
    emit('close');
  }).catch(() => {
    alert('채팅방 생성 중 오류가 발생했습니다.');
  });
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-box {
  background-color: #fff;
  padding: 24px;
  width: 400px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.modal-box h4,
.modal-box h5 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.chat-room-item {
  padding: 10px;
  margin-bottom: 8px;
  background-color: #f5f5f5;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}
.chat-room-item:hover {
  background-color: #eaeaea;
}

.search-btn, .close-btn {
  margin-top: 12px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background-color: #4caf50;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
.search-btn:hover,
.close-btn:hover {
  background-color: #43a047;
}
</style>
