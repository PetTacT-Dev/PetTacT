<template>
  <div>
    <h3>유기동물 상세정보</h3>
    <div v-if="detail">
      <p><strong>종류:</strong> {{ detail.kindNm }}</p>
      <label>상태:
        <textarea v-model="detail.processState" />
      </label>
      <label>특이사항:
        <textarea v-model="detail.specialMark" />
      </label>

      <button @click="updateDetail">수정하기</button>
      <button @click="goToList" style="margin-left: 10px;">목록으로</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      detail: null,
    };
  },
  mounted() {
    const desertionNo = this.$route.params.desertionNo;
    axios.get(`/v1/api/abandonment/${desertionNo}`).then((res) => {
      this.detail = res.data;
    });
  },
  methods: {
    updateDetail() {
      const desertionNo = this.$route.params.desertionNo;
      axios.put(`/v1/api/abandonment/${desertionNo}`, this.detail)
        .then((res) => {
          alert('수정 완료');
          this.detail = res.data;
        })
        .catch((err) => {
          console.error('수정 실패', err);
        });
    },
    goToList() {
      this.$router.push('/abandonment');
    },
  },
};
</script>

