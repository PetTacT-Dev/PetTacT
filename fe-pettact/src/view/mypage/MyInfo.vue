<template>
  <div class="mypage-container">
    <div class="main-content">
      <!-- 나의 정보 -->
      <div v-if="currentMenu === 'myInfo'" class="content-section">
        <div class="content-header">
          <h1 class="page-title">나의 정보</h1>
        </div>

        <div class="content-body">
          <div v-if="error" class="error-alert">
            {{ error }}
          </div>

          <div v-if="userInfo" class="user-info-grid">
            <div class="info-item">
              <div class="info-label">이메일</div>
              <div class="info-value">{{ userInfo.userEmail }}</div>
            </div>

            <div class="info-item">
              <div class="info-label">이름</div>
              <div class="info-value">{{ userInfo.userName }}</div>
            </div>

            <div class="info-item">
              <div class="info-label">생년월일</div>
              <div class="info-value">{{ userInfo.userBirth }}</div>
            </div>

            <div class="info-item">
              <div class="info-label">닉네임</div>
              <div class="info-value">{{ userInfo.userNickname }}</div>
            </div>

            <div class="info-item">
              <div class="info-label">전화번호</div>
              <div class="info-value">{{ userInfo.userTel }}</div>
            </div>

            <div class="info-item">
              <div class="info-label">우편번호</div>
              <div class="info-value">{{ userInfo.userZipcode }}</div>
            </div>

            <div class="info-item full-width">
              <div class="info-label">주소</div>
              <div class="info-value">{{ userInfo.userStreet1 }} {{ userInfo.userDetailAddress }}</div>
            </div>
          </div>

          <div class="action-buttons">
            <button class="btn-primary" @click="showUpdateModal = true">정보 수정</button>
            <button class="btn-danger" @click="showWithdrawModal = true">탈퇴하기</button>
          </div>

          <!-- 기존 모달 컴포넌트들 -->
          <VerifyPasswordModal v-if="showUpdateModal" @close="showUpdateModal = false" :onVerified="goToUpdate" />
          <VerifyPasswordModal v-if="showWithdrawModal" @close="showWithdrawModal = false" :onVerified="withdraw" />
        </div>
      </div>

      <!-- 나의 반려동물 -->
      <div v-else-if="currentMenu === 'myPets'" class="content-section">
        <div class="content-header">
          <h1 class="page-title">나의 반려동물</h1>
        </div>
        <div class="content-body">
          <div class="coming-soon">
            <div class="coming-icon">🐾</div>
            <h3>반려동물 관리 기능</h3>
            <p>곧 출시될 예정입니다!</p>
          </div>
        </div>
      </div>

      <!-- 나의 관심공고 -->
      <div v-else-if="currentMenu === 'interests'" class="content-section">
        <div class="content-header">
          <h1 class="page-title">나의 관심공고</h1>
        </div>
        <div class="content-body">
          <div class="coming-soon">
            <div class="coming-icon">💝</div>
            <h3>관심공고 관리 기능</h3>
            <p>곧 출시될 예정입니다!</p>
          </div>
        </div>
      </div>

      <!-- 일기 -->
      <div v-else-if="currentMenu === 'diary'" class="content-section">
        <div class="content-header">
          <h1 class="page-title">일기</h1>
        </div>
        <div class="content-body">
          <div class="coming-soon">
            <div class="coming-icon">📔</div>
            <h3>반려동물 일기 기능</h3>
            <p>곧 출시될 예정입니다!</p>
          </div>
        </div>
      </div>

      <!-- 나의 마켓 -->
      <div v-else-if="currentMenu === 'market'" class="content-section">
        <div class="content-header">
          <h1 class="page-title">나의 마켓</h1>
        </div>
        <div class="content-body">
          <div class="coming-soon">
            <div class="coming-icon">🛒</div>
            <h3>마켓 관리 기능</h3>
            <p>곧 출시될 예정입니다!</p>
          </div>
        </div>
      </div>

      <!-- 나의 활동 -->
      <div v-else-if="currentMenu === 'activity'" class="content-section">
        <div class="content-header">
          <h1 class="page-title">나의 활동</h1>
        </div>
        <div class="content-body">
          <div class="coming-soon">
            <div class="coming-icon">📊</div>
            <h3>활동 통계 기능</h3>
            <p>곧 출시될 예정입니다!</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import VerifyPasswordModal from './components/VerifyPasswordModal.vue';

const userInfo = ref('');
const error = ref('');
const router = useRouter();

const showUpdateModal = ref(false);
const showWithdrawModal = ref(false);

// 메뉴 상태 추가
const currentMenu = ref('myInfo');

const setCurrentMenu = (menu) => {
  currentMenu.value = menu;
};

onMounted(async () => {
  try {
    const res = await axios.get('/v1/user/mypage/myInfo');
    userInfo.value = res.data;
  } catch (err) {
    console.error(err);
    if (err.response && err.response.status === 401) {
      alert(err.response.data);
      router.push({ name: 'login' });
    } else {
      error.value = err.response?.data || '오류가 발생했습니다.';
    }
  }
});

const goToUpdate = () => {
  router.push({ name: 'myInfoUpdate' });
};

const withdraw = async () => {
  try {
    await axios.delete('/v1/user/mypage/withdraw');
    alert('탈퇴 완료');
    router.push('/');
  } catch {
    alert('탈퇴 실패');
  }
};

const handleRequestSeller = async () => {
  if (!confirm('판매자 권한을 요청하시겠습니까?')) return;

  try {
    const res = await axios.post('/v1/user/seller/request');
    alert('판매자 권한 요청이 접수되었습니다.');
  } catch (err) {
    console.error(err);
    alert(err.response.data)
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.mypage-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  gap: 30px;
  padding: 30px 20px;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
  background-color: #f7f7f7;
  justify-content: center;
}

/* 메인 컨텐츠 스타일 */
.main-content {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.content-section {
  height: 100%;
}

.content-header {
  padding: 30px 30px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.content-body {
  padding: 30px;
}

.error-alert {
  background: #fff5f5;
  color: #e53e3e;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #fed7d7;
  margin-bottom: 20px;
}

.user-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.info-item {
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.btn-primary {
  background: #008BE6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background: #0066CC;
}

.btn-danger {
  background: #dc3545;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-danger:hover {
  background: #c82333;
}

.coming-soon {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.coming-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.coming-soon h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #333;
}

.coming-soon p {
  font-size: 16px;
  color: #666;
}

/* 반응형 */
@media (max-width: 768px) {
  .mypage-container {
    flex-direction: column;
    gap: 20px;
    padding: 20px 15px;
  }

  .sidebar {
    width: 100%;
  }

  .content-header {
    padding: 20px 20px 15px;
  }

  .content-body {
    padding: 20px;
  }

  .page-title {
    font-size: 20px;
  }

  .user-info-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons button {
    width: 100%;
  }
}
</style>