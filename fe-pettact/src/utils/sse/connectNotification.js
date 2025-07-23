import { useNotificationStore } from '@/stores/notification';

let eventSource = null;

export function connectNotificationSSE(token) {
  console.log('[SSE] 연결 시작');

  if (eventSource) {
    eventSource.close();
  }

  eventSource = new EventSource(`/v1/notification/subscribe?token=${token}`);

  eventSource.addEventListener("notification", (event) => {
    const data = JSON.parse(event.data);
    console.log('[SSE] 알림 수신:', data);

    // store 직접 접근해서 알림 추가
    const notificationStore = useNotificationStore();
    notificationStore.addNotification(data);
  });

  eventSource.onerror = (error) => {
    console.error('[SSE] 연결 오류:', error);
    eventSource.close();
    eventSource = null;
  };
}

export function disconnectNotificationSSE() {
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
}
