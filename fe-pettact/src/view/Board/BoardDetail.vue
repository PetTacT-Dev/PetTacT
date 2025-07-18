<template>
  <div class="board-detail">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <h2>게시글을 불러오는 중...</h2>
    </div>

    <!-- 게시글 내용 -->
    <div v-else-if="board" class="detail-container">
      <!-- 헤더 -->
      <div class="detail-header">
        <div class="header-content">
          <div class="breadcrumb">
            <span @click="goBack" class="breadcrumb-link">커뮤니티</span>
            <span class="breadcrumb-separator">></span>
            <span @click="goToBoard" class="breadcrumb-link">{{ categoryInfo.title }}</span>
            <span class="breadcrumb-separator">></span>
            <span class="breadcrumb-current">게시글 보기</span>
          </div>
        </div>
      </div>

      <!-- 게시글 정보 -->
      <div class="board-content">
        <!-- 제목 및 메타 정보 -->
        <div class="board-header">
          <h1 class="board-title">{{ board.boardTitle }}</h1>
          <div class="board-meta">
            <div class="meta-left">
              <span class="author">작성자: {{ board.userNickname || '익명' }}</span>
              <span class="date">{{ formatDate(board.createdAt) }}</span>
              <div class="col-views">{{ board.boardViewCnt || 0 }}</div>
            </div>
            <!-- 작성자인 경우 수정/삭제 /신고 버튼 -->
            <div v-if="isAuthor" class="action-buttons">
              <button @click="editPost" class="btn btn-edit">
                ✏️ 수정하기
              </button>
              <button @click="deletePost" class="btn btn-delete">
                🗑️ 삭제하기
              </button>
              <button @click="openReportModal" class="report-btn">🚨 신고하기</button>
            </div>
          </div>
        </div>

        <!-- 게시글 내용 -->
        <div class="board-body">
          <div class="content-text">
            {{ board.boardContent }}
          </div>

          <!-- 게시글 추천 버튼 --> 
        <div class="board-recommend" v-if="categoryInfo.allowRecommend">
        <div class="recommend-count">
            👍 {{ board?.boardRecommendCount || 0 }}개
        </div>
        <button 
            @click="toggleRecommend" 
            :class="['recommend-btn', { active: isRecommended }]"
            :disabled="isRecommending"
        >
        <span v-if="!isRecommending">
        {{ isRecommended ? '👍 추천 취소' : '👍 추천하기' }}
        </span>
        <span v-else>처리중...</span>
        </button>
    </div>

    <!-- ✅ 추천 비허용 시 메시지 (선택사항) -->
    <div class="board-recommend-disabled" v-else>
        <div class="recommend-count">
        👍 {{ board?.boardRecommendCount || 0 }}개
        </div>
        <p class="disabled-message">이 게시판은 추천 기능이 비활성화되어 있습니다.</p>
    </div>

          <!-- 첨부 이미지들 -->
          <div v-if="attachedImages.length > 0" class="attached-images">
            <h3>첨부 이미지</h3>
            <div class="image-gallery">
              <div 
                v-for="(image, index) in attachedImages" 
                :key="image.fileNo"
                @click="openImageModal(index)"
                class="image-item"
              >
                <img 
                  :src="`/v1/multifile/image/${image.fileNo}`" 
                  :alt="image.fileName"
                  class="board-image"
                />
              </div>
            </div>
          </div>

          <!-- 첨부 파일들 -->
          <div v-if="attachedFiles.length > 0" class="attached-files">
            <h3>첨부 파일</h3>
            <div class="file-list">
              <div 
                v-for="file in attachedFiles" 
                :key="file.fileNo"
                class="file-item"
              >
                <div class="file-info">
                  <span class="file-icon">📎</span>
                  <span class="file-name">{{ file.fileName }}</span>
                  <span class="file-size">({{ formatFileSize(file.fileSize) }})</span>
                </div>
                <button 
                    @click="downloadFile(file.fileNo, file.fileName)"
                    class="download-btn"
                >
                  다운로드
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 댓글 섹션 -->
        <div class="reply-section">
            <h3>댓글</h3>
            <ReplyItem 
            :boardNo="board.boardNo" 
            :allowReply="board.responseDto.boardAllowReply" 
        />
        </div>
    </div>

    <!-- 오류 상태 -->
    <div v-else class="error-state">
      <h2>게시글을 찾을 수 없습니다</h2>
      <p>삭제되었거나 존재하지 않는 게시글입니다.</p>
      <button @click="goToBoard" class="btn btn-primary">목록으로 돌아가기</button>
    </div>

    <!-- 이미지 모달 -->
    <div v-if="showImageModal" @click="closeImageModal" class="image-modal">
      <div class="modal-content" @click.stop>
        <button @click="closeImageModal" class="modal-close">×</button>
        <img 
          v-if="currentImageIndex >= 0"
          :src="`/v1/multifile/image/${attachedImages[currentImageIndex].fileNo}`" 
          :alt="attachedImages[currentImageIndex].fileName"
          class="modal-image"
        />
        <div class="modal-controls">
          <button 
            @click="prevImage" 
            :disabled="currentImageIndex <= 0"
            class="nav-btn"
          >
            ‹ 이전
          </button>
          <span class="image-counter">
            {{ currentImageIndex + 1 }} / {{ attachedImages.length }}
          </span>
          <button 
            @click="nextImage" 
            :disabled="currentImageIndex >= attachedImages.length - 1"
            class="nav-btn"
          >
            다음 ›
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import ReplyItem from '@/view/reply/ReplyItem.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 게시글 관련 데이터
const board = ref(null)
const categoryInfo = ref({ title: '게시판' })
const attachedImages = ref([])
const attachedFiles = ref([])
const loading = ref(true)

// 이미지 모달 관련
const showImageModal = ref(false)
const currentImageIndex = ref(0)

// 게시글 추천 관련
const isRecommended = ref(false)
const isRecommending = ref(false)

// boardNo를 숫자로 변환
const boardNo = computed(() => route.params.boardNo)

// 작성자 권한 체크
const isAuthor = computed(() => {
  if (!board.value || !userStore.user) return false
  return board.value.userNo === userStore.user.userNo
})


const checkRecommendStatus = async () => {
    if (!categoryInfo.value.allowRecommend) {
    isRecommended.value = false
    return
  }

  if (!userStore.isLoggedIn) {
    isRecommended.value = false
    return
  }

  try {
    const response = await axios.get(`/v1/board/${boardNo.value}/recommend`)
    isRecommended.value = response.data
  } catch (error) {
    console.error('추천 상태 확인 실패:', error)
    isRecommended.value = false
  }
}

// loadBoardDetail 함수 수정
const loadBoardDetail = async () => {
  try {
    loading.value = true
    const boardNoParam = route.params.boardNo

    // 게시글 정보 조회
    const boardResponse = await axios.get(`/v1/board/${boardNoParam}`)
    console.log('전체 게시글 데이터:', boardResponse.data)
    console.log('responseDto 내용:', boardResponse.data.responseDto)
    board.value = boardResponse.data

    // responseDto에서 카테고리 정보 직접 사용
    if (boardResponse.data.responseDto) {
      const categoryData = boardResponse.data.responseDto
      
      console.log('boardAllowRecommend:', categoryData.boardAllowRecommend)
      console.log('boardCategoryNo:', categoryData.boardCategoryNo)
      
      categoryInfo.value = {
        title: categoryData.boardCategoryTitle || '게시판',
        allowRecommend: categoryData.boardAllowRecommend || false
      }
      
      console.log('설정된 categoryInfo:', categoryInfo.value)
    }

    // 첨부파일 조회
    await loadBoardFiles(boardNoParam)
    
    // 추천 상태 확인 추가
    await checkRecommendStatus()

  } catch (error) {
    console.error('게시글 로드 실패:', error)
    board.value = null
  } finally {
    loading.value = false
  }
}

// 게시글 파일 로드
const loadBoardFiles = async (boardNo) => {
  try {
    const filesResponse = await axios.get(`/v1/multifile`, {
      params: {
        referenceTable: 'BOARD',
        referenceNo: boardNo
      }
    })

    let files = []
    const responseData = filesResponse.data
    if (Array.isArray(responseData)) {
      files = responseData
    } else if (responseData && Array.isArray(responseData.data)) {
      files = responseData.data
    } else if (responseData && Array.isArray(responseData.content)) {
      files = responseData.content
    } else {
      files = []
    }
    
    // 파일 분리
    attachedImages.value = files.filter(file => 
      file && file.fileMimeType && file.fileMimeType.startsWith('image/')
    )
    attachedFiles.value = files.filter(file => 
      file && file.fileMimeType && !file.fileMimeType.startsWith('image/')
    )

  } catch (fileError) {
    console.log('파일 조회 실패 (정상 - 파일 없음):', fileError)
    attachedImages.value = []
    attachedFiles.value = []
  }
}

// 파일 다운로드 함수 추가
const downloadFile = async (fileNo, fileName) => {
  try {
    const response = await axios.get(`/v1/multifile/download/${fileNo}`, {
      responseType: 'blob'
    })
    
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
    
  } catch (error) {
    console.error('다운로드 실패:', error)
    alert('파일 다운로드에 실패했습니다.')
  }
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)
  
  if (diffMins < 1) return '방금 전'
  if (diffMins < 60) return `${diffMins}분 전`
  if (diffHours < 24) return `${diffHours}시간 전`
  if (diffDays < 7) return `${diffDays}일 전`
  
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 파일 크기 포맷
const formatFileSize = (bytes) => {
  if (!bytes) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 이미지 모달 관련
const openImageModal = (index) => {
  currentImageIndex.value = index
  showImageModal.value = true
}

const closeImageModal = () => {
  showImageModal.value = false
}

const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

const nextImage = () => {
  if (currentImageIndex.value < attachedImages.value.length - 1) {
    currentImageIndex.value++
  }
}

// 네비게이션
const goBack = () => {
  router.go(-1)
}

const goToBoard = () => {
  if (board.value && board.value.categoryNo) {
    router.push(`/category/${board.value.categoryNo}`)
  } else {
    router.push('/board')
  }
}

// 게시글 수정
const editPost = () => {
  const boardNoParam = route.params.boardNo
  router.push(`/board/${boardNoParam}/edit`)
}

// 게시글 삭제
const deletePost = async () => {
  if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
    return
  }

  try {
    const boardNoParam = route.params.boardNo
    await axios.delete(`/v1/board/${boardNoParam}`)
    
    alert('게시글이 삭제되었습니다.')
    goToBoard()
  } catch (error) {
    console.error('게시글 삭제 실패:', error)
    alert('게시글 삭제에 실패했습니다.')
  }
}

const toggleRecommend = async () => {

    if (!categoryInfo.value.allowRecommend) {
    alert('이 게시판은 추천 기능이 비활성화되어 있습니다.')
    return
  }

  const isUserLoggedIn = userStore.user && localStorage.getItem('accessToken')
  
  if (!isUserLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    return
  }

  isRecommending.value = true

  try {
    if (isRecommended.value) {
      // 추천 취소
      await axios.delete(`/v1/board/${boardNo.value}/recommend`)
      isRecommended.value = false
      // 카운트 수동 업데이트
      if (board.value.boardRecommendCount > 0) {
        board.value.boardRecommendCount--
      }
    } else {
      // 추천 하기
      await axios.post(`/v1/board/${boardNo.value}/recommend`)
      isRecommended.value = true
      // 카운트 수동 업데이트
      board.value.boardRecommendCount++
    }
  } catch (err) {
    console.error('추천 토글 실패:', err)
    alert('추천 처리 중 오류가 발생했습니다.')
  } finally {
    isRecommending.value = false
  }
}

// 초기 로드
onMounted(() => {
    console.log('=== 컴포넌트 마운트 시작 ===')
  loadBoardDetail()
})
</script>

<style scoped>
.board-detail {
  min-height: 100vh;
  background: #f8f9fa;
}

.loading, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  text-align: center;
}

.detail-header {
  background: white;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb {
  font-size: 14px;
  color: #6c757d;
}

.breadcrumb-link {
  color: #007bff;
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #0056b3;
}

.breadcrumb-separator {
  margin: 0 8px;
  color: #dee2e6;
}

.detail-container {
  max-width: 800px;
  margin: 0 auto;
}

.board-content {
  background: white;
  margin: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  overflow: hidden;
}

.board-header {
  padding: 30px;
  border-bottom: 1px solid #e9ecef;
}

.board-title {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 15px 0;
  line-height: 1.3;
}

.board-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-left {
  display: flex;
  gap: 20px;
  color: #6c757d;
  font-size: 14px;
  flex-wrap: wrap;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.board-body {
  padding: 30px;
}

.content-text {
  font-size: 16px;
  line-height: 1.7;
  color: #2c3e50;
  white-space: pre-wrap;
  margin-bottom: 30px;
}

.attached-images, .attached-files {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.attached-images h3, .attached-files h3 {
  margin-bottom: 15px;
  color: #495057;
  font-size: 18px;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.image-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.image-item:hover {
  transform: scale(1.02);
}

.board-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.file-icon {
  font-size: 18px;
}

.file-name {
  font-weight: 500;
  color: #495057;
}

.file-size {
  color: #6c757d;
  font-size: 12px;
}

.download-btn {
  background: #007bff;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 12px;
  transition: background-color 0.2s;
}

.download-btn:hover {
  background: #0056b3;
}

/* 이미지 모달 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.modal-close {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 30px;
  cursor: pointer;
  z-index: 1001;
}

.modal-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
}

.modal-controls {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  color: white;
}

.nav-btn {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-btn:hover:not(:disabled) {
  background: rgba(255,255,255,0.3);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.image-counter {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
}

/* 반응형 */
@media (max-width: 768px) {
  .board-content, .comments-section {
    margin: 10px;
    border-radius: 8px;
  }
  
  .board-header, .board-body, .comment-form-container, .comments-container {
    padding: 20px;
  }
  
  .board-title {
    font-size: 24px;
  }
  
  .board-meta {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .meta-left {
    flex-direction: column;
    gap: 5px;
  }
  
  .image-gallery {
    grid-template-columns: 1fr;
  }
  
  .file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .comments-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .comment-actions {
    align-self: flex-end;
  }
  
  .edit-actions {
    justify-content: flex-start;
  }
}
</style>