<template>
  <div style="position: fixed; top: 0; right: 0; width: 50vw; height: 100vh; background: white; z-index: 1000; border-left: 2px solid #ccc;">
    
    <!-- 왼쪽 절반: 채팅방 목록 -->
    <div style="width: 50%; height: 100%; float: left; background: #f5f5f5; padding: 20px;">
      <h3>채팅방 목록</h3>
      <button @click="closeModal" style="float: right; background: red; color: white; border: none; padding: 5px 10px;">×</button>
      
      <p>채팅방 개수: {{ chatRooms.length }}</p>
      
      <div v-for="room in chatRooms" 
           :key="room.roomNo" 
           @click="selectRoom(room.roomNo)"
           style="padding: 10px; margin: 10px 0; background: white; border: 1px solid #ddd; cursor: pointer;">
        <strong>{{ room.name }}</strong>
        <span v-if="room.unreadCount > 0" style="background: red; color: white; padding: 2px 6px; border-radius: 10px; margin-left: 10px;">
          {{ room.unreadCount }}
        </span>
      </div>
      
      <div v-if="chatRooms.length === 0" style="padding: 20px; text-align: center; color: #666;">
        채팅방이 없습니다
      </div>
    </div>

    <!-- 오른쪽 절반: 채팅창 -->
    <div style="width: 50%; height: 100%; float: right; background: white; padding: 20px;">
      <div v-if="selectedRoomNo">
        <h3>채팅방 {{ selectedRoomNo }}</h3>
        <ChatRoom :roomNo="selectedRoomNo" />
      </div>
      <div v-else style="text-align: center; margin-top: 100px; color: #666;">
        <h3>채팅방을 선택하세요</h3>
        <p>왼쪽에서 채팅방을 클릭해주세요</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useModalStore } from '@/js/modalStore';
import ChatRoom from './ChatRoom.vue';

const modalStore = useModalStore();
const chatRooms = ref([]);
const selectedRoomNo = ref(null);
const emit = defineEmits(['close']);

function selectRoom(roomNo) {
  selectedRoomNo.value = roomNo;
  modalStore.roomNo = roomNo;
  console.log("채팅방 선택:", roomNo);
}

function closeModal() {
  modalStore.closeMessageModal();
  emit('close');
}

onMounted(() => {
  // 테스트 데이터
  chatRooms.value = [
    { roomNo: 1, name: "전설의고수", unreadCount: 2 },
    { roomNo: 2, name: "초보자", unreadCount: 0 },
    { roomNo: 3, name: "게임왕", unreadCount: 1 }
  ];
  
  console.log("간단한 테스트 모달 로드");
  console.log("채팅방 데이터:", chatRooms.value);
});
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
  justify-content: space-between;
  padding: 16px 20px;
  background: #ffffff;
  border-bottom: 1px solid #e9ecef;
}

.header-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #6c757d;
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

/* 검색창 */
.search-container {
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: #ffffff;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  padding: 10px 16px;
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

/* 채팅방 컨테이너 */
.chat-rooms-container {
  flex: 1;
  overflow-y: auto;
}

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