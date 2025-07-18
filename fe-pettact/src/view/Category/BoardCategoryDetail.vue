<template>
  <div>
    <!-- 카테고리 상세 정보 (읽기 전용) -->
    <div v-if="categoryDetail" class="board-category-form">
      <div class="form-header">
        <h2>게시판 카테고리 상세 정보</h2>
        <p>{{ categoryDetail.boardCategoryTitle }} 게시판의 설정 정보입니다.</p>
      </div>

      <div class="form-container">
        <!-- 기본 정보 -->
        <div class="form-section">
          <h3>기본 정보</h3>
          
          <div class="form-group">
            <label>카테고리 제목</label>
            <div class="detail-value">{{ categoryDetail.boardCategoryTitle || '-' }}</div>
          </div>

          <div class="form-group">
            <label>카테고리 설명</label>
            <div class="detail-value">{{ categoryDetail.boardCategoryDescription || '-' }}</div>
          </div>

          <div class="form-group">
            <label>허용 권한</label>
            <div class="detail-value">
              <span class="badge" :class="categoryDetail.boardAllowedRole === 'ROLE_ADMIN' ? 'admin' : 'user'">
                {{ categoryDetail.boardAllowedRole === 'ROLE_ADMIN' ? '관리자' : '일반 사용자' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 파일 설정 -->
        <div class="form-section">
          <h3>파일 설정</h3>
          
          <div class="form-group">
            <label>이미지 업로드</label>
            <div class="detail-value">
              <span class="status" :class="categoryDetail.boardAllowImage ? 'enabled' : 'disabled'">
                {{ categoryDetail.boardAllowImage ? '허용' : '비허용' }}
              </span>
            </div>
          </div>

          <div v-if="categoryDetail.boardAllowImage" class="form-group">
            <label>최대 이미지 개수</label>
            <div class="detail-value">{{ categoryDetail.boardMaxImageCount }}개</div>
          </div>

          <div class="form-group">
            <label>첨부파일 업로드</label>
            <div class="detail-value">
              <span class="status" :class="categoryDetail.boardAllowAttachment ? 'enabled' : 'disabled'">
                {{ categoryDetail.boardAllowAttachment ? '허용' : '비허용' }}
              </span>
            </div>
          </div>

          <div class="form-group">
            <label>최대 파일 크기</label>
            <div class="detail-value">{{ categoryDetail.boardMaxFileSize }}MB</div>
          </div>
        </div>

        <!-- 게시판 설정 -->
        <div class="form-section">
          <h3>게시판 설정</h3>
          
          <div class="form-group">
            <label>에디터 타입</label>
            <div class="detail-value">
              <span class="editor-type">{{ getEditorTypeName(categoryDetail.editorType) }}</span>
            </div>
          </div>

          <div class="form-group">
            <label>댓글 기능</label>
            <div class="detail-value">
              <span class="status" :class="categoryDetail.boardAllowReply ? 'enabled' : 'disabled'">
                {{ categoryDetail.boardAllowReply ? '허용' : '비허용' }}
              </span>
            </div>
          </div>

          <div class="form-group">
            <label>추천 기능</label>
            <div class="detail-value">
              <span class="status" :class="categoryDetail.boardAllowRecommend ? 'enabled' : 'disabled'">
                {{ categoryDetail.boardAllowRecommend ? '허용' : '비허용' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 통계 정보 -->
        <div class="form-section">
          <h3>통계 정보</h3>
          
          <div class="form-group">
            <label>총 게시글 수</label>
            <div class="detail-value">{{ categoryDetail.totalBoards || 0 }}개</div>
          </div>

          <div class="form-group">
            <label>생성일</label>
            <div class="detail-value">{{ formatDate(categoryDetail.createdAt) }}</div>
          </div>
        </div>
      </div>

      <!-- 액션 버튼 -->
      <div class="form-actions">
        <button @click="goBack" class="btn btn-secondary">
          목록으로
        </button>
        <button @click="goToEdit" class="btn btn-primary" v-if="isAdmin">
          수정하기
        </button>
        <button @click="goToDelete" class="btn btn-primary" v-if="isAdmin">
          삭제하기
        </button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const categoryDetail = ref(null)


const getCategoryDetail = async () => {
  try {
    const categoryNo = route.params.categoryNo
    const res = await axios.get(`/v1/board-categories/${categoryNo}`)
    console.log('📡 API 응답 전체:', res)
    console.log('📄 응답 데이터:', res.data)
    categoryDetail.value = res.data
    console.log('📄 categoryDetail.value 설정 후:', categoryDetail.value)
    console.log('📄 categoryDetail.value.boardAllowedRole:', categoryDetail.value.boardAllowedRole)
  } catch (err) {
    console.error('카테고리 정보 조회 실패:', err)
    alert('카테고리 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

const goBack = () => {
  router.push({ name: 'BoardCategoryList' })
}

const goToEdit = () => {
  console.log('🔥 goToEdit 함수 실행됨!')
  console.log('🔥 현재 categoryNo:', route.params.categoryNo)
  console.log('🔥 라우터 푸시 시도...')
  
  try {
    router.push({ name: 'BoardCategoryEdit', params: { categoryNo: route.params.categoryNo } })
    console.log('🔥 라우터 푸시 성공!')
  } catch (error) {
    console.error('🔥 라우터 푸시 실패:', error)
  }
}

const goToDelete = async () => {
  // 확인 다이얼로그
  if (!confirm('정말로 이 게시판을 삭제하시겠습니까?\n삭제된 게시판은 복구할 수 없습니다.')) {
    return
  }
  
  try {
    console.log('🔥 삭제 요청 시작:', route.params.categoryNo)
    
    // DELETE API 호출
    await axios.delete(`/v1/board-categories/${route.params.categoryNo}`)
    
    alert('게시판이 성공적으로 삭제되었습니다.')
    console.log('🔥 삭제 성공')
    
    // 삭제 성공 후 목록 페이지로 이동
    router.push({ name: 'BoardCategoryList' })
    
  } catch (error) {
    console.error('🔥 삭제 실패:', error)
    
    if (error.response?.status === 400) {
      alert('게시글이 있는 게시판은 삭제할 수 없습니다.')
    } else if (error.response?.status === 403) {
      alert('삭제 권한이 없습니다.')
    } else {
      alert('삭제 중 오류가 발생했습니다.')
    }
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').split('.')[0]
}

const getEditorTypeName = (type) => {
  const types = {
    'MARKDOWN': '마크다운',
    'RICH_TEXT': '리치텍스',
    'PLAIN_TEXT': '플레인 텍스트'
  }
  return types[type] || type
}

const isAdmin = computed(() => {
  console.log('🔥 현재 사용자:', userStore.user)
  console.log('🔥 사용자 권한:', userStore.user?.userRole)
  console.log('🔥 isAdmin 결과:', userStore.user?.userRole === 'ROLE_ADMIN')
  return userStore.user?.userRole === 'ROLE_ADMIN'
})

onMounted(() => {
  getCategoryDetail()
})
</script>

<style scoped>
.board-category-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.form-header h2 {
  color: #333;
  font-size: 24px;
  margin: 0 0 10px 0;
}

.form-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.form-section {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
}

.form-section h3 {
  color: #333;
  font-size: 18px;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.required {
  color: #e74c3c;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-bottom: 0;
}

.form-checkbox {
  width: auto;
  margin-right: 10px;
  transform: scale(1.2);
}

.checkbox-text {
  color: #333;
  font-size: 14px;
}

.result-message {
  padding: 15px;
  border-radius: 4px;
  text-align: center;
  font-weight: 500;
  margin: 20px 0;
}

.result-message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.result-message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding: 20px 0;
  border-top: 1px solid #e0e0e0;
}

.badge {
  display: inline-block !important;
  padding: 8px 12px !important;
  border-radius: 4px !important;
  font-size: 14px !important;
  font-weight: bold !important;
  color: white !important;
}

.badge.admin {
  background-color: #e74c3c !important;
}

.badge.user {
  background-color: #3498db !important;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.btn-outline {
  background-color: transparent;
  color: #3498db;
  border: 1px solid #3498db;
}

.btn-outline:hover {
  background-color: #3498db;
  color: white;
}

@media (max-width: 600px) {
  .board-category-form {
    padding: 15px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>