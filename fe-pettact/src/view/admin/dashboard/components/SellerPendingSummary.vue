<template>
  <div class="mb-4">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <h5 class="mb-0">신고</h5>
      <div class="text-muted">
        미처리 <strong>{{ pendingCount }}</strong>건 |
        <router-link :to="{ name: 'adminSellerRequests' }">전체보기 ></router-link>
      </div>
    </div>

    <table class="table table-striped table-hover table-sm">
      <thead>
        <tr>
          <th>ID</th>
          <th>이메일</th>
          <th>이름</th>
          <th>닉네임</th>
          <th>가입일</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in pendingSellers" :key="user.userNo">
          <td>{{ user.userNo }}</td>
          <td>{{ user.userEmail }}</td>
          <td>{{ user.userName }}</td>
          <td>{{ user.userNickname }}</td>
          <td>{{ formatDate(user.createdAt) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const pendingCount = ref(0)
const pendingSellers = ref([])

onMounted(async () => {
  const countRes = await axios.get('/v1/admin/dashboard/seller/pending-count')
  pendingCount.value = countRes.data

  const listRes = await axios.get('/v1/admin/dashboard/seller/pending?limit=5')
  pendingSellers.value = listRes.data
})

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString()
}
</script>
