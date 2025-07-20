<template>
  <div class="chat-room">
    <!-- 채팅방 헤더 -->
    <div class="chat-header">
      <div class="chat-partner-info">
        <div class="partner-avatar"> 
        </div>
        <div class="partner-details">
          <h4 class="partner-name">채팅 상대방</h4>
          <span class="member-count">2</span>
        </div>
      </div>
      <div class="header-actions">
        <button class="action-btn search-btn">🔍</button>
        <button class="action-btn menu-btn">☰</button>
      </div>
    </div>

    <!-- 메시지 영역 -->
    <div ref="chatBox" class="chat-messages">
      <!-- 날짜 구분선 -->
      <div class="date-divider">
        <span>2024년 10월 20일</span>
      </div>

      <div
        v-for="(msg, index) in modalStore.messages"
        :key="index"
        :class="['chat-message', msg.isMine ? 'me' : 'other']"
      >
        <!-- 상대방 메시지 -->
        <div v-if="!msg.isMine" class="message-group">
          <div class="sender-avatar">
          </div>
          <div class="message-content">
            <span class="sender-name">{{ msg.senderNickname }}</span>
            <div class="message-row">
              <div class="bubble other-bubble">{{ msg.message }}</div>
              <div class="message-time">{{ formatMessageTime(msg.createdAt) }}</div>
            </div>
          </div>
        </div>

        <!-- 내 메시지 -->
        <div v-else class="message-group my-message">
          <div class="message-content">
            <div class="message-row">
              <div class="message-time">{{ formatMessageTime(msg.createdAt) }}</div>
              <div class="bubble my-bubble">{{ msg.message }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 입력 영역 -->
    <div class="chat-input-container">
      <div class="input-wrapper">
        <button class="attachment-btn">📎</button>
        <input
          v-model="message"
          @keyup.enter="sendMessage"
          type="text"
          placeholder="메시지를 입력하세요"
          class="message-input"
        />
        <button @click="sendMessage" class="send-btn" :disabled="!message.trim()">
          전송
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import { useModalStore } from '@/js/modalStore';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';

const props = defineProps({
  roomNo: {
    type: Number,
    required: true
  }
});

const modalStore = useModalStore();
const message = ref('');
const chatBox = ref(null);
let stompClient = null;

// JWT 디코드
function getUserInfoFromToken() {
  const token = localStorage.getItem('accessToken');
  return token ? jwtDecode(token) : null;
}

// 메시지 불러오기 + 읽음 처리
async function fetchMessages(roomNo) {
  try {
    const res = await axios.get(`/v1/chat/message/${roomNo}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    });

    const userNo = getUserInfoFromToken()?.userNo;
    modalStore.messages = res.data.map(msg => ({
      ...msg,
      isMine: msg.senderUserNo === userNo
    }));

    const lastId = modalStore.messages.at(-1)?.messageId;
    if (lastId) {
      await axios.post('/v1/chat/read', {
        roomNo,
        lastMessageId: lastId
      }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
    }

  } catch (err) {
    console.error('메시지 불러오기 실패:', err);
  }
}

// 웹소켓 연결
function connect(roomNo) {
  const token = localStorage.getItem('accessToken');
  const userNo = getUserInfoFromToken()?.userNo;

  const socket = new SockJS('/ws-stomp');
  stompClient = new Client({
    webSocketFactory: () => socket,
    connectHeaders: {
      Authorization: `Bearer ${token}`
    },
    reconnectDelay: 5000,
    onConnect: () => {
      console.log('✅ STOMP 연결 성공');
      modalStore.connected = true;
      modalStore.setClient(stompClient);

      stompClient.subscribe(`/topic/chat/room/${roomNo}`, async (msg) => {
        const data = JSON.parse(msg.body);
        data.isMine = data.senderUserNo === userNo;

        modalStore.addMessage(data);

        // ✅ 수신한 메시지가 내 것이 아니라면 즉시 읽음 처리
        if (!data.isMine && data.messageId) {
          try {
            await axios.post('/v1/chat/read', {
              roomNo,
              lastMessageId: data.messageId
            }, {
              headers: {
                Authorization: `Bearer ${token}`
              }
            });
          } catch (err) {
            console.error('읽음 처리 실패:', err);
          }
        }
      });
    },
    onStompError: (frame) => {
      console.error('❌ STOMP 에러:', frame);
    },
    onWebSocketError: (event) => {
      console.error('❌ WebSocket 연결 실패:', event);
    }
  });

  stompClient.activate();
}

// 메시지 전송
function sendMessage() {
  if (!modalStore.connected || !message.value.trim()) return;

  stompClient?.publish({
    destination: '/app/chat/message',
    body: JSON.stringify({
      roomNo: modalStore.roomNo,
      message: message.value
    }),
    headers: {
      Authorization: `Bearer ${localStorage.getItem('accessToken')}`
    }
  });

  message.value = '';
}

// 메시지 시간 포맷
function formatMessageTime(timestamp) {
  if (!timestamp) return '방금 전';
  
  const date = new Date(timestamp);
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const period = hours >= 12 ? '오후' : '오전';
  const displayHours = hours > 12 ? hours - 12 : hours === 0 ? 12 : hours;
  
  return `${period} ${displayHours}:${minutes.toString().padStart(2, '0')}`;
}

// 자동 스크롤
watch(() => modalStore.messages.length, async () => {
  await nextTick();
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight;
  }
});

onMounted(() => {
  console.log("ChatRoom 마운트됨, roomNo:", props.roomNo);
  fetchMessages(props.roomNo);
  connect(props.roomNo);
});

onBeforeUnmount(() => {
  stompClient?.deactivate();
});
</script>

<style scoped>
/* 채팅룸 전체 컨테이너 */
.chat-room {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

/* 채팅방 헤더 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  min-height: 70px;
}

.chat-partner-info {
  display: flex;
  align-items: center;
}

.partner-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.partner-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.partner-details {
  display: flex;
  flex-direction: column;
}

.partner-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  line-height: 1.2;
}

.member-count {
  font-size: 13px;
  color: #6c757d;
  margin-top: 2px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  color: #6c757d;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: #e9ecef;
  color: #2d3748;
}

/* 메시지 영역 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f7;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 날짜 구분선 */
.date-divider {
  text-align: center;
  margin: 20px 0 16px 0;
}

.date-divider span {
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 메시지 그룹 */
.message-group {
  display: flex;
  margin-bottom: 4px;
}

.my-message {
  justify-content: flex-end;
}

.sender-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 8px;
  flex-shrink: 0;
  align-self: flex-end;
}

.sender-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  max-width: 70%;
}

.sender-name {
  font-size: 13px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 4px;
  display: block;
}

.message-row {
  display: flex;
  align-items: flex-end;
  gap: 6px;
}

.my-message .message-row {
  flex-direction: row-reverse;
}

/* 말풍선 */
.bubble {
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.4;
  word-break: break-word;
  max-width: 100%;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.other-bubble {
  background: #ffffff;
  color: #2d3748;
  border-bottom-left-radius: 6px;
}

.my-bubble {
  background: #ffe066;
  color: #2d3748;
  border-bottom-right-radius: 6px;
}

.message-time {
  font-size: 11px;
  color: #8e8e93;
  white-space: nowrap;
  align-self: flex-end;
  margin-bottom: 2px;
}

/* 입력 영역 */
.chat-input-container {
  padding: 16px 20px;
  background: #ffffff;
  border-top: 1px solid #e9ecef;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 22px;
  padding: 8px 16px;
  transition: all 0.2s ease;
}

.input-wrapper:focus-within {
  border-color: #007AFF;
  background: #ffffff;
}

.attachment-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #8e8e93;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.attachment-btn:hover {
  background: #e9ecef;
}

.message-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  background: transparent;
  color: #2d3748;
  padding: 6px 0;
}

.message-input::placeholder {
  color: #8e8e93;
}

.send-btn {
  background: #007AFF;
  color: white;
  border: none;
  border-radius: 16px;
  padding: 6px 16px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
  background: #0056b3;
}

.send-btn:disabled {
  background: #c7c7cc;
  cursor: not-allowed;
}

/* 스크롤바 스타일링 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}
</style>