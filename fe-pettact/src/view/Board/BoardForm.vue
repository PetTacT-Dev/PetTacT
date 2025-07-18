<template>
  <div class="board-form">
    <!-- 헤더 -->
    <div v-if="!categoryInfo || !categoryInfo.title">
      <h1>로딩 중...</h1>
      <p>카테고리 정보를 불러오는 중입니다.</p>
    </div>

    <div v-else>
      <div class="form-header">
        <div class="header-content">
          <div class="breadcrumb">
            <span @click="goBack" class="breadcrumb-link">커뮤니티</span>
            <span class="breadcrumb-separator">></span>
            <span @click="goToBoard" class="breadcrumb-link">{{ categoryInfo.title }}</span>
            <span class="breadcrumb-separator">></span>
            <span class="breadcrumb-current">글쓰기</span>
          </div>
          <h1>{{ categoryInfo.title }}</h1>
        </div>
      </div>
    </div>

    <!-- 폼 영역 -->
    <div class="form-container">
      <form @submit.prevent="submitForm">
        <!-- 제목 입력 -->
        <div class="form-group">
          <label for="title" class="form-label">제목 *</label>
          <input
            id="title"
            v-model="formData.boardTitle"
            type="text"
            class="form-input"
            placeholder="제목을 입력하세요"
            maxlength="100"
            required
          />
          <div class="char-count">{{ formData.boardTitle.length }}/100</div>
        </div>

        <!-- 내용 입력 -->
        <div class="form-group">
          <label for="content" class="form-label">내용 *</label>
          <textarea
            id="content"
            v-model="formData.boardContent"
            class="form-textarea"
            placeholder="내용을 입력하세요"
            rows="15"
            required
          ></textarea>
        </div>

        <!-- 이미지 업로드 -->
        <div v-if="categoryInfo.allowImages" class="form-group">
          <label class="form-label">이미지 첨부</label>
          <div class="image-upload-area">
            <input
              ref="imageInput"
              type="file"
              multiple
              accept="image/*"
              @change="handleImageUpload"
              class="file-input"
            />
            <div class="upload-button" @click="$refs.imageInput.click()">
              <span class="upload-icon">📷</span>
              <span>이미지 선택 (최대 {{ categoryInfo.maxImageCount }}개)</span>
            </div>
          </div>

          <!-- 이미지 미리보기 -->
          <div v-if="selectedImages.length > 0" class="image-preview">
            <div
              v-for="(image, index) in selectedImages"
              :key="index"
              class="preview-item"
            >
              <img :src="image.url" :alt="`Preview ${index + 1}`" />
              <button type="button" @click="removeImage(index)" class="remove-btn">×</button>
            </div>
            <button type="button" @click="removeAllImages" class="remove-all-btn">
              선택한 이미지 전체 삭제
            </button>
          </div>
        </div>

        <!-- 파일 업로드 -->
        <div v-if="categoryInfo.allowAttachments" class="form-group">
          <label class="form-label">파일 첨부</label>
          <div class="file-upload-area">
            <input
              ref="fileInput"
              type="file"
              multiple
              @change="handleFileUpload"
              class="file-input"
            />
            <div class="upload-button" @click="$refs.fileInput.click()">
              <span class="upload-icon">📎</span>
              <span>파일 선택 (최대 {{ categoryInfo.maxFileSize }}MB)</span>
            </div>
          </div>

          <!-- 파일 목록 -->
          <div v-if="selectedFiles.length > 0" class="file-list">
            <div
              v-for="(file, index) in selectedFiles"
              :key="index"
              class="file-item"
            >
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
              <button type="button" @click="removeFile(index)" class="remove-btn">×</button>
            </div>
            <button type="button" @click="removeAllFiles" class="remove-all-btn">
              선택한 파일 전체 삭제
            </button>
          </div>
        </div>

        <!-- 폼 버튼 -->
        <div class="form-actions">
          <button type="button" @click="goBack" class="btn btn-cancel">취소</button>
          <button type="submit" :disabled="!isFormValid || isSubmitting" class="btn btn-submit">
            {{ isSubmitting ? '등록 중...' : '등록하기' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const imageInput = ref(null)
const fileInput = ref(null)

const categoryInfo = ref({
  title: '',
  allowImages: false,
  allowAttachments: false,
  maxImageCount: 5,
  maxFileSize: 50
})

const formData = ref({
  boardTitle: '',
  boardContent: ''
})

const selectedImages = ref([])
const selectedFiles = ref([])
const isSubmitting = ref(false)

const isFormValid = computed(() => {
  return formData.value.boardTitle.trim().length > 0 && 
         formData.value.boardContent.trim().length > 0
})


// const handleImageUpload = (event) => {
//   const files = Array.from(event.target.files)
//   const maxCount = boardConfig.value.boardMaxImageCount

//   if (selectedImages.value.length + files.length > maxCount) {
//     alert(`이미지는 최대 ${maxCount}장까지만 업로드할 수 있습니다.`)
//     return
//   }

//   files.forEach(file => {
//     if (file.type.startsWith('image/')) {
//       const url = URL.createObjectURL(file)
//       selectedImages.value.push({ file, url })
//     }
//   })
//   event.target.value = ''
// }

const handleImageUpload = (event) => {
  const files = Array.from(event.target.files)
  const maxCount = categoryInfo.value.maxImageCount

  for (const file of files) {
    if (!file.type.startsWith('image/')) continue
    if (selectedImages.value.length >= maxCount) {
      alert(`이미지는 최대 ${maxCount}장까지만 업로드할 수 있습니다.`)
      break
    }

    const url = URL.createObjectURL(file)
    selectedImages.value.push({ file, url })
  }

  // 파일 input 초기화 (같은 파일 다시 업로드 허용)
  event.target.value = ''
}

const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  files.forEach(file => {
    selectedFiles.value.push(file)
  })
  event.target.value = ''
}

const removeImage = (index) => {
  URL.revokeObjectURL(selectedImages.value[index].url)
  selectedImages.value.splice(index, 1)
}

const removeFile = (index) => {
  selectedFiles.value.splice(index, 1)
}

// 전체 이미지 삭제
const removeAllImages = () => {
  selectedImages.value = []
  if (imageInput.value) {
    imageInput.value.value = null
  }
}

// 전체 파일 삭제
const removeAllFiles = () => {
  selectedFiles.value = []
  if (fileInput.value) {
    fileInput.value.value = null
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const submitForm = async () => {
  if (!isFormValid.value || isSubmitting.value) return
  
  try {
    isSubmitting.value = true
    
    // ✅ categoryNo 변수 선언
    const categoryNo = route.params.categoryNo
    
    const submitData = new FormData()
    
    const boardData = {
      boardTitle: formData.value.boardTitle.trim(),
      boardContent: formData.value.boardContent.trim(),
      boardCategoryNo: parseInt(categoryNo)
    }
    
    submitData.append('data', new Blob([JSON.stringify(boardData)], {
      type: 'application/json'
    }))
    
    // 파일 처리...
    if (selectedImages.value && selectedImages.value.length > 0) {
      selectedImages.value.forEach((image) => {
        submitData.append('files', image.file)
      })
    }
    
    if (selectedFiles.value && selectedFiles.value.length > 0) {
      selectedFiles.value.forEach((file) => {
        submitData.append('files', file)
      })
    }
    
    const response = await axios.post('/v1/board', submitData)
    
    alert('게시글이 성공적으로 등록되었습니다.')
    router.push(`/board/${categoryNo}`)
    
  } catch (error) {
    console.error('게시글 등록 실패:', error)
    alert('게시글 등록에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

const loadCategoryInfo = async () => {
  try {
    const categoryNo = route.params.categoryNo
    const response = await axios.get(`/v1/board-categories/${categoryNo}`)
    
    categoryInfo.value = {
      title: response.data.boardCategoryTitle || '게시판',
      allowImages: response.data.boardAllowImage,         
      allowAttachments: response.data.boardAllowAttachment,
      maxImageCount: response.data.boardMaxImageCount,
      maxFileSize: response.data.boardMaxFileSize
    }

    console.log('백엔드에서 받은 값:', {
    boardMaxImageCount: response.data.boardMaxImageCount,
    boardMaxFileCount: response.data.boardMaxFileCount
})

  } catch (error) {
    console.error('에러:', error)
    categoryInfo.value = {
      title: '게시판',
      allowImages: true,
      allowAttachments: true,
      maxImageCount: 5,
      maxFileSize: 50
    }
  }
}

const goBack = () => {
  router.push('/board')
}

const goToBoard = () => {
  router.push('/board')
}

onMounted(() => {
  loadCategoryInfo()
})
</script>

<style scoped>
.board-form {
  min-height: 100vh;
  background: #f8f9fa;
}

.form-header {
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
  margin-bottom: 15px;
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

.form-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
}

.form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px 20px;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #495057;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.form-textarea {
  width: 100%;
  padding: 15px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  font-size: 16px;
  font-family: inherit;
  resize: vertical;
  min-height: 300px;
  transition: border-color 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6c757d;
  margin-top: 5px;
}

.file-input {
  display: none;
}

.image-upload-area,
.file-upload-area {
  margin-bottom: 15px;
}

.upload-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 20px;
  border: 2px dashed #ced4da;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: #f8f9fa;
}

.upload-button:hover {
  border-color: #007bff;
  background: #e3f2fd;
}

.upload-icon {
  font-size: 24px;
}

.image-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.preview-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: #f8f9fa;
}

.preview-item img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.remove-all-btn {
  margin-top: 10px;
  background-color: #eee;
  border: none;
  padding: 6px 12px;
  cursor: pointer;
  font-size: 14px;
  border-radius: 4px;
}

.remove-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 25px;
  height: 25px;
  background: rgba(0,0,0,0.7);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-list {
  margin-top: 15px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 8px;
  position: relative;
}

.file-name {
  flex: 1;
  font-weight: 500;
}

.file-size {
  color: #6c757d;
  font-size: 12px;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 40px;
}

.btn {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 120px;
}

.btn-cancel {
  background: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background: #5a6268;
}

.btn-submit {
  background: #007bff;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #0056b3;
}

.btn-submit:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

/* 반응형 */
@media (max-width: 768px) {
  .header-content,
  .form-container {
    padding: 20px 15px;
  }
  
  .form-header h1 {
    font-size: 24px;
  }
  
  .image-preview {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>