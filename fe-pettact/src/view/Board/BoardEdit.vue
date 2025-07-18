<template>
  <div class="board-edit">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <h2>게시글 정보를 불러오는 중...</h2>
    </div>

    <!-- 게시글 수정 폼 -->
    <div v-else-if="board" class="edit-container">
      <!-- 헤더 -->
      <div class="edit-header">
        <div class="header-content">
          <div class="breadcrumb">
            <span @click="goBack" class="breadcrumb-link">커뮤니티</span>
            <span class="breadcrumb-separator">></span>
            <span @click="goToBoard" class="breadcrumb-link">{{ categoryInfo.title }}</span>
            <span class="breadcrumb-separator">></span>
            <span class="breadcrumb-current">게시글 수정</span>
          </div>
        </div>
      </div>

      <!-- 수정 폼 -->
      <div class="edit-content">
        <form @submit.prevent="submitForm" class="board-form">
          <!-- 게시글 정보 -->
          <div class="form-section">
            <h3>게시글 정보</h3>

            <div class="form-group">
              <label for="boardTitle" class="required">제목</label>
              <input
                type="text"
                id="boardTitle"
                v-model="formData.boardTitle"
                placeholder="제목을 입력하세요"
                required
                maxlength="200"
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label for="boardContent" class="required">내용</label>
              <textarea
                id="boardContent"
                v-model="formData.boardContent"
                placeholder="내용을 입력하세요"
                rows="15"
                required
                class="form-textarea"
              ></textarea>
            </div>
          </div>

          <!-- 현재 이미지 -->
          <div v-if="currentImages.length > 0" class="form-section">
            <h3>현재 이미지</h3>
            <div class="current-images">
              <div
                v-for="image in currentImages"
                :key="image.fileNo"
                class="current-image-item"
              >
                <img
                  :src="`/v1/multifile/image/${image.fileNo}`"
                  :alt="image.fileName"
                  class="current-image"
                />
                <button
                  type="button"
                  @click="removeCurrentImage(image.fileNo)"
                  class="remove-image-btn"
                >
                  ×
                </button>
                <p class="image-name">{{ image.fileName }}</p>
              </div>
            </div>
          </div>

          <!-- 새 이미지 업로드 -->
          <div v-if="categoryInfo.allowImages" class="form-section">
            <h3>새 이미지 추가</h3>
            <div class="image-upload-area">
              <input
                type="file"
                id="imageInput"
                @change="handleImageSelect"
                accept="image/*"
                multiple
                class="file-input"
              />
              <label for="imageInput" class="file-label">
                <div class="upload-content">
                  <span class="upload-icon">📷</span>
                  <p>이미지 선택 (최대 {{ categoryInfo.maxImageCount }}개)</p>
                  <p class="upload-hint">JPG, PNG, GIF 파일만 가능</p>
                </div>
              </label>
            </div>

            <div v-if="newImages.length > 0" class="new-images-preview">
              <h4>추가할 이미지</h4>
              <div class="image-preview-grid">
                <div
                  v-for="(image, index) in newImages"
                  :key="index"
                  class="image-preview-item"
                >
                  <img :src="image.preview" :alt="image.file.name" class="preview-image" />
                  <button
                    type="button"
                    @click="removeNewImage(index)"
                    class="remove-preview-btn"
                  >
                    ×
                  </button>
                  <p class="preview-name">{{ image.file.name }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 현재 첨부파일 -->
          <div v-if="currentFiles.length > 0" class="form-section">
            <h3>현재 첨부파일</h3>
            <div class="current-files">
              <div
                v-for="file in currentFiles"
                :key="file.fileNo"
                class="current-file-item"
              >
                <div class="file-info">
                  <span class="file-icon">📎</span>
                  <span class="file-name">{{ file.fileName }}</span>
                  <span class="file-size">({{ formatFileSize(file.fileSize) }})</span>
                </div>
                <button
                  type="button"
                  @click="removeCurrentFile(file.fileNo)"
                  class="remove-file-btn"
                >
                  삭제
                </button>
              </div>
            </div>
          </div>

          <!-- 새 파일 업로드 -->
          <div v-if="categoryInfo.allowAttachments" class="form-section">
            <h3>새 파일 첨부</h3>
            <div class="file-upload-area">
              <input
                type="file"
                id="fileInput"
                @change="handleFileSelect"
                multiple
                class="file-input"
              />
              <label for="fileInput" class="file-label">
                <div class="upload-content">
                  <span class="upload-icon">📎</span>
                  <p>파일 선택</p>
                  <p class="upload-hint">최대 {{ categoryInfo.maxFileSize }}MB</p>
                </div>
              </label>
            </div>

            <div v-if="newFiles.length > 0" class="new-files-preview">
              <h4>추가할 파일</h4>
              <div class="file-preview-list">
                <div
                  v-for="(file, idx) in newFiles"
                  :key="`new-file-${idx}`"
                  class="file-preview-item"
                >
                  <div class="file-info">
                    <span class="file-icon">📎</span>
                    <span class="file-name">{{ file.name }}</span>
                    <span class="file-size">({{ formatFileSize(file.size) }})</span>
                  </div>
                  <button
                    type="button"
                    @click="removeNewFile(idx)"
                    class="remove-preview-btn"
                  >
                    삭제
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 버튼 영역 -->
          <div class="form-actions">
            <button
              type="button"
              @click="goBack"
              class="btn btn-secondary"
              :disabled="isSubmitting"
            >
              취소
            </button>
            <button
              type="submit"
              class="btn btn-primary"
              :disabled="!isFormValid || isSubmitting"
            >
              <span v-if="isSubmitting">수정 중...</span>
              <span v-else>게시글 수정</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 오류 상태 -->
    <div v-else class="error-state">
      <h2>게시글을 찾을 수 없습니다</h2>
      <p>삭제되었거나 존재하지 않는 게시글입니다.</p>
      <button @click="goBack" class="btn btn-primary">목록으로 돌아가기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const boardNo = route.params.boardNo
const board = ref(null)
const loading = ref(true)
const isSubmitting = ref(false)

const formData = ref({
  boardTitle: '',
  boardContent: '',
})

// 새로 추가할 파일들
const newImages = ref([])
const newFiles = ref([])

// 삭제할 파일 ID들
const deletedFileIds = ref([])

// 현재 표시되는 파일들 (기존 파일 - 삭제된 파일)
const currentImages = ref([])
const currentFiles = ref([])

const categoryInfo = ref({
  allowImages: true,
  allowAttachments: true,
  maxImageCount: 5,
  maxFileSize: 10, // MB
  title: '게시판'
})

const isFormValid = computed(() => {
  return formData.value.boardTitle.trim() && formData.value.boardContent.trim()
})

const goBack = () => router.go(-1)
const goToBoard = () => {
  if (board.value && board.value.categoryNo) {
    router.push(`/category/${board.value.categoryNo}`)
  } else {
    router.push('/board')
  }
}

// 파일 크기 포맷 함수
const formatFileSize = (bytes) => {
  if (!bytes) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const fetchData = async () => {
  loading.value = true
  try {
    // 게시글 조회
    const boardResponse = await axios.get(`/v1/board/${boardNo}`)
    board.value = boardResponse.data

    // 폼 데이터 채우기
    formData.value.boardTitle = board.value.boardTitle
    formData.value.boardContent = board.value.boardContent

    // ✅ categoryNo를 유연하게 추출
    const categoryNo = board.value.categoryNo || board.value.responseDto?.boardCategoryNo
    console.log('✅ 추출된 categoryNo:', categoryNo)

    // ✅ 카테고리 정보 API 호출
    if (categoryNo) {
      const categoryResponse = await axios.get(`/v1/board-categories/${categoryNo}`)
      console.log('✅ 카테고리 응답:', categoryResponse.data)

      // ✅ DB 값으로 덮어쓰기
      categoryInfo.value = {
        title: categoryResponse.data.boardCategoryTitle || '게시판',
        allowImages: categoryResponse.data.boardAllowImage,
        allowAttachments: categoryResponse.data.boardAllowAttachment,
        maxImageCount: categoryResponse.data.boardMaxImageCount,
        maxFileSize: categoryResponse.data.boardMaxFileSize
      }

      console.log('✅ 최종 categoryInfo:', categoryInfo.value)
    } else {
      console.warn('⚠️ categoryNo가 없습니다. categoryInfo 세팅되지 않음')
    }

    // 첨부파일 조회
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
      } else if (Array.isArray(responseData?.data)) {
        files = responseData.data
      } else if (Array.isArray(responseData?.content)) {
        files = responseData.content
      }

      currentImages.value = files.filter(file =>
        file?.fileMimeType?.startsWith('image/')
      )
      currentFiles.value = files.filter(file =>
        file?.fileMimeType && !file.fileMimeType.startsWith('image/')
      )
    } catch (fileError) {
      console.warn('파일 조회 실패 (정상일 수 있음):', fileError)
      currentImages.value = []
      currentFiles.value = []
    }
  } catch (error) {
    console.error('게시글 조회 실패:', error)
    board.value = null
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)

// 이미지 선택 핸들러
const handleImageSelect = (e) => {
  const files = Array.from(e.target.files)
  
  // 최대 개수 체크
  const totalImages = currentImages.value.length + newImages.value.length + files.length
  if (totalImages > categoryInfo.value.maxImageCount) {
    alert(`이미지는 최대 ${categoryInfo.value.maxImageCount}개까지 업로드 가능합니다.`)
    e.target.value = ''
    return
  }
  
  files.forEach((file) => {
    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드 가능합니다.')
      return
    }
    
    const reader = new FileReader()
    reader.onload = (event) => {
      newImages.value.push({ file, preview: event.target.result })
    }
    reader.readAsDataURL(file)
  })
  e.target.value = ''
}

// 파일 선택 핸들러
const handleFileSelect = (e) => {
  const files = Array.from(e.target.files)
  
  files.forEach((file) => {
    // 파일 크기 체크
    if (file.size > categoryInfo.value.maxFileSize * 1024 * 1024) {
      alert(`파일 크기는 ${categoryInfo.value.maxFileSize}MB를 초과할 수 없습니다.`)
      return
    }
    newFiles.value.push(file)
  })
  e.target.value = ''
}

// 새 이미지 제거
const removeNewImage = (index) => {
  newImages.value.splice(index, 1)
}

// 새 파일 제거
const removeNewFile = (index) => {
  newFiles.value.splice(index, 1)
}

// 기존 이미지 삭제
const removeCurrentImage = (fileNo) => {
  if (!confirm('이 이미지를 삭제하시겠습니까?')) return
  
  const index = currentImages.value.findIndex(img => img.fileNo === fileNo)
  if (index !== -1) {
    deletedFileIds.value.push(fileNo)
    currentImages.value.splice(index, 1)
    console.log("삭제된 파일 ID 목록:", JSON.stringify(deletedFileIds.value))
  }
}

// 기존 파일 삭제
const removeCurrentFile = (fileNo) => {
  if (!confirm('이 파일을 삭제하시겠습니까?')) return
  
  const index = currentFiles.value.findIndex(file => file.fileNo === fileNo)
  if (index !== -1) {
    deletedFileIds.value.push(fileNo)
    currentFiles.value.splice(index, 1)
  }
}

const submitForm = async () => {
  if (!isFormValid.value || isSubmitting.value) return
  
  isSubmitting.value = true

  try {
    const formDataObj = new FormData()
    
    const categoryNo = board.value?.responseDto?.boardCategoryNo
    console.log('categoryNo from board:', categoryNo)
    
    // BoardCreateDto를 JSON으로 전송
    const boardData = {
      boardTitle: formData.value.boardTitle,
      boardContent: formData.value.boardContent
    }
    formDataObj.append('data', new Blob([JSON.stringify(boardData)], { type: 'application/json' }))
    
    const allNewFiles = [...newImages.value.map(img => img.file), ...newFiles.value]
    allNewFiles.forEach(file => {
      formDataObj.append('files', file)
    })

    if (deletedFileIds.value.length > 0) {
      deletedFileIds.value.forEach(id => {
        formDataObj.append('deletedFileIds', id)
      })
    }

    console.log("삭제된 파일 ID 목록:", deletedFileIds.value);
    
    await axios.put(`/v1/board/${boardNo}`, formDataObj, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (categoryNo) {
      router.push(`/board/${categoryNo}`)  // 카테고리 목록으로
    } else {
      router.push(`/board/${boardNo}`)     // 게시글 상세로
    }
    
  } catch (error) {
    console.error('게시글 수정 실패:', error)
    alert('게시글 수정 중 오류가 발생했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

</script>

<style scoped>
.board-edit {
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

.edit-header {
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

.edit-container {
  max-width: 800px;
  margin: 0 auto;
}

.edit-content {
  background: white;
  margin: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  overflow: hidden;
}

.board-form {
  padding: 30px;
}

.form-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e9ecef;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.form-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #495057;
}

.required::after {
  content: ' *';
  color: #dc3545;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #007bff;
}

.form-textarea {
  resize: vertical;
  min-height: 300px;
  font-family: inherit;
}

/* 현재 이미지 */
.current-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
}

.current-image-item {
  position: relative;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.current-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 25px;
  height: 25px;
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-name {
  padding: 8px;
  font-size: 12px;
  color: #6c757d;
  text-align: center;
  margin: 0;
  background: #f8f9fa;
}

/* 현재 파일 */
.current-files {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.current-file-item {
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

.remove-file-btn {
  background: #dc3545;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.remove-file-btn:hover {
  background: #c82333;
}

/* 업로드 영역 */
.image-upload-area, .file-upload-area {
  margin-bottom: 20px;
}

.file-input {
  display: none;
}

.file-label {
  display: block;
  padding: 40px 20px;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.file-label:hover {
  border-color: #007bff;
  background: #f8f9fa;
}

.upload-content {
  color: #6c757d;
}

.upload-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 10px;
}

.upload-hint {
  font-size: 12px;
  margin-top: 5px;
}

/* 새 이미지 미리보기 */
.new-images-preview, .new-files-preview {
  margin-top: 20px;
}

.new-images-preview h4, .new-files-preview h4 {
  margin-bottom: 15px;
  color: #495057;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
}

.image-preview-item {
  position: relative;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.remove-preview-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 25px;
  height: 25px;
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-name {
  padding: 8px;
  font-size: 12px;
  color: #6c757d;
  text-align: center;
  margin: 0;
  background: #f8f9fa;
}

/* 새 파일 미리보기 */
.file-preview-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.file-preview-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #e3f2fd;
  border-radius: 8px;
  border: 1px solid #bbdefb;
}

.file-preview-item .remove-preview-btn {
  position: static;
  width: auto;
  height: auto;
  background: #dc3545;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

/* 버튼 */
.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #545b62;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 반응형 */
@media (max-width: 768px) {
  .edit-content {
    margin: 10px;
    border-radius: 8px;
  }
  
  .board-form {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .current-images,
  .image-preview-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }
}
</style>