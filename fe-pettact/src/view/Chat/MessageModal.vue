<template>
  <div class="chat-app-container">
    <!-- 왼쪽: 채팅방 목록 영역 -->
    <div class="chat-sidebar">
      <!-- 헤더 -->
      <div class="sidebar-header">
        <button @click="closeModal" class="close-btn">×</button>
        <h4 class="header-title">채팅</h4>
      </div>

      <!-- 검색바 -->
      <SearchUserModal @roomOpen="openRoom" />

      <!-- 채팅방 목록 -->
      <div class="chat-rooms-container">
        <div v-if="chatRooms.length > 0" class="chat-rooms-list">
          <div
            v-for="room in chatRooms"
            :key="room.roomNo"
            @click="openRoom(room.roomNo)"
            :class="['chat-room-item', { active: modalStore.roomNo === room.roomNo }]"
          >
            <div class="room-avatar">
              <!-- <img src="/api/placeholder/50/50" :alt="room.name" /> -->
            </div>
            <div class="room-info">
              <div class="room-name">{{ room.name }}</div>
              <div class="room-last-message">{{ room.lastMessage || '채팅을 시작해보세요' }}</div>
            </div>
            <div class="room-meta">
              <div class="room-time">{{ formatTime(room.lastTime) }}</div>
              <span v-if="room.unreadCount > 0" class="unread-badge">{{ room.unreadCount }}</span>
            </div>
          </div>
        </div>

        <!-- 빈 상태 -->
        <div v-else class="empty-state">
          <div class="empty-icon">💭</div>
          <p>참여 중인 채팅방이 없습니다</p>
          <p class="empty-guide">상단 검색을 통해 새로운 채팅을 시작해보세요!</p>
        </div>
      </div>
    </div>

    <!-- 오른쪽: 채팅창 영역 -->
    <div class="chat-main">
      <!-- 항상 ChatRoom 표시하되, roomNo가 있을 때만 실제 기능 -->
      <ChatRoom v-if="modalStore.roomNo" :roomNo="modalStore.roomNo" />
      
      <!-- roomNo가 없을 때 환영 메시지 -->
      <div v-else class="welcome-screen">
        <div class="welcome-content">
          <h3>대화를 시작해보세요! 👋</h3>
          <p>왼쪽에서 채팅방을 선택하거나 검색을 통해 새로운 대화를 시작하세요.</p>
        </div>
      </div>
    </div>

    <!-- 기존 검색 모달 (필요시) -->
    <SearchUserModal
      v-if="showSearch"
      @roomOpen="openRoom"
      @close="showSearch = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useModalStore } from '@/js/modalStore';
import ChatRoom from './ChatRoom.vue';
import SearchUserModal from './SearchUserModal.vue';

const modalStore = useModalStore();
const chatRooms = ref([]);
const showSearch = ref(false);
const emit = defineEmits(['close']);

// function formatTime(timestamp) {
//   if (!timestamp) return '';
  
//   const now = new Date();
//   const date = new Date(timestamp);
//   const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
//   const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  
//   if (messageDate.getTime() === today.getTime()) {
//     // 오늘이면 시간만
//     const hours = date.getHours();
//     const minutes = date.getMinutes();
//     const period = hours >= 12 ? '오후' : '오전';
//     const displayHours = hours > 12 ? hours - 12 : hours === 0 ? 12 : hours;
//     return `${period} ${displayHours}:${minutes.toString().padStart(2, '0')}`;
//   } else {
//     // 오늘이 아니면 날짜
//     return `${date.getMonth() + 1}월 ${date.getDate()}일`;
//   }
// }

onMounted(() => {
  // 테스트 채팅방 데이터 추가
  chatRooms.value = [
    {
      roomNo: 1,
      name: "전설의고수",
      lastMessage: "그래, 이따 8시에 봐!",
      lastTime: "2024-10-20T14:05:00",
      unreadCount: 2
    },
    {
      roomNo: 2,
      name: "초보자",
      lastMessage: "지금 시작할 수 있나요?",
      lastTime: "2024-10-19T10:30:00",
      unreadCount: 0
    },
    {
      roomNo: 3,
      name: "게임왕",
      lastMessage: "오늘 저녁에 게임 가능?",
      lastTime: "2024-10-18T15:20:00",
      unreadCount: 1
    }
  ];

  console.log("테스트 채팅방 데이터 추가:", chatRooms.value);
  console.log("chatRooms.length:", chatRooms.value.length);
  console.log("첫 번째 채팅방:", chatRooms.value[0]);
  console.log("modalStore.roomNo:", modalStore.roomNo);

  // 실제 데이터도 가져오기 (API 호출)
  fetchChatRooms();
});

function fetchChatRooms() {
  axios.get('/v1/chat/room/my')
    .then(res => {
      chatRooms.value = res.data;
    })
    .catch(err => {
      console.error('채팅방 목록 조회 실패:', err);
    });
}

function openRoom(roomNo) {
  modalStore.roomNo = roomNo;
  showSearch.value = false;
}

function closeModal() {
  if (modalStore.roomNo) {
    // 채팅방 나가기 (room 리셋)
    modalStore.resetChat();
    modalStore.roomNo = null;
  } else {
    // 모달 닫기
    modalStore.closeMessageModal();
    emit('close');
  }
  // 채팅방 목록 새로고침
  fetchChatRooms();
}

function formatTime(timestamp) {
  if (!timestamp) return '';
  
  const now = new Date();
  const date = new Date(timestamp);
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  
  if (messageDate.getTime() === today.getTime()) {
    // 오늘이면 시간만
    return `오후 ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
  } else {
    // 오늘이 아니면 날짜
    return `${date.getMonth() + 1}월 ${date.getDate()}일`;
  }
}
</script>

<style scoped>
/* 전체 채팅 앱 컨테이너 */
.chat-app-container {
  position: fixed;
  top: 0;
  right: 0;
  width: 50vw;
  height: 100vh;
  display: flex;
  background: #ffffff;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

/* 왼쪽 사이드바 */
.chat-sidebar {
  width: 50%;
  background: #f8f9fa;
  border-right: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
}

/* 사이드바 헤더 */
.sidebar-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #ffffff;
  border-bottom: 1px solid #e9ecef;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #6c757d;
  margin-right: 12px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f1f5f9;
  color: #2d3748;
}

.header-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
}

/* 채팅방 컨테이너 */
.chat-rooms-container {
  flex: 1;
  overflow-y: auto;
}

/* 채팅방 목록 */
.chat-rooms-list {
  padding: 0;
}

.chat-room-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f1f5f9;
  position: relative;
}

.chat-room-item:hover {
  background: #f8fafe;
}

.chat-room-item.active {
  background: #e8f4fd;
  border-right: 3px solid #008BE6;
}

.room-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.room-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.room-info {
  flex: 1;
  min-width: 0;
  margin-right: 8px;
}

.room-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 4px;
  font-size: 15px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.room-last-message {
  color: #6c757d;
  font-size: 13px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.room-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.room-time {
  color: #a0aec0;
  font-size: 11px;
  white-space: nowrap;
}

.unread-badge {
  background: #ef4444;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: 700;
  min-width: 18px;
  text-align: center;
  line-height: 1.2;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
  opacity: 0.7;
}

.empty-state p {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
}

.empty-guide {
  font-size: 13px !important;
  font-weight: 400 !important;
  color: #a0aec0;
}

/* 오른쪽 메인 영역 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

/* 환영 화면 */
.welcome-screen {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbfc;
}

.welcome-content {
  text-align: center;
  color: #6c757d;
  max-width: 300px;
}

.welcome-content h3 {
  margin: 0 0 12px 0;
  font-size: 1.3rem;
  color: #2d3748;
  font-weight: 600;
}

.welcome-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

/* 스크롤바 스타일링 */
.chat-rooms-container::-webkit-scrollbar {
  width: 6px;
}

.chat-rooms-container::-webkit-scrollbar-track {
  background: transparent;
}

.chat-rooms-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.chat-rooms-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .chat-app-container {
    width: 60vw;
  }
}

@media (max-width: 768px) {
  .chat-app-container {
    width: 100vw;
  }
  
  .chat-sidebar {
    width: 100%;
  }
  
  .chat-main {
    display: none;
  }
}
</style>
