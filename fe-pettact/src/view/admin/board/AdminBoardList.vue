<template>
  <div class="container mt-4">
    <h2 class="mb-4 border-bottom pb-2">게시물 관리</h2>

    <!-- 검색 필터 -->
    <div class="card mb-4">
      <div class="card-body">
        <form class="row g-3">
          <div class="col-md-4">
            <input v-model="filters.keyword" type="text" class="form-control" placeholder="제목 검색" />
          </div>
          <div class="col-md-3">
            <select v-model="filters.categoryNo" class="form-select">
              <option value="">카테고리 전체</option>
              <option v-for="category in categories" :key="category.boardCategoryNo" :value="category.boardCategoryNo">
                {{ category.boardCategoryTitle }}
              </option>
            </select>
          </div>
          <div class="col-md-3">
            <select v-model="filters.isDeleted" class="form-select">
              <option value="">삭제여부 전체</option>
              <option :value="false">활성</option>
              <option :value="true">삭제됨</option>
            </select>
          </div>
          <div class="col-md-2 d-grid">
            <button type="button" class="btn btn-primary" @click="onSearch">검색</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 테이블 -->
    <div class="table-responsive">
      <table class="table table-striped table-hover align-middle">
        <thead class="table-dark">
          <tr>
            <th scope="col">번호</th>
            <th scope="col">제목</th>
            <th scope="col">카테고리</th>
            <th scope="col">작성자</th>
            <th scope="col">이메일</th>
            <th scope="col">등록일</th>
            <th scope="col">숨김 여부</th>
            <th scope="col">상세</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(board, index) in pageData.content" :key="board.boardNo">
            <td>{{ index + 1 + ((pageData.currentPage - 1) * pageData.pageSize) }}</td>
            <td>{{ board.boardTitle }}</td>
            <td>{{ board.categoryName }}</td>
            <td>{{ board.userNickname }}</td>
            <td>{{ board.userEmail }}</td>
            <td>{{ formatDateTime(board.createdAt) }}</td>
            <td>{{ board.isDeleted ? '삭제됨' : '활성' }}</td>
            <td>
              <button type="button" class="btn btn-sm btn-outline-primary" @click="showDetail(board.boardNo)">상세보기</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이징 -->
    <Pagination
      :totalElements="pageData.totalElements"
      :currentPage="pageData.currentPage"
      :pageSize="pageData.pageSize"
      @change="onPageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Pagination from '@/components/common/Pagination.vue'

const router = useRouter()

const pageData = ref({
  content: [],
  totalElements: 0,
  currentPage: 1,
  pageSize: 10
})

const filters = ref({
  keyword: '',
  categoryNo: '',
  isDeleted: ''
})

const categories = ref([])

const getCategories = async () => {
  try {
    const res = await axios.get('/v1/board-categories')
    categories.value = res.data
  } catch (err) {
    console.error(err)
  }
}

const getBoardList = async (page = 1) => {
  try {
    const res = await axios.get('/v1/admin/board', {
      params: {
        keyword: filters.value.keyword,
        category: filters.value.categoryNo,
        isDeleted: filters.value.isDeleted,
        page: page,
        size: pageData.value.pageSize
      }
    })
    pageData.value = res.data
  } catch (err) {
    console.error(err)
  }
}

const onSearch = () => {
  getBoardList(1) // 검색 시 페이지 초기화
}

const onPageChange = (page) => {
  getBoardList(page)
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ').split('.')[0]
}

const showDetail = (boardNo) => {
  router.push({ name: 'adminBoardDetail', params: { boardNo } })
}

onMounted(() => {
  getCategories()
  getBoardList()
})
</script>
