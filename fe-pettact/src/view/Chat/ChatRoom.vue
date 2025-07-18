<template>
  <div class="chat-room">
    <div ref="chatBox" class="chat-messages">
      <div
        v-for="(msg, index) in modalStore.messages"
        :key="index"
        :class="['chat-message', msg.isMine ? 'me' : 'other']"
      >
        <span class="sender">{{ msg.senderNickname }}</span>
        <div class="bubble">{{ msg.message }}</div>
      </div>
    </div>

    <div class="chat-input">
      <input
        v-model="message"
        @keyup.enter="sendMessage"
        type="text"
        placeholder="메시지를 입력하세요"
      />
      <button @click="sendMessage">전송</button>
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

// 자동 스크롤
watch(() => modalStore.messages.length, async () => {
  await nextTick();
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight;
  }
});

onMounted(() => {
  fetchMessages(props.roomNo);
  connect(props.roomNo);
});

onBeforeUnmount(() => {
  stompClient?.deactivate();
});
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 400px;
  background-color: #e5ddd5;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-message {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.chat-message.me {
  align-self: flex-end;
  align-items: flex-end;
}

.chat-message.other {
  align-self: flex-start;
  align-items: flex-start;
}

.sender {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.bubble {
  position: relative;
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.4;
  background-color: #ffffff;
  color: #000;
  word-break: break-word;
}

.chat-message.me .bubble {
  background-color: #9fe6a0;
  color: #000;
}

.chat-message.other .bubble::before {
  content: '';
  position: absolute;
  top: 10px;
  left: -6px;
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-right: 6px solid #ffffff;
  border-bottom: 6px solid transparent;
}

.chat-message.me .bubble::before {
  content: '';
  position: absolute;
  top: 10px;
  right: -6px;
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-left: 6px solid #9fe6a0;
  border-bottom: 6px solid transparent;
}

.chat-input {
  display: flex;
  border-top: 1px solid #ccc;
  padding: 10px;
  background-color: #f0f0f0;
}

.chat-input input {
  flex: 1;
  padding: 10px;
  border-radius: 20px;
  border: 1px solid #ccc;
  font-size: 14px;
  outline: none;
  background-color: #fff;
  margin-right: 8px;
}

.chat-input button {
  padding: 8px 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
}

.chat-input button:hover {
  background-color: #43a047;
}
</style>
