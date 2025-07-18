<template>
  <div class="modal-overlay" @click.self="closeModal">
    <div class="modal-box">
      <h4 v-if="!modalStore.roomNo">나의 채팅방</h4>

      <!-- 채팅방 목록 -->
      <ul v-if="!modalStore.roomNo && chatRooms.length > 0">
        <li
          v-for="room in chatRooms"
          :key="room.roomNo"
          @click="openRoom(room.roomNo)"
          class="chat-room-item"
        >
          {{ room.name }}
          <span v-if="room.unreadCount > 0" class="badge">{{ room.unreadCount }}</span>
        </li>
      </ul>
      <p v-else-if="!modalStore.roomNo">참여 중인 채팅방이 없습니다.</p>

      <!-- 채팅창 -->
      <ChatRoom v-if="modalStore.roomNo" :roomNo="modalStore.roomNo" />

      <!-- 닉네임 검색 버튼 -->
      <button v-if="!modalStore.roomNo" @click="showSearch = true" class="search-btn">
        닉네임으로 채팅 신청하기
      </button>

      <!-- 닉네임 검색 모달 -->
      <SearchUserModal
        v-if="showSearch"
        @roomOpen="openRoom"
        @close="showSearch = false"
      />

      <!-- 닫기 버튼 -->
      <button class="close-btn" @click="closeModal">
        {{ modalStore.roomNo ? '채팅방 나가기' : '모달 닫기' }}
      </button>
    </div>
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

onMounted(() => {
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
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-box {
  background: black;
  padding: 20px;
  border-radius: 12px;
  width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.chat-room-item {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  cursor: pointer;
}

.chat-room-item:hover {
  background-color: black;
}

.search-btn,
.close-btn {
  margin-top: 12px;
  padding: 8px 16px;
  background-color: black;
  border: none;
  color: white;
  border-radius: 6px;
  cursor: pointer;
}

.search-btn:hover,
.close-btn:hover {
  background-color: black;
}

.chat-room-item {
  position: relative;
  padding-right: 30px;
}

.badge {
  position: absolute;
  top: 50%;
  right: 5px;
  transform: translateY(-50%);
  background-color: red;
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 12px;
}
</style>
