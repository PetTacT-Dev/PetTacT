<template>
  <nav v-if="totalPages > 1" class="pagination-container">
    <ul class="pagination">
      <li
        class="page-item"
        :class="{ disabled: currentPage === 1 }"
      >
        <button class="page-link" @click="$emit('change', currentPage - 1)" :disabled="currentPage === 1">
          이전
        </button>
      </li>

      <li
        class="page-item"
        v-for="page in pagesToShow"
        :key="page"
        :class="{ active: page === currentPage }"
      >
        <button class="page-link" @click="$emit('change', page)">
          {{ page }}
        </button>
      </li>

      <li
        class="page-item"
        :class="{ disabled: currentPage === totalPages }"
      >
        <button class="page-link" @click="$emit('change', currentPage + 1)" :disabled="currentPage === totalPages">
          다음
        </button>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: Number,
  totalPages: Number,
  limit: {
    type: Number,
    default: 5
  }
})

const pagesToShow = computed(() => {
  const half = Math.floor(props.limit / 2)
  let start = Math.max(1, props.currentPage - half)
  let end = Math.min(props.totalPages, start + props.limit - 1)

  if (end - start < props.limit - 1) {
    start = Math.max(1, end - props.limit + 1)
  }

  const pages = []
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.pagination {
  display: flex;
  gap: 6px;
  padding: 0;
  list-style: none;
}

.page-item {
  display: inline;
}

.page-item.disabled .page-link {
  opacity: 0.5;
  pointer-events: none;
}

.page-item.active .page-link {
  background-color: #007bff;
  color: white;
  font-weight: bold;
  border: 1px solid #007bff;
}

.page-link {
  border: 1px solid #ccc;
  background-color: white;
  color: #333;
  padding: 6px 12px;
  cursor: pointer;
  border-radius: 4px;
  min-width: 40px;
  text-align: center;
}
</style>
