<template>
<div>
   <section class="sub-nav-section">
      <div class="sub-nav-container">
        <h2 class="sub-nav-title">커뮤니티</h2>
        <div class="sub-nav-menu">
          <div v-for="category in boardCategories" 
               :key="category.boardCategoryNo"
               class="sub-nav-item">
            {{ category.boardCategoryTitle }}
          </div>
        </div>
      </div>
    </section>
   <div class="board-category-form">
    <div class="form-header">
      <h2>게시판 카테고리 생성</h2>
      <p>새로운 게시판 카테고리를 생성합니다.</p>
    </div>

    <form @submit.prevent="submitForm" class="form-container">
      <div class="form-section">
        <h3>기본 정보</h3>
        
        <div class="form-group">
          <label for="boardCategoryTitle">카테고리 제목 <span class="required">*</span></label>
          <input 
            id="boardCategoryTitle"
            v-model="formData.boardCategoryTitle"
            type="text"
            placeholder="카테고리 제목을 입력하세요"
            class="form-input"
            required
          />
        </div>

        <div class="form-group">
          <label for="boardCategoryDescription">카테고리 설명</label>
          <textarea
            id="boardCategoryDescription"
            v-model="formData.boardCategoryDescription"
            placeholder="카테고리 설명을 입력하세요"
            class="form-textarea"
            rows="4"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="boardAllowedRole">허용 권한</label>
          <select
            id="boardAllowedRole"
            v-model="formData.boardAllowedRole"
            class="form-select"
          >
            <option value="ROLE_USER">일반 사용자</option>
            <option value="ROLE_ADMIN">관리자</option>
          </select>
        </div>
      </div>

      <div class="form-section">
        <h3>파일 설정</h3>
        
        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="formData.boardAllowImage"
              class="form-checkbox"
            />
            <span class="checkbox-text">이미지 업로드 허용</span>
          </label>
        </div>

        <div v-if="formData.boardAllowImage" class="form-group">
          <label for="boardMaxImageCount">최대 이미지 개수</label>
          <input
            id="boardMaxImageCount"
            v-model.number="formData.boardMaxImageCount"
            type="number"
            min="1"
            max="20"
            class="form-input"
          />
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="formData.boardAllowAttachment"
              class="form-checkbox"
            />
            <span class="checkbox-text">첨부파일 업로드 허용</span>
          </label>
        </div>

        <div class="form-group">
          <label for="boardMaxFileSize">최대 파일 크기 (MB)</label>
          <input
            id="boardMaxFileSize"
            v-model.number="formData.boardMaxFileSize"
            type="number"
            min="1"
            max="100"
            class="form-input"
          />
        </div>
      </div>

      <div class="form-section">
        <h3>게시판 설정</h3>
        
        <div class="form-group">
          <label for="editorType">에디터 타입</label>
          <select
            id="editorType"
            v-model="formData.editorType"
            class="form-select"
          >
            <option value="RICH_TEXT">리치 텍스트 에디터</option>
            <option value="MARKDOWN">마크다운 에디터</option>
            <option value="PLAIN_TEXT">일반 텍스트</option>
          </select>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="formData.boardAllowReply"
              class="form-checkbox"
            />
            <span class="checkbox-text">댓글 허용</span>
          </label>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="formData.boardAllowRecommend"
              class="form-checkbox"
            />
            <span class="checkbox-text">추천 기능 허용</span>
          </label>
        </div>
      </div>

      <div v-if="submitResult" class="result-message" :class="{ 'success': submitResult.success, 'error': !submitResult.success }">
        {{ submitResult.message }}
      </div>

      <div class="form-actions">
        <button type="button" @click="cancelForm" class="btn btn-secondary">
          취소
        </button>
        <button type="button" @click="resetForm" class="btn btn-outline">
          초기화
        </button>
        <button type="submit" :disabled="isSubmitting" class="btn btn-primary">
          <span v-if="isSubmitting">생성 중…</span>
          <span v-else>카테고리 생성</span>
        </button>
      </div>
    </form>
  </div>
</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

const boardCategories = ref([])

const fetchBoardCategories = async () => {
  try {
    const response = await axios.get('/v1/board-categories')
    boardCategories.value = response.data || []
  } catch (error) {
    console.error('카테고리 조회 실패:', error)
  }
}

const formData = ref({
  boardCategoryTitle: '',
  boardCategoryDescription: '',
  boardAllowedRole: 'ROLE_ADMIN',
  boardAllowImage: false,
  boardAllowAttachment: false,
  boardMaxFileSize: 10,
  boardAllowReply: true,
  boardMaxImageCount: 5,
  editorType: 'RICH_TEXT',
  boardAllowRecommend: true
});

const isSubmitting = ref(false);
const submitResult = ref(null);

const validateForm = () => {
  if (!formData.value.boardCategoryTitle.trim()) {
    submitResult.value = { 
      success: false, 
      message: '카테고리 제목을 입력해주세요.' 
    };
    return false;
  }

  if (formData.value.boardMaxFileSize <= 0 || formData.value.boardMaxFileSize > 100) {
    submitResult.value = { 
      success: false, 
      message: '파일 크기는 1MB에서 100MB 사이여야 합니다.' 
    };
    return false;
  }

  if (formData.value.boardAllowImage && (formData.value.boardMaxImageCount <= 0 || formData.value.boardMaxImageCount > 20)) {
    submitResult.value = { 
      success: false, 
      message: '이미지 개수는 1개에서 20개 사이여야 합니다.' 
    };
    return false;
  }

  return true;
};

const submitForm = () => {
  if (!validateForm()) return;

  isSubmitting.value = true;
  submitResult.value = null;

  axios.post('/v1/board-categories', formData.value).then(() => {
    submitResult.value = { 
      success: true, 
      message: '게시판 카테고리가 성공적으로 생성되었습니다!' 
    };
    setTimeout(() => {
      router.push('/boardCategoryList');
    }, 2000);
  }).catch(error => {
    console.error('생성 실패:', error);
    let errorMessage = '게시판 카테고리 생성에 실패했습니다.';
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message;
    }
    submitResult.value = { 
      success: false, 
      message: errorMessage 
    };
  }).finally(() => {
    isSubmitting.value = false;
  });
};

const cancelForm = () => {
  router.push('/boardCategoryList');
};

const resetForm = () => {
  formData.value = {
    boardCategoryTitle: '',
    boardCategoryDescription: '',
    boardAllowedRole: 'ROLE_ADMIN',
    boardAllowImage: false,
    boardAllowAttachment: false,
    boardMaxFileSize: 10,
    boardAllowReply: true,
    boardMaxImageCount: 5,
    editorType: 'RICH_TEXT',
    boardAllowRecommend: true
  };
  submitResult.value = null;
};

onMounted(() => {
  fetchBoardCategories()
})
</script>

<style scoped>
.sub-nav-section {
  background: white;
  padding: 40px 0;
  border-bottom: 1px solid #e0e0e0;
}

.sub-nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.sub-nav-title {
  font-size: 15px;
  font-weight: 400;
  color: #333;
  font-family: 'Pretendard', sans-serif;
  margin: 0;
  text-align: center;
}

.sub-nav-menu {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 83px;
  width: 1049px;
  margin: 28px auto 0;
  padding: 30px 40px;
  border: 1px solid #e2e2e2;
  border-radius: 8px;
  background: white;
}

.sub-nav-item {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: transparent;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 400;
  color: #333;
  position: relative;
  text-align: center;
}

.sub-nav-item:hover {
  color: #008BE6;
  font-weight: bold;
  transform: translateY(-2px);
}

.sub-nav-item.active {
  background: #008BE6;
  color: white;
}

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