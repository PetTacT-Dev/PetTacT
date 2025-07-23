<template>
  <div class="container mt-4">
    <h2>📋 HTTP 로그 목록</h2>

    <div class="d-flex align-items-center mb-3">
      <select v-model="selectedOption" class="form-select me-2" style="width: 160px;">
        <option value="uri">URI</option>
        <option value="ip">IP</option>
        <option value="userId">유저번호</option>
      </select>
    <input v-model="searchValue" class="form-control me-2" :placeholder="getPlaceholder()" style="width: 300px;" @keyup.enter="searchLogs"/>
      <button @click="searchLogs" class="btn btn-primary">검색</button>
    </div>

    <!-- 📋 로그 테이블 -->
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>시간</th>
          <th>메서드</th>
          <th>URI</th>
          <th>IP</th>
          <th>유저 번호</th>
          <th>상태</th>
          <th>소요시간(ms)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="log in logs" :key="log._id">
          <td>{{ formatDate(log.timestamp) }}</td>
          <td>{{ log.method }}</td>
          <td>{{ log.uri }}</td>
          <td>{{ log.ip }}</td>
          <td>{{ log.userId }}</td>
          <td>{{ log.status }}</td>
          <td>{{ log.duration }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import axios from 'axios';

export default {
  data() {
    return {
      logs: [],
      selectedOption: 'uri',
      searchValue: ''
    };
  },
  mounted() {
    this.fetchLogs();
  },//
  methods: {
    fetchLogs(params = {}) {
      axios.get("/v1/logs", { params }).then((res) => {
        this.logs = res.data;
      });
    },
    searchLogs() {
      const params = {};
      if (this.searchValue) {
        params[this.selectedOption] = this.searchValue;
      }
      this.fetchLogs(params);
    },
    getPlaceholder() {
      switch (this.selectedOption) {
        case 'uri': return 'URI 검색';
        case 'ip': return 'IP 검색 (예: 127.0.0.1)';
        case 'userId': return '유저번호 검색';
        default: return '';
      }
    },
    formatDate(isoDateStr) {
      const date = new Date(isoDateStr);
      const year = date.getFullYear();
      const month = `${date.getMonth() + 1}`.padStart(2, "0");
      const day = `${date.getDate()}`.padStart(2, "0");
      const hour = `${date.getHours()}`.padStart(2, "0");
      const minute = `${date.getMinutes()}`.padStart(2, "0");
      const second = `${date.getSeconds()}`.padStart(2, "0");

      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    }
  }
};
</script>

