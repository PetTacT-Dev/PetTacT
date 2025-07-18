<template>
  <nav v-if="totalElements > pageSize">
    <ul class="pagination justify-content-center mt-3">
      <li class="page-item" :class="{ disabled: currentPage === 1 }">
        <button class="page-link" @click="$emit('change', currentPage - 1)">«</button>
      </li>

      <li
        class="page-item"
        v-for="page in pageNumbers"
        :key="page"
        :class="{ active: currentPage === page }"
      >
        <button class="page-link" @click="$emit('change', page)">
          {{ page }}
        </button>
      </li>

      <li class="page-item" :class="{ disabled: currentPage === totalPages }">
        <button class="page-link" @click="$emit('change', currentPage + 1)">»</button>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue';
const props = defineProps({
  totalElements: Number,
  currentPage: Number,
  pageSize: Number,
  pageRange: { type: Number, default: 5 } // 한 번에 보여줄 페이지 수
});

const totalPages = computed(() => Math.ceil(props.totalElements / props.pageSize));

const pageNumbers = computed(() => {
  const start = Math.max(1, props.currentPage - Math.floor(props.pageRange / 2));
  const end = Math.min(totalPages.value, start + props.pageRange - 1);
  const range = [];
  for (let i = start; i <= end; i++) {
    range.push(i);
  }
  return range;
});
</script>
