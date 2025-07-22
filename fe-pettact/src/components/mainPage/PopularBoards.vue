<template>
  <div class="community-card">
    <div class="card-header">
      <h3 class="card-title">인기글</h3>
      <router-link to="/board/popular" class="more-link">더보기 <span>⟩</span></router-link>
    </div>

    <div v-if="loading">로딩 중...</div>
    <div v-else-if="boards.length === 0">표시할 게시글이 없습니다.</div>
    <ul v-else class="board-list">
      <li v-for="board in boards" :key="board.boardNo" class="board-item"
        @click="goToBoard(board.boardNo)">
        <div class="board-content">
          <span class="board-category">{{ board.responseDto.boardCategoryTitle }}</span>
          <span class="board-title">{{ board.boardTitle }}</span>
        </div>
        <div class="board-stats">
          <div class="like-count">❤️ {{ board.boardRecommendCount }}</div>
          <div class="view-count">👁 {{ formatViewCount(board.boardViewCnt) }}</div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const boards = ref([])
const loading = ref(true)
const router = useRouter()

const fetchPopularBoards = async () => {
  try {
    const { data } = await axios.get('/v1/board/popular', {
      params: { categoryNo: null, count: 7 }
    })
    console.log('🔥 인기글 응답', data)
    boards.value = data
  } catch (err) {
    console.error('🔥 인기글 API 실패', err)
  } finally {
    loading.value = false
  }
}

const goToBoard = (boardNo) => {
  router.push({ name: 'BoardDetail', params: { boardNo: boardNo } })
}

const formatViewCount = (views) => {
  if (views >= 1000) return (views / 1000).toFixed(1) + 'K'
  return views.toString()
}

onMounted(fetchPopularBoards)
</script>

<style scoped>
.community-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.more-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.more-link:hover {
  color: #333;
}

.board-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.board-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.board-item:last-child {
  border-bottom: none;
}

.board-item:hover {
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 4px;
  padding: 16px 8px;
  margin: 0 -8px;
}

.board-content {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow: hidden;
  min-width: 0;
  flex: 1;
}

.board-category {
  font-size: 12px;
  color: #888;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
  width: 80px;
  text-align: left;
}

.board-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.board-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  min-width: 80px;
}

.like-count,
.view-count {
  font-size: 12px;
  color: #666;
}
</style>