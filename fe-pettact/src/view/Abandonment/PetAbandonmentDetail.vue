<template>
  <div class="pet-register-container">
    <div class="pet-register-wrapper">
      <div class="pet-register-header">
        <h1>유기동물 상세정보</h1>
        <p class="register-subtitle" v-if="detail"></p>
        <p class="register-subtitle" v-else>정보를 불러오는 중입니다...</p>
      </div>

      <div v-if="detail" class="pet-detail-view">
        <div v-if="images.length" class="carousel slide mb-4" id="carouselImages" data-bs-ride="carousel">
          <div class="carousel-inner">
            <div
              v-for="(img, idx) in images"
              :key="idx"
              class="carousel-item"
              :class="{ active: idx === 0 }"
            >
              <img :src="img" class="d-block w-100" alt="유기동물 사진" />
            </div>
          </div>
          <button class="carousel-control-prev" type="button" data-bs-target="#carouselImages" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" />
          </button>
          <button class="carousel-control-next" type="button" data-bs-target="#carouselImages" data-bs-slide="next">
            <span class="carousel-control-next-icon" />
          </button>
        </div>
        <div class="field-group">
          <label class="field-label">공고번호</label>
          <p class="field-value light">{{ detail.noticeNo }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">구조일</label>
          <p class="field-value light">{{ detail.noticeSdt }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">보호중인 보호소</label>
          <p class="field-value light">{{ detail.careNm }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">연락처</label>
          <p class="field-value light">{{ detail.careTel }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">발견장소</label>
          <p class="field-value light">{{ detail.happenPlace }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">품종</label>
          <p class="field-value light">{{ detail.kindNm }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">나이</label>
          <p class="field-value light">{{ detail.age }}</p>
        </div>
        <div class="field-group">
          <label class="field-label">무게</label>
          <p class="field-value light">{{ detail.weight }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">상태</label>
          <p class="field-value light">{{ detail.processState }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">특이사항</label>
          <p class="field-value light">{{ detail.specialMark || '없음' }}</p>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label class="field-label">공고 시작일</label>
            <p class="field-value light">{{ formatDate(detail.noticeSdt) }}</p>
          </div>
          <div class="field-group">
            <label class="field-label">공고 종료일</label>
            <p class="field-value light">{{ formatDate(detail.noticeEdt) }}</p>
          </div>
        </div>

        <div class="button-group mt-4">
          <button class="register-button" v-if="isAdmin" @click="updateDetail">수정하기</button>
          <button class="register-button" style="background: #666;" @click="goToList">목록으로</button>
          <button class="register-button" style="background: #444;" v-if="isAdmin" @click="goToAdminDashboard">관리자 페이지</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      detail: null,
      images: [],
      isAdmin: false,
    };
  },
  mounted() {
    const desertionNo = this.$route.params.desertionNo;

    axios.get(`/v1/api/abandonment/${desertionNo}`).then((res) => {
      this.detail = res.data;
      this.extractImages(res.data);
    });

    axios.get('/v1/member/me').then((res) => {
      this.isAdmin = res.data.role === 'ADMIN';
    });
  },
  methods: {
    extractImages(data) {
      this.images = [];
      if (data.popfile1) this.images.push(data.popfile1);
      if (data.popfile2) this.images.push(data.popfile2);
    },
    updateDetail() {
      if (!this.isAdmin) return;

      const desertionNo = this.$route.params.desertionNo;
      axios.put(`/v1/api/abandonment/${desertionNo}`, this.detail)
        .then((res) => {
          alert('수정 완료');
          this.detail = res.data;
        })
        .catch((err) => {
          console.error('수정 실패:', err);
          alert('수정 실패');
        });
    },
    goToList() {
      this.$router.push('/abandonment');
    },
    goToAdminDashboard() {
      this.$router.push('/admin');
    },
    formatDate(str) {
      if (!str) return '';
      return `${str.slice(0, 4)}-${str.slice(4, 6)}-${str.slice(6, 8)}`;
    },
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.pet-register-container {
  width: 90%;
  max-width: 600px;
  margin: 0 auto;
  background: white;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, sans-serif;
}

.pet-register-wrapper {
  width: 100%;
  max-width: 540px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.pet-register-header {
  text-align: center;
}

.pet-register-header h1 {
  font-family: "Inter", sans-serif;
  font-weight: 700;
  font-size: 24px;
  line-height: 33.6px;
  color: black;
  margin-bottom: 12px;
}

.register-subtitle {
  color: #666;
  font-size: 16px;
  line-height: 1.5;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.field-label {
  font-family: "Pretendard", sans-serif;
  font-weight: 500;
  font-size: 14px;
  color: #333;
  margin-left: 5px;
}

.field-value {
  font-size: 15px;
  padding: 12px 15px;
  border-radius: 5px;
}

.field-value.light {
  background-color: #fafafa;
  color: #444;
  border: 1px solid #eee;
}

.register-button {
  width: 100%;
  height: 50px;
  background: #008be6;
  border: none;
  border-radius: 7px;
  color: white;
  font-family: "Pretendard", sans-serif;
  font-weight: 600;
  font-size: 18px;
  line-height: 28px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 10px;
}

.register-button:hover:not(:disabled) {
  background: #0066cc;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

.carousel {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

@media (max-width: 768px) {
  .pet-register-container {
    padding: 20px 15px;
  }

  .field-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .pet-register-header h1 {
    font-size: 20px;
  }
}
</style>
