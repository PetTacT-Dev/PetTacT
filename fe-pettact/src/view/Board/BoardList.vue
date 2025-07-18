<template>
  <div class="board-list-table">
    <section class="sub-nav-section">
      <div class="sub-nav-container">
        <h2 class="sub-nav-title">커뮤니티</h2>
        <div class="sub-nav-menu">
          <div
            v-for="category in boardCategories"
            :key="category.boardCategoryNo"
            class="sub-nav-item-container"
          >
            <div
              class="sub-nav-item"
              :class="{ active: category.boardCategoryNo == currentCategoryNo }"
              @click="goToBoard(category.boardCategoryNo)"
            >
              {{ category.boardCategoryTitle }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 현재 선택된 카테고리 표시 -->
    <div class="current-category-header">
      <h3>{{ boardConfig.title }}</h3>
      <p v-if="boardConfig.description">{{ boardConfig.description }}</p>
    </div>

    <!-- ✅ 검색바 추가 -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-input-group">
          <input 
            v-model="searchKeyword"
            type="text"
            placeholder="제목, 내용, 작성자 통합검색"
            class="search-input"
            @keyup.enter="searchBoards"
          />
          <button @click="searchBoards" class="search-btn">
            🔍 검색
          </button>
          <button @click="clearSearch" class="clear-btn" v-if="searchKeyword">
            ❌ 초기화
          </button>
        </div>
         <div class="search-info" v-if="searchKeyword && isSearched">
            <span class="search-keyword">"{{ searchKeyword }}"</span> 검색 결과: {{ totalElements }}개
        </div>
      </div>
    </div>

    <div class="list-header">
        <div class="col-title">제목</div>
        <div class="col-author">작성자</div>
        <div class="col-date">작성일</div>
        <div class="col-replies">댓글</div> 
        <div class="col-views">조회수</div>
    </div>

    <div class="list-body">
        <div
        v-for="board in boards"
        :key="board.boardNo"
        @click="goToDetail(board.boardNo)"
        class="list-row"
      > 
        <div class="col-title">
            <span class="title-text">{{ board.boardTitle }}</span>
        </div>
        <div class="col-author">{{ board.userNickname }}</div>
        <div class="col-date">{{ formatDate(board.createdAt) }}</div>
        <div class="col-replies">{{ board.totalReplyCount || 0 }}</div>
        <div class="col-views">{{ board.boardViewCnt || 0 }}</div>
        </div>
    </div>

    <!-- 페이징 -->
    <div class="pagination" v-if="totalPages > 1">
      <button
        v-for="page in totalPages"
        :key="page"
        @click="changePage(page)"
        :class="{ active: currentPage === page }"
      >
        {{ page }}
      </button>
    </div>

    <div class="list-actions">
      <button @click="goToCreate" class="write-btn">
        ✏️ 작성하기
      </button>
    </div>
  </div>
</template>

<script setup> 
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const boardCategories = ref([])
const currentCategoryNo = ref(null)

// 카테고리 title 출력
const loadBoardCategories = async () => {
  try {
    const response = await axios.get('/v1/board-categories')
    boardCategories.value = response.data
    console.log('카테고리 목록 로드:', response.data)
  } catch (error) {
    console.error('게시판 카테고리 로딩 실패:', error)
  }
}

// 다른 카테고리로 이동하는 함수
const goToBoard = (categoryNo) => {
  console.log(`카테고리 이동: ${currentCategoryNo.value} → ${categoryNo}`)
  if (categoryNo != currentCategoryNo.value) {
    router.push(`/board/${categoryNo}`)
  }
}

// 반응형 데이터
const boards = ref([])
const totalBoards = ref(0)
const todayBoards = ref(0)
const loading = ref(false)
const searchKeyword = ref('')
const isSearched = ref(false)
const boardConfig = ref({
  title: '게시판',
  description: '',
  icon: '',
  searchPlaceholder: '검색어를 입력하세요',
  listType: 'list'
})

// 페이징 관련 변수
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)

// 글쓰기 버튼
const goToCreate = () => {
  const categoryNo = route.params.categoryNo
  router.push(`/category/${categoryNo}/create`)
}

// 게시글 상세
const goToDetail = (boardNo) => {
  router.push(`/board/detail/${boardNo}`)
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now - date
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) {
    return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
  } else if (diffDays < 7) {
    return `${diffDays}일 전`
  } else {
    return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
  }
}

// ✅ 검색 실행
const searchBoards = async () => {
  if (!searchKeyword.value.trim()) {
    alert('검색어를 입력해주세요.')
    return
  }
  
  console.log('🔍 검색 실행:', searchKeyword.value)
  currentPage.value = 1  // 검색 시 첫 페이지로
  isSearched.value = true
  await loadBoards()
}

// ✅ 검색 초기화
const clearSearch = async () => {
  console.log('🔄 검색 초기화')
  searchKeyword.value = ''
  isSearched.value = false
  currentPage.value = 1
  await loadBoards()
}

// 페이지 변경
const changePage = (page) => {
  console.log('📄 페이지 변경:', currentPage.value, '→', page)
  currentPage.value = page
  loadBoards()
}

// ✅ 게시글 목록 로드 (검색 + 페이징)
const loadBoards = async () => {
  try {
    loading.value = true
    const categoryNo = route.params.categoryNo
    currentCategoryNo.value = categoryNo

    console.log('🔍 로드 파라미터:', {
      categoryNo,
      page: currentPage.value - 1,
      size: pageSize.value,
      searchKeyword: searchKeyword.value
    })

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }

    // ✅ 검색어가 있으면 searchKeyword 파라미터 추가
    if (searchKeyword.value.trim()) {
      params.searchKeyword = searchKeyword.value.trim()
    }

    const response = await axios.get(`/v1/board-categories/${categoryNo}/boards`, {
      params
    })

    // 페이징 응답 처리
    if (response.data.content) {
      boards.value = response.data.content
      totalPages.value = response.data.totalPages
      totalElements.value = response.data.totalElements
      totalBoards.value = response.data.totalElements
      
      console.log('📊 페이징 정보:', {
        totalPages: response.data.totalPages,
        totalElements: response.data.totalElements,
        currentPage: currentPage.value,
        searchKeyword: searchKeyword.value
      })
    } else {
      // 페이징 없는 응답 처리 (호환성)
      boards.value = Array.isArray(response.data) ? response.data : []
      totalBoards.value = boards.value.length
    }

    const today = new Date().toISOString().split('T')[0]
    todayBoards.value = boards.value.filter(board =>
      board.createdAt && board.createdAt.startsWith(today)
    ).length

  } catch (error) {
    console.error('❌ 게시글 로드 실패:', error)
    boards.value = []
    totalBoards.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

// 카테고리 정보 로드
const loadCategoryInfo = async () => {
  try {
    const categoryNo = route.params.categoryNo
    const response = await axios.get(`/v1/board-categories/${categoryNo}`)
    
    boardConfig.value = {
      title: response.data.boardCategoryTitle || '게시판',
      description: response.data.boardCategoryDescription || '',
      icon: '📋',
      searchPlaceholder: '검색어를 입력하세요',
      listType: 'list'
    }
  } catch (error) {
    console.error('카테고리 로드 실패:', error)
  }
}

// 초기 로드
onMounted(async () => {
  currentCategoryNo.value = route.params.categoryNo
  await loadBoardCategories()
  await loadCategoryInfo()
  await loadBoards()
})

// 라우트 변경 감지
watch(
  () => route.params.categoryNo,
  async (newCategoryNo) => {
    if (newCategoryNo && newCategoryNo != currentCategoryNo.value) {
      console.log(`라우트 변경 감지: ${currentCategoryNo.value} → ${newCategoryNo}`)
      currentCategoryNo.value = newCategoryNo
      // ✅ 페이지 초기화 + 검색 초기화
      currentPage.value = 1
      searchKeyword.value = ''
      await loadCategoryInfo()
      await loadBoards()
    }
  }
)
</script>


<style scoped>
.board-list-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e1e5e9;
}

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

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.search-container {
  max-width: 600px;
  margin: 0 auto;
}

.search-input-group {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.search-input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.search-btn {
  padding: 12px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.search-btn:hover {
  background: #0056b3;
}

.clear-btn {
  padding: 12px 15px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.clear-btn:hover {
  background: #545b62;
}

.search-info {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.search-keyword {
  font-weight: bold;
  color: #007bff;
}

.list-header {
  display: grid;
  grid-template-columns: 2fr 120px 120px 80px 80px;
  background: #f8f9fa;
  padding: 16px;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e1e5e9;
}

.list-body {
  max-height: 600px;
  overflow-y: auto;
}

.list-row {
  display: grid;
  grid-template-columns: 2fr 120px 120px 80px 80px;
  padding: 16px;
  border-bottom: 1px solid #f1f3f4;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.list-row:hover {
  background: #f8f9fa;
}

.list-row:last-child {
  border-bottom: none;
}

.col-title {
  text-align: left;
}

.col-author, .col-date, .col-views, .col-replies { 
  text-align: center;
  color: #6c757d;
  font-size: 14px;
}

.title-text {
  font-weight: 500;
  color: #212529;
  line-height: 1.4;
}
</style>