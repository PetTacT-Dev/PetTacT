<template>
  <div class="animal-search-page">
    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">따뜻한 보금자리를 찾아요</h1>
          <p class="hero-subtitle">
            새로운 가족을 기다리는 우리 아이들의 정보를 확인해보세요.
          </p>
          <div class="hero-buttons">
            <button class="btn btn-outline">입양 정보</button>
            <button class="btn btn-primary" @click="scrollToResults">
              바로 검색
            </button>
          </div>
        </div>

        <!-- 검색어 입력창 -->
        <div class="search-container">
          <span class="search-icon">🔍</span>
          <input
            type="text"
            placeholder="검색어를 입력하세요"
            class="search-input"
            v-model="searchKeyword"
          />
          <button @click="searchAnimals" class="search-btn">조회</button>
        </div>
      </div>
    </section>

    <!-- 카테고리 섹션 -->
    <section class="category-section">
      <div class="category-grid">
        <div v-for="region in regions" :key="region" class="category-item">
          <div class="region-name">{{ region }}</div>
        </div>
      </div>
    </section>

    <!-- 검색 필터 + 결과 -->
    <section class="search-results-section" ref="resultsSection">
      <div class="search-filters-container">
        <h2 class="search-title">유기동물 검색</h2>

        <div class="filters">
          <select
            v-model="selectedUpKindCd"
            @change="fetchKinds"
            class="filter-select"
          >
            <option value="">축종 선택</option>
            <option value="417000">개</option>
            <option value="422400">고양이</option>
            <option value="429900">기타</option>
          </select>

          <select v-model="selectedKindCd" class="filter-select">
            <option value="">품종 선택</option>
            <option v-for="k in kindList" :key="k.kindCd" :value="k.kindCd">
              {{ k.kindNm }}
            </option>
          </select>

          <select
            v-model="selectedSido"
            @change="handleSidoChange"
            class="filter-select"
          >
            <option value="">시도 선택</option>
            <option v-for="s in sidoList" :key="s.orgCd" :value="s">
              {{ s.orgdownNm }}
            </option>
          </select>

          <select
            v-model="selectedSigungu"
            @change="handleSigunguChange"
            class="filter-select"
          >
            <option value="">시군구 선택</option>
            <option v-for="g in sigunguList" :key="g.orgCd" :value="g">
              {{ g.orgdownNm }}
            </option>
          </select>

          <button @click="goPage(1)" class="search-filter-btn">조회</button>
        </div>

        <!-- 조회 결과 -->
        <div v-if="abandonments.length > 0" class="adoption-section">
          <div class="adoption-container">
            <div class="adoption-header">
              <h2 class="adoption-title">조회 결과</h2>
              <p class="adoption-subtitle">
                (총 {{ totalElements.toLocaleString() }}건)
              </p>
            </div>

            <div class="adoption-content">
              <div
                v-for="(row, rowIndex) in chunkedAbandonments"
                :key="rowIndex"
                class="adoption-row"
              >
                <div v-for="a in row" :key="a.desertionNo" class="pet-card">
                  <div class="pet-image">
                    <img
                      :src="a.popfile1 || '/image/no-image.png'"
                      alt="사진"
                    />
                  </div>
                  <div class="pet-info">
                    <div class="pet-tags">
                      <span class="tag tag-dark"
                        >{{ a.sexCd }} / {{ a.age }}</span
                      >
                      <span class="tag tag-light">{{ a.kindCd }}</span>
                    </div>
                    <div class="pet-details">
                      <div class="pet-details-content">
                        <div class="pet-name">No. {{ a.desertionNo }}</div>
                        <div class="pet-location">{{ a.happenPlace }}</div>
                        <div class="pet-description">
                          {{ a.noticeSdt }} ~ {{ a.noticeEdt }}
                        </div>
                      </div>
                      <button
                        @click="DetailView(a.desertionNo)"
                        class="pet-button"
                      >
                        상세보기
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <Pagination
              :current-page="page"
              :total-pages="totalPages"
              @change="goPage"
            />
          </div>
        </div>

        <p v-else-if="searched" class="no-results">
          조건에 맞는 유기동물이 없습니다.
        </p>
      </div>
    </section>

    <!-- FAQ -->
    <section class="faq-section">
      <div class="faq-container">
        <div class="faq-header">
          <h2 class="faq-title">사용자가 자주하는 질문</h2>
          <p class="faq-subtitle">
            사용자들이 가장 궁금해하는 질문 세 가지를 미리 소개해드립니다.
          </p>
          <button class="faq-view-all">모든 FAQ 보기</button>
        </div>

        <div class="faq-list">
          <div
            v-for="(faq, index) in faqs"
            :key="index"
            class="faq-item"
            :class="{ open: openIndex === index }"
            @click="toggleFAQ(index)"
          >
            <div class="faq-question-row">
              <div class="faq-question">{{ faq.question }}</div>
              <div class="faq-icon">{{ openIndex === index ? "×" : "+" }}</div>
            </div>
            <div class="faq-answer" v-if="openIndex === index">
              {{ faq.answer }}
            </div>
            <button class="faq-button">입양 보호소 전화하기</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 마감 임박 리스트 (기본 표시) -->
    <section
      class="adoption-section"
      v-if="!searched && defaultPets.length > 0"
    >
      <div class="adoption-container">
        <div class="adoption-header">
          <h2 class="adoption-title">입양 마감 임박 동물들</h2>
          <p class="adoption-subtitle">
            입양 마감일이 가까운 아이들을 소개해요.
          </p>
        </div>
        <div class="adoption-content">
          <div
            v-for="(row, rowIndex) in chunkedDefaultPets"
            :key="rowIndex"
            class="adoption-row"
          >
            <div v-for="a in row" :key="a.desertionNo" class="pet-card">
              <div class="pet-image">
                <img :src="a.popfile1 || '/image/no-image.png'" alt="사진" />
              </div>
              <div class="pet-info">
                <div class="pet-tags">
                  <span class="tag tag-dark">{{ a.sexCd }} / {{ a.age }}</span>
                  <span class="tag tag-light">{{ a.kindCd }}</span>
                </div>
                <div class="pet-details">
                  <div class="pet-details-content">
                    <div class="pet-name">No. {{ a.desertionNo }}</div>
                    <div class="pet-location">{{ a.happenPlace }}</div>
                    <div class="pet-description">
                      {{ a.noticeSdt }} ~ {{ a.noticeEdt }}
                    </div>
                  </div>
                  <button @click="DetailView(a.desertionNo)" class="pet-button">
                    상세보기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import axios from "axios";
import Pagination from "@/components/common/Paginations.vue";
import { useModalStore } from "@/js/modalStore";

export default {
  components: { Pagination },
  data() {
    return {
      selectedUpKindCd: "",
      selectedKindCd: "",
      selectedSido: "",
      selectedSigungu: "",
      selectedShelterCd: "",
      kindList: [],
      sidoList: [],
      sigunguList: [],
      shelterList: [],
      abandonments: [],
      page: 1,
      totalPages: 1,
      totalElements: 0,
      searched: false,

      searchKeyword: "",
      defaultPets: [],
      defaultPage: 1,
      defaultTotalPages: 1,
      openIndex: 0,
      selectedRegion: null,
      regions: ["서울", "경기", "인천", "강원", "충청", "전라", "경상", "제주"],
      faqs: [
        {
          question: "홈페이지에서 직접 입양이 가능한가요?",
          answer:
            "홈페이지에서의 직접 입양은 불가합니다. 해당 보호소로 유선 연락 부탁드립니다.",
        },
        {
          question: "입양 후 반려동물 등록은 어떻게 하나요?",
          answer: "입양 후에는 동물등록센터를 통해 등록 가능합니다.",
        },
        {
          question: "입양 조건이 따로 있나요?",
          answer:
            "일부 보호소는 보호자 요건을 따로 두고 있습니다. 해당 보호소에 문의해주세요.",
        },
      ],
    };
  },
  computed: {
    chunkedDefaultPets() {
      const chunkSize = 3;
      const chunks = [];
      for (let i = 0; i < this.defaultPets.length; i += chunkSize) {
        chunks.push(this.defaultPets.slice(i, i + chunkSize));
      }
      return chunks;
    },
    chunkedAbandonments() {
      const chunkSize = 3;
      const chunks = [];
      for (let i = 0; i < this.abandonments.length; i += chunkSize) {
        chunks.push(this.abandonments.slice(i, i + chunkSize));
      }
      return chunks;
    },
  },
  created() {
    this.fetchSido();
    this.fetchEndingSoonPets(1);
  },
  methods: {
    scrollToResults() {
      this.$refs.resultsSection.scrollIntoView({ behavior: "smooth" });
    },
    searchAnimals() {
      if (this.searchKeyword.trim()) {
        this.scrollToResults();
        console.log("검색어:", this.searchKeyword);
      }
    },
    toggleFAQ(index) {
      this.openIndex = this.openIndex === index ? null : index;
    },
    fetchEndingSoonPets(page = 1) {
      axios
        .get("v1/pet/abandonment/ending-soon", {
          params: { page, size: 9 },
        })
        .then((res) => {
          console.log("🐶 마감 임박 동물 응답:", res.data);
          this.defaultPets = res.data.content;
          this.defaultTotalPages = res.data.totalPages;
          this.defaultPage = page;
        })
        .catch((err) => {
          console.error("마감 임박 동물 조회 실패:", err);
        });
    },
    goDefaultPage(page) {
      this.fetchEndingSoonPets(page);
    },
    openChatModal() {
      const modalStore = useModalStore();
      modalStore.openMessageModal();
    },
    DetailView(desertionNo) {
      this.$router.push(`/abandonment/${desertionNo}`);
    },
    fetchSido() {
      axios.get("v1/pet/sido").then((res) => (this.sidoList = res.data.items));
    },
    fetchKinds() {
      this.selectedKindCd = "";
      this.kindList = [];
      if (!this.selectedUpKindCd) return;
      axios
        .get("v1/pet/kind", {
          params: { upKindCd: this.selectedUpKindCd },
        })
        .then((res) => (this.kindList = res.data.items));
    },
    handleSidoChange() {
      this.selectedSigungu = "";
      this.sigunguList = [];
      this.selectedShelterCd = "";
      this.shelterList = [];
      if (this.selectedSido) this.fetchSigungu();
    },
    fetchSigungu() {
      axios
        .get("v1/pet/sigungu", {
          params: { uprCd: this.selectedSido.orgCd },
        })
        .then((res) => (this.sigunguList = res.data.items));
    },
    handleSigunguChange() {
      this.selectedShelterCd = "";
      this.shelterList = [];
      if (this.selectedSido && this.selectedSigungu) this.fetchShelters();
    },
    fetchShelters() {
      axios
        .get("v1/pet/shelter", {
          params: {
            uprCd: this.selectedSido.orgCd,
            orgCd: this.selectedSigungu.orgCd,
          },
        })
        .then((res) => (this.shelterList = res.data.items));
    },
    goPage(page) {
      if (!this.selectedKindCd) {
        alert("품종을 선택해주세요.");
        return;
      }
      const params = {
        page,
        size: 10,
        upKindCd: this.selectedUpKindCd,
        kindCd: this.selectedKindCd,
      };
      if (this.selectedSido?.orgdownNm) {
        params.orgNm = this.selectedSido.orgdownNm;
        if (this.selectedSigungu?.orgdownNm) {
          params.orgNm += " " + this.selectedSigungu.orgdownNm;
        }
      }
      if (this.selectedShelterCd) {
        params.careRegNo = this.selectedShelterCd;
      }
      axios
        .get("v1/pet/abandonment", { params })
        .then((res) => {
          this.abandonments = res.data.content;
          this.totalPages = res.data.totalPages;
          this.totalElements = res.data.totalElements;
          this.page = page;
          this.searched = true;
        })
        .catch((err) => {
          console.error("유기동물 조회 실패:", err);
        });
    },
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Pretendard", sans-serif;
  background: white;
  color: #111111;
}

.deco {
  position: absolute;
  z-index: 0;
  pointer-events: none;
}

/* 위 왼쪽 */
.deco-left {
  width: 100.12px;
  height: 137px;
  top: 0;
  left: 0;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.25);
  filter: blur(2px);
}

/* 위 오른쪽 */
.deco-right {
  width: 99.8px;
  height: 121px;
  top: 0;
  right: 0;
}

/* 아래 중앙 (회전됨) */
.deco-bottom {
  width: 135.16px;
  height: 99px;
  bottom: 0;
  left: 50%;
  transform: rotate(-90deg) translateX(-50%);
  transform-origin: top left;
}

/* 히어로 섹션 */
.hero-section {
  position: relative;
  width: 100%;
  height: 100vh;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 48px;
  padding: 250px 0;
}

.hero-content {
  width: 658px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 48px;
}

.hero-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 25px;
}

.hero-title {
  text-align: center;
  color: black;
  font-size: 29px;
  font-weight: 600;
  line-height: 40.6px;
  margin-bottom: 19px;
}

.hero-subtitle {
  text-align: center;
  color: black;
  font-size: 18px;
  font-weight: 300;
  line-height: 25.2px;
}

.hero-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 25px;
}

.btn {
  padding: 11.2px 24px;
  border-radius: 15px;
  font-size: 15px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-outline {
  background: transparent;
  border: 1px solid #000;
  color: black;
}

.btn-outline:hover {
  background: white;
  color: black;
}

.btn-primary {
  background: #008be6;
  border: none;
  color: white;
}

.btn-primary:hover {
  background: #0066cc;
}

.search-btn {
  padding: 10px 18px;
  border-radius: 8px;
  background-color: #007bff;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
  border: none;
  font-size: 14px;
}

.search-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.search-btn:hover {
  background-color: #0056b3;
}

.search-container {
  width: 540px;
  height: 60px;
  position: relative;
  background: white;
  box-shadow: 10px 10px 30px rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.search-icon {
  width: 28px;
  height: 28px;
  margin-right: 9px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: #111111;
}

.search-input::placeholder {
  color: #767676;
}

.search-btn {
  padding: 8px 16px;
  background: #008be6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: #0066cc;
}

/* 장식 이미지들 */
.hero-decoration {
  position: absolute;
  background: #ccc;
}

.decoration-1 {
  width: 99px;
  height: 135.16px;
  left: 467px;
  top: 498px;
  transform: rotate(-90deg);
}

.decoration-2 {
  width: 100.12px;
  height: 137px;
  right: 661px;
  top: 566px;
  filter: blur(2px);
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.3);
}

.decoration-3 {
  width: 99.8px;
  height: 121px;
  right: 563px;
  top: 278px;
}

/* 카테고리 섹션 */
.category-section {
  width: 100%;
  padding: 80px 0;
  display: flex;
  justify-content: center;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(8, 167px);
  gap: 12px;
  width: 1353px;
}

.category-item {
  width: 167px;
  height: 80px;
  background: rgba(247, 247, 247, 0.73);
  box-shadow: 10px 10px 30px rgba(0, 0, 0, 0.06);
  border-radius: 8px;
  border: 1px solid #e5e5ec;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: rgba(0, 139, 230, 0.1);
  transform: translateY(-2px);
}

.category-icon {
  width: 68px;
  height: 34px;
  background: #505050;
  border-radius: 4px;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 24px;
}

.filter-select {
  padding: 10px 14px;
  border: 1.5px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  font-size: 14px;
  min-width: 130px;
  transition: 0.2s;
}

.filter-select:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(0, 139, 230, 0.2);
}

.search-filter-btn {
  padding: 10px 18px;
  border-radius: 8px;
  background-color: #008be6;
  color: white;
  font-weight: bold;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s;
}

.search-filter-btn:hover {
  background-color: #006dc1;
}

/* ✅ 검색 상단 박스 */
.search-container {
  width: 540px;
  height: 60px;
  position: relative;
  background: white;
  box-shadow: 10px 10px 30px rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  margin-bottom: 32px;
}

.search-icon {
  width: 28px;
  height: 28px;
  margin-right: 9px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: #111111;
}

.search-input::placeholder {
  color: #767676;
}

.search-btn {
  padding: 8px 16px;
  background: #008be6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: #0066cc;
}

/* ✅ 입양 마감 임박 섹션 */
.adoption-section {
  width: 100%;
  padding: 40px 0;
}

.adoption-header {
  text-align: center;
  margin-bottom: 32px;
}

.adoption-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
}

.adoption-subtitle {
  font-size: 15px;
  color: #666;
}

/* ✅ 카드 그리드 (3x3) */
.adoption-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 36px;
  justify-items: center;
  max-width: 1480px;
  margin: 0 auto;
}

/* ✅ 개별 카드 */
.pet-card {
  width: 100%;
  max-width: 470px;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: transform 0.3s ease;
}

.pet-card:hover {
  transform: translateY(-5px);
}

.pet-image img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-bottom: 1px solid #eee;
}

.pet-info {
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.pet-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 13px;
}

.tag-dark {
  background-color: #444;
  color: #fff;
}

.tag-light {
  background-color: #eee;
  color: #333;
}

.pet-details-content {
  margin-bottom: 14px;
}

.pet-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.pet-location {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.pet-description {
  font-size: 13px;
  color: #999;
}

.pet-button {
  width: 420px;
  height: 42px;
  background-color: #F7F7FB;
  color: #111;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 15px;
  font-family: 'Pretendard', sans-serif;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-button:hover {
  background-color: #006dc1;
}

.pet-button:hover {
  background: #008be6;
  color: white;
}

/* 결과 없음 */
.no-results {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 100px 0;
}

.no-results-content {
  text-align: center;
}

.no-results-content h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.no-results-content p {
  font-size: 16px;
  color: #666;
}

/* FAQ 섹션 */
.faq-section {
  width: 100%;
  padding: 80px 0;
  display: flex;
  justify-content: center;
}

.faq-container {
  width: 1473px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 104px;
}

.faq-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 104px;
}

.faq-header {
  width: 429px;
  display: flex;
  flex-direction: column;
}

.faq-list {
  width: 940px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.faq-title {
  color: black;
  font-size: 40px;
  font-weight: 700;
  line-height: 56px;
  margin-bottom: 20px;
}

.faq-subtitle {
  color: #767676;
  font-size: 18px;
  font-weight: 500;
  line-height: 25.2px;
  margin-bottom: 30px;
}

.faq-view-all {
  width: 222px;
  height: 52px;
  background: white;
  border: 1px solid #e5e5ec;
  border-radius: 15px;
  color: #111111;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.faq-view-all:hover {
  background: #008be6;
  color: white;
}

.faq-item {
  width: 940px;
  padding: 32px;
  background: #e5e5ec;
  box-shadow: 10px 10px 30px rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.faq-question-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.faq-question {
  flex: 1;
  color: black;
  font-size: 20px;
  font-weight: 600;
  line-height: 28px;
}

.faq-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.faq-icon.open {
  background: #f0f8ff;
}

.faq-icon.closed {
  background: #f7f7fb;
}

.faq-answer {
  color: #767676;
  font-size: 18px;
  font-weight: 500;
  line-height: 25.2px;
}

.faq-button {
  width: 646px;
  height: 55px;
  background: #f0f8ff;
  border: none;
  border-radius: 12px;
  color: black;
  font-size: 18px;
  font-weight: 600;
  line-height: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.faq-button:hover {
  background: #008be6;
  color: white;
}

.faq-answer,
.faq-button {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease, padding 0.3s ease;
}

.faq-item.open .faq-answer,
.faq-item.open .faq-button {
  max-height: 200px;
  padding-top: 20px;
}

.faq-icon {
  transition: transform 0.3s ease;
}

.faq-item.open .faq-icon {
  transform: rotate(45deg);
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 40px 0;
}

.pagination-btn {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  border: 1px solid #dfe3e8;
  background: white;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.3s ease;
}

.pagination-btn:hover {
  background: #008be6;
  color: white;
  border-color: #008be6;
}

.pagination-btn.active {
  background: white;
  color: #4200ff;
  border-color: #4200ff;
}

.pagination-btn.disabled {
  background: #919eab;
  color: #c4cdd5;
  opacity: 0.5;
  cursor: not-allowed;
}
</style>