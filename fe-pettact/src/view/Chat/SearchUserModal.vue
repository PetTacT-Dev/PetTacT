<template>
  <div class="search-bar-container">
    <!-- 검색 입력창 -->
    <div class="search-input-wrapper">
      <span class="search-icon">🔍</span>
      <input 
        v-model="nickName" 
        placeholder="채팅방 이름, 참여자 검색" 
        @keyup.enter="search"
        @focus="showResults = true"
        class="search-input"
      />
      <button v-if="nickName" @click="clearSearch" class="clear-btn">×</button>
    </div>

    <!-- 검색 결과 드롭다운 -->
    <div v-if="showResults && (nickName || errorMessage || result)" class="search-results-dropdown">
      <!-- 에러 메시지 -->
      <div v-if="errorMessage" class="search-error">
        <span class="error-icon">⚠️</span>
        <span>{{ errorMessage }}</span>
      </div>

      <!-- 검색 결과 -->
      <div v-if="result" class="search-result-item" @click="requestChat">
        <div class="user-avatar">👤</div>
        <div class="user-info">
          <div class="user-name">{{ result.userName }}</div>
          <div class="user-details">{{ result.userBirth }}</div>
        </div>
        <div class="chat-action">
          <span class="action-text">채팅 신청</span>
          <span class="action-icon">💬</span>
        </div>
      </div>

      <!-- 검색 안내 -->
      <div v-if="!result && !errorMessage && nickName" class="search-guide">
        <div class="guide-content">
          <span class="guide-icon">💡</span>
          <span>Enter를 눌러 사용자를 검색하세요</span>
        </div>
      </div>

      <!-- 빈 상태 -->
      <div v-if="!nickName" class="search-empty">
        <div class="empty-content">
          <span class="empty-icon">🔍</span>
          <span>검색어를 입력해주세요</span>
        </div>
      </div>
    </div>

    <!-- 검색 결과 배경 오버레이 -->
    <div v-if="showResults" class="search-overlay" @click="hideResults"></div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios'

const emit = defineEmits(['roomOpen', 'searchFocus', 'searchBlur']); // 'close' 제거

const nickName = ref('');
const result = ref(null);
const errorMessage = ref('');
const showResults = ref(false);

function search() {
  errorMessage.value = ''; 
  if (!nickName.value.trim()) {
    errorMessage.value = '닉네임을 입력해주세요'; // alert 대신 에러 메시지
    return;
  }

  result.value = null; // 기존 결과 초기화

  axios.get(`/v1/chat/user/${nickName.value}`)
    .then(res => {
      result.value = res.data;
      showResults.value = true;
    })
    .catch(() => {
      errorMessage.value = '해당 회원을 찾을 수 없습니다.';
      showResults.value = true;
    });
}

function requestChat() {
  if (!result.value) return;

  axios.post(`/v1/chat/room/create/${result.value.userNo}`, null)
    .then(res => {
      emit('roomOpen', res.data.roomNo); // 채팅방 번호 전달
      clearSearch(); // 검색 초기화
    })
    .catch(() => {
      errorMessage.value = '채팅방 생성 중 오류가 발생했습니다.'; // alert 대신 에러 메시지
    });
}

function clearSearch() {
  nickName.value = '';
  result.value = null;
  errorMessage.value = '';
  showResults.value = false;
}

function hideResults() {
  showResults.value = false;
  emit('searchBlur');
}
</script>

<style scoped>
/* 검색바 컨테이너 */
.search-bar-container {
  position: relative;
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

/* 검색 입력창 */
.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #ffffff;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  padding: 10px 16px;
  transition: all 0.2s ease;
}

.search-input-wrapper:focus-within {
  border-color: #008BE6;
  box-shadow: 0 0 0 3px rgba(0, 139, 230, 0.1);
}

.search-icon {
  font-size: 16px;
  color: #6c757d;
  margin-right: 10px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  background: transparent;
  color: #2d3748;
}

.search-input::placeholder {
  color: #a0aec0;
}

.clear-btn {
  background: #e2e8f0;
  border: none;
  color: #6c757d;
  font-size: 16px;
  cursor: pointer;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 8px;
  transition: all 0.2s ease;
}

.clear-btn:hover {
  background: #cbd5e1;
}

/* 검색 결과 드롭다운 */
.search-results-dropdown {
  position: absolute;
  top: 100%;
  left: 16px;
  right: 16px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 300px;
  overflow-y: auto;
  margin-top: 4px;
}

/* 검색 오버레이 */
.search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: transparent;
}

/* 검색 에러 */
.search-error {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  color: #dc3545;
  background: #fee2e2;
  border-bottom: 1px solid #fca5a5;
}

.error-icon {
  font-size: 16px;
}

/* 검색 결과 아이템 */
.search-result-item {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f1f5f9;
}

.search-result-item:hover {
  background: #f8fafe;
}

.search-result-item:last-child {
  border-bottom: none;
}

.user-avatar {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #008BE6 0%, #B6E5FF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  margin-right: 12px;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  font-size: 15px;
}

.user-details {
  color: #6c757d;
  font-size: 13px;
}

.chat-action {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #008BE6;
  font-size: 13px;
  font-weight: 600;
}

.action-icon {
  font-size: 16px;
}

/* 검색 안내 */
.search-guide {
  padding: 20px 16px;
  text-align: center;
}

.guide-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #6c757d;
  font-size: 14px;
}

.guide-icon {
  font-size: 18px;
}

/* 빈 상태 */
.search-empty {
  padding: 20px 16px;
  text-align: center;
}

.empty-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #a0aec0;
  font-size: 14px;
}

.empty-icon {
  font-size: 18px;
}

/* 스크롤바 스타일링 */
.search-results-dropdown::-webkit-scrollbar {
  width: 6px;
}

.search-results-dropdown::-webkit-scrollbar-track {
  background: transparent;
}

.search-results-dropdown::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.search-results-dropdown::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .search-bar-container {
    padding: 10px 12px;
  }
  
  .search-results-dropdown {
    left: 12px;
    right: 12px;
  }
  
  .search-result-item {
    padding: 12px;
  }
  
  .user-avatar {
    width: 38px;
    height: 38px;
    font-size: 16px;
  }
}
</style>