
<template>
  <div class="community-page">
    <section class="sub-nav-section">
      <div class="sub-nav-container">
        <h2 class="sub-nav-title">커뮤니티</h2>
        
        <!-- 로딩 상태 -->
        <div v-if="loading" class="loading-state">
          <p>게시판 목록을 불러오는 중...</p>
        </div>

        <!-- 에러 상태 -->
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button @click="fetchBoardCategories" class="retry-btn">다시 시도</button>
        </div>

        <!-- 게시판 목록 -->
        <div v-else class="sub-nav-menu">
          <div 
            v-for="category in boardCategories" 
            :key="category.boardCategoryNo"
            class="sub-nav-item-container"
          >
            <!-- 게시판 제목 (클릭 시 게시판으로 이동) -->
            <div 
              class="sub-nav-item" 
              @click="goToBoard(category.boardCategoryNo)"
            >
              <span class="board-title">{{ category.boardCategoryTitle }}</span>
            </div>

            <!-- 관리자 버튼들 (관리자만 표시) -->
            <div v-if="isAdmin" class="admin-buttons">
              <button 
                @click.stop="viewCategoryDetail(category)" 
                class="admin-btn detail-btn"
                title="상세정보"
              >
                📄
              </button>
              <button 
                @click.stop="editCategory(category)" 
                class="admin-btn edit-btn"
                title="수정하기"
              >
                ✏️
              </button>
              <button 
                @click.stop="deleteCategory(category)" 
                class="admin-btn delete-btn"
                title="삭제하기"
              >
                🗑️
              </button>
            </div>
          </div>

          <!-- 게시판이 없을 때 -->
          <div v-if="boardCategories.length === 0 && !loading" class="empty-boards">
            <p>등록된 게시판이 없습니다.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 게시판 생성 버튼 (관리자만) -->
    <div v-if="isAdmin" class="category-actions">
      <button @click="goToCreateCategory" class="create-btn">
        + 새 게시판 만들기
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

export default {
  setup() {
    const router = useRouter()
    const userStore = useUserStore()

    // Reactive data
    const boardCategories = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Computed
    const isAdmin = computed(() => userStore.user?.userRole === 'ROLE_ADMIN')

    // Methods
    const fetchBoardCategories = async () => {
      loading.value = true
      error.value = null
      
      try {
        console.log('🔍 게시판 카테고리 목록 조회 시작...')
        const response = await axios.get('/v1/board-categories')
        boardCategories.value = response.data || []
        console.log('✅ 게시판 목록 로드 완료:', boardCategories.value)
      } catch (err) {
        console.error('❌ 게시판 목록 조회 실패:', err)
        error.value = '게시판 목록을 불러오는데 실패했습니다.'
        boardCategories.value = []
      } finally {
        loading.value = false
      }
    }

    const goToBoard = (categoryNo) => {
      console.log('게시판 이동:', categoryNo)
      router.push(`/board/${categoryNo}`)
    }

    const goToCreateCategory = () => {
      router.push('/boardCategoryForm')
    }

    const viewCategoryDetail = (category) => {
      console.log('상세정보:', category)
      router.push(`/boardCategoryDetail/${category.boardCategoryNo}`)
    }

    const editCategory = (category) => {
      console.log('수정:', category)
      router.push(`/boardCategory/${category.boardCategoryNo}/edit`)
    }

    const deleteCategory = async (category) => {
      if (confirm(`'${category.boardCategoryTitle}' 게시판을 정말 삭제하시겠습니까?`)) {
        try {
          await axios.delete(`/v1/board-categories/${category.boardCategoryNo}`)
          alert('게시판이 삭제되었습니다.')
          fetchBoardCategories() // 목록 새로고침
        } catch (error) {
          console.error('삭제 실패:', error)
          alert('게시판 삭제에 실패했습니다.')
        }
      }
    }

    // Lifecycle
    onMounted(() => {
      console.log('🚀 BoardCategoryList 컴포넌트 마운트')
      console.log('👤 현재 사용자 권한 - isAdmin:', isAdmin.value)
      fetchBoardCategories()
    })

    // Return
    return {
      boardCategories,
      loading,
      error,
      isAdmin,
      fetchBoardCategories,
      goToCreateCategory,
      goToBoard,
      viewCategoryDetail,
      editCategory,
      deleteCategory
    }
  }
}
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

.modern-dropdown {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border: 1px solid #e5e7eb;
  min-width: 200px;
  z-index: 10000;
  overflow: hidden;
}

/* 배경 오버레이 */
.dropdown-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);  /* 반투명 검은 배경 */
  z-index: 9999;
  backdrop-filter: blur(2px);      /* 배경 블러 효과 */
}

.dropdown-item-modern {
  padding: 12px 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-item-modern:last-child {
  border-bottom: none;
}

.dropdown-item-modern:hover {
  background: #f9fafb;
  color: #111827;
}

.delete-item {
  color: #ef4444;
}

.delete-item:hover {
  background: #fef2f2;
  color: #dc2626;
}

/* .modern-dropdown {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid #e5e7eb;
  min-width: 160px;
  z-index: 1000;
  overflow: hidden;
  margin-top: 8px;
}

.dropdown-item-modern {
  padding: 12px 16px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-item-modern:last-child {
  border-bottom: none;
}

.dropdown-item-modern:hover {
  background: #f9fafb;
  color: #111827;
}

.delete-item {
  color: #ef4444;
}

.delete-item:hover {
  background: #fef2f2;
  color: #dc2626;
} */

.create-btn {
  /* 기본 모양 */
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  
  /* 색상 - 관리자 전용이므로 눈에 띄는 색 */
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  box-shadow: 0 2px 4px rgba(0, 123, 255, 0.3);
  
  /* 아이콘 여백 (아이콘 추가 시) */
  display: flex;
  align-items: center;
  gap: 8px;
}

.create-btn:hover {
  background: linear-gradient(135deg, #0056b3, #004085);
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.4);
  transform: translateY(-1px);
}

.create-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 123, 255, 0.3);
}
</style>