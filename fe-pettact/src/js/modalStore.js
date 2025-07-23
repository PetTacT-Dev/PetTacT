// @/js/modalStore.js
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useModalStore = defineStore('modalStore', () => {
  // 💬 모달 열림 상태
  const isMessageModalOpen = ref(false);

  // 채팅 관련 상태
  const roomNo = ref(null);
  const messages = ref([]);
  const connected = ref(false);
  const stompClient = ref(null);

  // 모달 제어
  function openMessageModal() {
    isMessageModalOpen.value = true;
  }

  function closeMessageModal() {
    isMessageModalOpen.value = false;
  }

  function resetRoom() {
  this.roomNo = null
  this.messages = []
  this.stompClient?.deactivate()
  this.stompClient = null
  this.connected = false
 }
  // 채팅 관련 액션
  function setClient(client) {
    stompClient.value = client;
  }

  function addMessage(msg) {
    messages.value.push(msg);
  }

  function resetChat() {
    roomNo.value = null;
    messages.value = [];
    connected.value = false;
    stompClient.value = null;
  }

  return {
    // 모달 상태
    isMessageModalOpen,
    openMessageModal,
    closeMessageModal,

    // 채팅 상태 및 액션
    roomNo,
    messages,
    connected,
    stompClient,
    setClient,
    addMessage,
    resetChat,
  };
});
