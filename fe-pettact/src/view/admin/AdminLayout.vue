<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <div class="sidebar">
      <h4 class="sidebar-title">관리자</h4>
      <ul class="sidebar-menu">
        <!-- 공통 -->
        <li class="menu-item">
          <router-link :to="{ name: 'adminDashboard' }" class="menu-link">
            <i class="icon">📊</i>
            대시보드
          </router-link>
        </li>

        <!-- 회원 관리 -->
        <li class="menu-item">
          <router-link :to="{ name: 'adminUserList' }" class="menu-link">
            <i class="icon">👥</i>
            회원 관리
          </router-link>
        </li>

        <!-- 게시물 관리 -->
        <li class="menu-item">
          <router-link :to="{ name: 'adminBoardList' }" class="menu-link">
            <i class="icon">📝</i>
            게시물 관리
          </router-link>
        </li>

        <!-- 구분선 -->
        <li class="menu-divider"></li>

        <!-- 판매자 관리 -->
        <li class="menu-category">판매자 관리</li>
        <li class="menu-item">
          <router-link :to="{ name: 'adminSellerRequests' }" class="menu-link">
            <i class="icon">⏰</i>
            권한 요청 대기
          </router-link>
        </li>
        <li class="menu-item">
          <router-link :to="{ name: 'adminSellerList' }" class="menu-link">
            <i class="icon">🏪</i>
            판매자 목록
          </router-link>
        </li>

        <!-- 구분선 -->
        <li class="menu-divider"></li>

        <!-- 신고 관리 -->
        <li class="menu-category">신고 관리</li>
        <li class="menu-item">
          <router-link :to="{ name: 'adminReportList' }" class="menu-link">
            <i class="icon">🚨</i>
            신고
          </router-link>
        </li>
      </ul>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Top Navigation -->
      <div class="top-nav">
        <div class="nav-left">
          <h1 class="page-title">관리자 대시보드</h1>
        </div>
        <div class="nav-right">
          <div class="user-info">
            <span class="user-name">관리자</span>
            <div class="user-avatar">👤</div>
          </div>
        </div>
      </div>

      <!-- Content Area -->
      <div class="content-wrapper">
        <!-- 통계 카드 -->
        <div class="stats-grid">
          <!-- 카테고리별 게시물 비율 (큰 카드) -->
          <div class="category-chart-card">
            <div class="card-header">
              <h3>📊 카테고리별 게시물 비율</h3>
              <div class="chart-controls">
                <select class="time-selector">
                  <option>최근 7일</option>
                  <option>최근 30일</option>
                  <option>최근 3개월</option>
                </select>
              </div>
            </div>
            <div class="chart-content">
              <div class="donut-chart-container">
                <canvas id="categoryChart" width="200" height="200"></canvas>
                <div class="chart-center-text">
                  <div class="center-number">1,247</div>
                  <div class="center-label">총 게시물</div>
                </div>
              </div>
              <div class="chart-legend">
                <div class="legend-item">
                  <div class="legend-color" style="background: #007bff;"></div>
                  <span class="legend-label">일반 게시물</span>
                  <span class="legend-value">45.2%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color" style="background: #28a745;"></div>
                  <span class="legend-label">상품 리뷰</span>
                  <span class="legend-value">28.7%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color" style="background: #ffc107;"></div>
                  <span class="legend-label">Q&A</span>
                  <span class="legend-value">15.8%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color" style="background: #dc3545;"></div>
                  <span class="legend-label">공지사항</span>
                  <span class="legend-value">10.3%</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 작은 통계 카드들 -->
          <div class="small-stats">
            <div class="stat-card">
              <div class="stat-header">
                <h4>전체 회원</h4>
                <div class="stat-icon">👥</div>
              </div>
              <div class="stat-number">1</div>
              <div class="stat-change positive">+0% 전일 대비</div>
            </div>

            <div class="stat-card">
              <div class="stat-header">
                <h4>오늘 가입</h4>
                <div class="stat-icon">🆕</div>
              </div>
              <div class="stat-number">0</div>
              <div class="stat-change neutral">변동 없음</div>
            </div>

            <div class="stat-card">
              <div class="stat-header">
                <h4>가입자 증가율</h4>
                <div class="stat-icon">📈</div>
              </div>
              <div class="stat-number">100.00%</div>
              <div class="stat-change positive">+100% 전월 대비</div>
            </div>

            <div class="stat-card">
              <div class="stat-header">
                <h4>판매자 승인 대기</h4>
                <div class="stat-icon">⏳</div>
              </div>
              <div class="stat-number">0</div>
              <div class="stat-change neutral">처리 완료</div>
            </div>
          </div>
        </div>

        <!-- 차트 영역 -->
        <div class="charts-grid">
          <div class="chart-card">
            <div class="chart-header">
              <h3>일별 신규 사용자</h3>
              <div class="chart-period">최근 7일간 추이</div>
            </div>
            <div class="chart-body">
              <canvas id="userChart" width="400" height="200"></canvas>
            </div>
          </div>

          <div class="chart-card">
            <div class="chart-header">
              <h3>일별 게시글 등록 수</h3>
              <div class="chart-period">최근 7일간 추이</div>
            </div>
            <div class="chart-body">
              <canvas id="postChart" width="400" height="200"></canvas>
            </div>
          </div>

          <div class="chart-card full-width">
            <div class="chart-header">
              <h3>일별 신고 접수 수</h3>
              <div class="chart-period">최근 7일간 추이</div>
            </div>
            <div class="chart-body">
              <canvas id="reportChart" width="400" height="150"></canvas>
            </div>
          </div>
        </div>

        <router-view />
      </div>
    </div>

    <!-- Chat Sidebar -->
    <div class="chat-sidebar">
      <!-- 실시간 채팅 -->
      <div class="chat-section">
        <div class="chat-header">
          <h3>💬 실시간 채팅</h3>
          <div class="online-status">
            <span class="status-dot"></span>
            <span>Online</span>
          </div>
        </div>
        
        <div class="chat-messages" ref="chatMessages">
          <div 
            v-for="message in messages" 
            :key="message.id"
            :class="['message', message.type]"
          >
            <div class="message-content">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
        
        <div class="chat-input-container">
          <input 
            v-model="newMessage"
            @keypress.enter="sendMessage"
            type="text" 
            class="chat-input" 
            placeholder="메시지를 입력하세요..."
          >
          <button @click="sendMessage" class="send-btn">▶</button>
        </div>
      </div>

      <!-- 쇼핑몰 결제 합산 -->
      <div class="payment-summary-section">
        <div class="section-header">
          <h3>🛒 쇼핑몰 결제 합산</h3>
          <span class="last-update">실시간</span>
        </div>
        
        <div class="summary-stats">
          <div class="summary-stat">
            <div class="stat-icon">💰</div>
            <div class="stat-info">
              <div class="stat-value">46,000,000원</div>
              <div class="stat-label">총 결제 금액</div>
            </div>
          </div>
          <div class="summary-stat">
            <div class="stat-icon">📊</div>
            <div class="stat-info">
              <div class="stat-value">1,847건</div>
              <div class="stat-label">결제 건수</div>
            </div>
          </div>
          <div class="summary-stat">
            <div class="stat-icon">📈</div>
            <div class="stat-info">
              <div class="stat-value">+12.5%</div>
              <div class="stat-label">전월 대비</div>
            </div>
          </div>
        </div>

        <div class="payment-methods">
          <h4>결제 수단별 현황</h4>
          <div class="method-item">
            <span class="method-name">카드결제</span>
            <div class="method-bar">
              <div class="method-progress" style="width: 65%; background: #007bff;"></div>
            </div>
            <span class="method-percent">65%</span>
          </div>
          <div class="method-item">
            <span class="method-name">계좌이체</span>
            <div class="method-bar">
              <div class="method-progress" style="width: 25%; background: #28a745;"></div>
            </div>
            <span class="method-percent">25%</span>
          </div>
          <div class="method-item">
            <span class="method-name">간편결제</span>
            <div class="method-bar">
              <div class="method-progress" style="width: 10%; background: #ffc107;"></div>
            </div>
            <span class="method-percent">10%</span>
          </div>
        </div>
      </div>

      <!-- 빈 섹션 (추후 확장용) -->
      <div class="empty-section">
        <div class="empty-content">
          <div class="empty-icon">📋</div>
          <div class="empty-text">
            <h4>새로운 위젯</h4>
            <p>곧 추가될 예정입니다</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  data() {
    return {
      chatVisible: true,
      newMessage: '',
      messages: [
        {
          id: 1,
          type: 'user',
          content: '안녕하세요! 문의사항이 있습니다.',
          time: '14:30'
        },
        {
          id: 2,
          type: 'admin',
          content: '안녕하세요! 무엇을 도와드릴까요?',
          time: '14:32'
        },
        {
          id: 3,
          type: 'user',
          content: '판매자 등록은 어떻게 하나요?',
          time: '14:33'
        },
        {
          id: 4,
          type: 'admin',
          content: '판매자 등록은 마이페이지에서 신청하실 수 있습니다.',
          time: '14:35'
        }
      ]
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts();
    });
  },
  methods: {
    initCharts() {
      // 신고 차트
      const reportCtx = document.getElementById('reportChart');
      if (reportCtx) {
        new Chart(reportCtx, {
          type: 'line',
          data: {
            labels: ['월', '화', '수', '목', '금', '토', '일'],
            datasets: [{
              label: '신고 접수',
              data: [2, 1, 3, 5, 2, 1, 4],
              borderColor: '#dc3545',
              backgroundColor: 'rgba(220, 53, 69, 0.1)',
              tension: 0.4,
              fill: true,
              pointBackgroundColor: '#dc3545',
              pointBorderColor: '#ffffff',
              pointBorderWidth: 2
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              y: {
                beginAtZero: true,
                grid: {
                  color: 'rgba(0,0,0,0.05)'
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            },
            plugins: {
              legend: {
                display: false
              }
            }
          }
        });
      }
    },
    toggleChat() {
      this.chatVisible = !this.chatVisible;
    },
    sendMessage() {
      if (this.newMessage.trim()) {
        const now = new Date();
        const timeString = now.getHours().toString().padStart(2, '0') + ':' + 
                          now.getMinutes().toString().padStart(2, '0');
        
        this.messages.push({
          id: Date.now(),
          type: 'admin',
          content: this.newMessage,
          time: timeString
        });
        
        this.newMessage = '';
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    scrollToBottom() {
      const container = this.$refs.chatMessages;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }
  }
};
</script>

<style scoped>
.admin-layout {
  display: grid;
  grid-template-columns: 250px 1fr 400px;
  height: 100vh;
  background: #f8f9fa;
}

/* Sidebar Styles */
.sidebar {
  background: #2c3e50;
  color: white;
  padding: 2rem 0;
  overflow-y: auto;
}

.sidebar-title {
  text-align: center;
  margin-bottom: 2rem;
  color: #3498db;
  font-size: 1.5rem;
  font-weight: bold;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  margin: 0.2rem 0;
}

.menu-link {
  display: flex;
  align-items: center;
  padding: 1rem 2rem;
  color: #ecf0f1;
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.menu-link:hover {
  background: rgba(52, 152, 219, 0.2);
  border-left-color: #3498db;
  color: white;
  text-decoration: none;
}

.menu-link.router-link-active {
  background: rgba(52, 152, 219, 0.3);
  border-left-color: #3498db;
  color: white;
}

.icon {
  margin-right: 0.5rem;
  font-size: 1.1rem;
}

.menu-category {
  padding: 1rem 2rem;
  color: #95a5a6;
  font-weight: bold;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 1rem 2rem;
}

/* Main Content Styles */
.main-content {
  display: flex;
  flex-direction: column;
  background: white;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  background: white;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.page-title {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.nav-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border-radius: 20px;
}

.user-name {
  color: #2c3e50;
  font-weight: 500;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #3498db;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

/* Content Wrapper Styles */
.content-wrapper {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: 2fr 2fr;
  grid-template-rows: auto auto;
  gap: 2rem;
  margin-bottom: 2rem;
}

/* Category Chart Card */
.category-chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  padding: 2rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.card-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
}

.time-selector {
  padding: 0.5rem 1rem;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  background: white;
  color: #2c3e50;
  font-size: 0.9rem;
}

.chart-content {
  display: flex;
  align-items: center;
  gap: 3rem;
}

.donut-chart-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-center-text {
  position: absolute;
  text-align: center;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.center-number {
  font-size: 2rem;
  font-weight: bold;
  color: #2c3e50;
}

.center-label {
  font-size: 0.9rem;
  color: #6c757d;
  margin-top: 0.2rem;
}

.chart-legend {
  flex: 1;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  gap: 0.8rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-label {
  flex: 1;
  color: #2c3e50;
  font-size: 0.9rem;
}

.legend-value {
  font-weight: bold;
  color: #2c3e50;
  font-size: 0.9rem;
}

/* Small Stats */
.small-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  padding: 1.5rem;
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.stat-header h4 {
  margin: 0;
  color: #6c757d;
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-icon {
  font-size: 1.2rem;
  opacity: 0.7;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.stat-change {
  font-size: 0.8rem;
}

.stat-change.positive {
  color: #28a745;
}

.stat-change.negative {
  color: #dc3545;
}

.stat-change.neutral {
  color: #6c757d;
}

/* Charts Grid */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  padding: 2rem;
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-header {
  margin-bottom: 2rem;
}

.chart-header h3 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
}

.chart-period {
  color: #6c757d;
  font-size: 0.9rem;
}

.chart-body {
  position: relative;
  height: 200px;
}

/* Payment Summary Section */
.payment-summary-section {
  background: white;
  margin: 0 1rem 1rem 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  padding: 1.5rem;
}

.summary-stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.summary-stat {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-icon {
  font-size: 1.5rem;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #2c3e50;
}

.stat-label {
  font-size: 0.8rem;
  color: #6c757d;
}

.payment-methods h4 {
  margin-bottom: 1rem;
  color: #2c3e50;
  font-size: 1rem;
}

.method-item {
  display: grid;
  grid-template-columns: 80px 1fr 40px;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.8rem;
}

.method-name {
  font-size: 0.9rem;
  color: #2c3e50;
}

.method-bar {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.method-progress {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.method-percent {
  font-size: 0.8rem;
  color: #6c757d;
  text-align: right;
}

/* Empty Section */
.empty-section {
  background: white;
  margin: 0 1rem 1rem 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  padding: 2rem;
  text-align: center;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.empty-icon {
  font-size: 3rem;
  opacity: 0.3;
}

.empty-text h4 {
  margin: 0 0 0.5rem 0;
  color: #6c757d;
  font-size: 1rem;
}

.empty-text p {
  margin: 0;
  color: #adb5bd;
  font-size: 0.9rem;
}

/* Chat Sidebar Styles */
.chat-sidebar {
  background: #f8f9fa;
  border-left: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

/* Funding Overview */
.funding-overview {
  padding: 1.5rem;
  background: white;
  margin: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.last-update {
  font-size: 0.8rem;
  color: #6c757d;
}

.funding-stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 0.8rem;
  color: #6c757d;
  margin-bottom: 0.3rem;
}

.stat-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #2c3e50;
}

.stat-sub {
  font-size: 0.8rem;
  color: #6c757d;
}

/* Payment Info */
.payment-info {
  margin: 0 1rem 1rem 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.payment-info .section-header {
  padding: 1.5rem 1.5rem 0 1.5rem;
}

.add-btn {
  background: #28a745;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 14px;
  cursor: pointer;
}

.payment-list {
  padding: 0 1.5rem;
}

.payment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.payment-amount {
  font-weight: bold;
  color: #2c3e50;
}

.payment-date {
  font-size: 0.8rem;
  color: #6c757d;
}

.payment-type {
  font-size: 0.8rem;
  color: #6c757d;
  background: #e9ecef;
  padding: 0.2rem 0.5rem;
  border-radius: 12px;
}

.payment-summary {
  padding: 1rem 1.5rem;
  border-top: 1px solid #f1f3f4;
}

.payment-summary h4 {
  font-size: 0.9rem;
  color: #2c3e50;
  margin-bottom: 0.8rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  color: #2c3e50;
  padding-top: 0.5rem;
  border-top: 1px solid #f1f3f4;
}

.question-section {
  text-align: center;
  padding: 1rem 1.5rem 1.5rem 1.5rem;
}

.question-section p {
  margin-bottom: 0.8rem;
  color: #6c757d;
  font-size: 0.9rem;
}

.question-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
}

/* Chat Section */
.chat-section {
  background: white;
  margin: 0 1rem 1rem 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f1f3f4;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h3 {
  margin: 0;
  font-size: 1rem;
  color: #2c3e50;
}

.online-status {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.8rem;
  color: #28a745;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #28a745;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.chat-messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

.message {
  margin-bottom: 1rem;
  max-width: 85%;
}

.message.admin {
  margin-left: auto;
  text-align: right;
}

.message-content {
  padding: 0.8rem;
  border-radius: 12px;
  word-wrap: break-word;
  font-size: 0.9rem;
}

.message.user .message-content {
  background: #f1f3f4;
  color: #2c3e50;
}

.message.admin .message-content {
  background: #007bff;
  color: white;
}

.message-time {
  font-size: 0.7rem;
  color: #6c757d;
  margin-top: 0.3rem;
}

.chat-input-container {
  padding: 1rem;
  border-top: 1px solid #f1f3f4;
  display: flex;
  gap: 0.5rem;
}

.chat-input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  outline: none;
  font-size: 0.9rem;
}

.chat-input:focus {
  border-color: #007bff;
}

.send-btn {
  background: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  cursor: pointer;
  font-size: 0.8rem;
}

/* Responsive */
@media (max-width: 1200px) {
  .admin-layout {
    grid-template-columns: 200px 1fr 350px;
  }
}

@media (max-width: 992px) {
  .admin-layout {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr;
  }
  
  .sidebar {
    display: none;
  }
  
  .chat-sidebar {
    position: fixed;
    right: -100%;
    top: 0;
    width: 100%;
    height: 100vh;
    z-index: 1000;
    transition: right 0.3s ease;
  }
  
  .chat-sidebar.active {
    right: 0;
  }
}
</style>