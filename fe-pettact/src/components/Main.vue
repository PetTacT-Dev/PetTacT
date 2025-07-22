<template>
  <div class="main-page">
    <!-- banner section -->
    <section class="hero">
      <div class="swiper-wrapper" :style="swiperStyle" @mouseenter="pauseAutoSlide" @mouseleave="resumeAutoSlide">
        <!-- 각 배너 슬라이드 -->
        <div v-for="(banner, index) in banners" :key="`slide-${index}`" class="swiper-slide">
          <!-- 배너 이미지 -->
          <div class="banner-image-container">
            <img :src="banner.image" :alt="banner.alt" class="banner-image" @error="handleImageError" />
          </div>

          <!-- 배너 텍스트 (각 슬라이드마다 개별) -->
          <div class="banner-overlay">
            <div class="banner-content">
              <h1 class="banner-title">{{ banner.title }}</h1>
              <p class="banner-subtitle">{{ banner.subtitle }}</p>
              <a :href="banner.link" class="banner-cta">
                <span>{{ banner.buttonText }}</span>
                <span class="cta-arrow">→</span>
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 인디케이터 -->
      <div class="swiper-pagination">
        <button v-for="(_, index) in banners" :key="index" class="pagination-bullet"
          :class="{ 'pagination-bullet-active': currentIndex === index }" @click="goToSlide(index)">
        </button>
      </div>

      <!-- 네비게이션 버튼 -->
      <button class="swiper-button-prev" @click="prevSlide">‹</button>
      <button class="swiper-button-next" @click="nextSlide">›</button>
    </section>

    <!-- Community Section -->
    <section class="community-section">
      <div class="community-container">
        <!-- 인기글 커뮤니티 카드 -->
        <PopularBoards />

        <!-- 인기포토 갤러리 -->
        <div class="photo-gallery">
          <div class="card-header">
            <h3 class="card-title">인기포토</h3>
            <a href="/board/photo" class="more-link">더보기 <span>⟩</span></a>
          </div>
          <div class="gallery-images">
            <div v-for="photo in popularPhotos" :key="photo.boardNo" class="gallery-item"
              @click="goToBoard(photo.boardCategory.id, photo.boardNo)">
              <img class="gallery-img" :src="photo.imageUrl" :alt="photo.boardTitle" />
              <div class="gallery-caption">
                <span>{{ photo.boardCategory.name }}</span>
                <span>{{ photo.boardTitle }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Adoption Section -->
    <AdoptionBanner />

    <!-- Features Section -->
    <section class="features-section">
      <div class="features-container">
        <div class="features-grid">
          <div class="features-row">
            <div class="feature-card">
              <div class="feature-number">01</div>
              <div class="feature-icon">❤️</div>
              <h3 class="feature-title">사랑으로 연결</h3>
              <p class="feature-description">새로운 가족을 기다리는 유기동물들의 정보를<br />편하게 확인해보세요.</p>
            </div>
            <div class="feature-card">
              <div class="feature-number">02</div>
              <div class="feature-icon">⭐</div>
              <h3 class="feature-title">특별한 추억</h3>
              <p class="feature-description">반려동물과의 소중한 순간들을 기록하고<br />공유해요.</p>
            </div>
          </div>
          <div class="features-row">
            <div class="feature-card">
              <div class="feature-number">03</div>
              <div class="feature-icon">🤝</div>
              <h3 class="feature-title">따뜻한 커뮤니티</h3>
              <p class="feature-description">같은 마음을 가진 사람들과 소통하고<br />정보를 나눠요.</p>
            </div>
            <div class="feature-card">
              <div class="feature-number">04</div>
              <div class="feature-icon">🛒</div>
              <h3 class="feature-title">우리가 만드는 마켓</h3>
              <p class="feature-description">반려인들이 직접 참여하는 따뜻한 마켓을<br />이용해보세요.</p>
            </div>
          </div>
        </div>

        <!-- 오른쪽 버튼들 -->
        <div class="features-buttons">
          <div class="matching-button" @click="goToMatching">
            <div class="matching-button-content">
              <div class="matching-button-title">🎯 운명의 반려동물 매칭하기</div>
              <div class="matching-button-description">성격과 라이프스타일을 분석해서<br />완벽한 파트너를 찾아보세요.</div>
            </div>
            <div class="matching-button-arrow"></div>
          </div>

          <div class="diary-button" @click="goToDiary">
            <div class="diary-button-content">
              <div class="diary-button-title">💕 반려동물 시선으로 보는 하루</div>
              <div class="diary-button-description">반려동물의 눈으로 바라본 하루를 일기로 남겨보세요.<br />오늘 있었던 특별한 순간들을 기록하고 추억을 만들어보세요.
              </div>
            </div>
            <div class="diary-button-arrow"></div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script>
import axios from 'axios'
import PopularBoards from './mainPage/PopularBoards.vue'
import AdoptionBanner from './mainPage/AdoptionBanner.vue'

export default {
  name: 'MainPage',
  components: {
    PopularBoards,
    AdoptionBanner
  },
  data() {
    return {
      // 배너 관련 데이터
      currentIndex: 0,
      isTransitioning: false,
      autoSlideTimer: null,
      isPaused: false,
      slideSpeed: 5000,

      banners: [
        {
          id: 1,
          image: '/image/jorgen-haland-fu0z-_iPa4M-unsplash (1).jpg',
          alt: '펫 거래 안전',
          title: 'How Can We Make Pet Trading Safer?',
          subtitle: '검증된 판매자와 함께하는 안심 거래',
          buttonText: '거래 방법 안내',
          link: '/trading-guide'
        },
        {
          id: 2,
          image: '/image/niklas-liniger-eyGyeByOjig-unsplash.jpg',
          alt: '반려동물 찾기',
          title: 'Find Your Perfect Pet Companion',
          subtitle: '나에게 맞는 반려동물을 찾아보세요',
          buttonText: '펫 찾기',
          link: '/find-pets'
        },
        {
          id: 3,
          image: '/image/rachel-kelli-0-X_N654aw4-unsplash.jpg',
          alt: '펫 케어 커뮤니티',
          title: 'Pet Care Tips & Community',
          subtitle: '반려동물 케어 팁과 커뮤니티',
          buttonText: '커뮤니티 참여',
          link: '/community'
        }
      ],

      // 커뮤니티 관련 데이터
      // popularBoards: [
      //   {
      //     boardNo: 1523,
      //     boardTitle: '귀여운 실키테리어 모리 안뇽하세용✌️',
      //     boardCategory: { id: 1, name: '입양후기 게시판' },
      //     user: { nickname: 'user123' },
      //     stats: { likes: 52, views: 2500 },
      //     createdAt: '2025-07-09T10:30:00Z'
      //   },
      //   {
      //     boardNo: 1524,
      //     boardTitle: '화이트 테리어 입양 고민되네여,,',
      //     boardCategory: { id: 2, name: '자유 게시판' },
      //     user: { nickname: 'petlover' },
      //     stats: { likes: 41, views: 2200 },
      //     createdAt: '2025-07-09T09:15:00Z'
      //   },
      //   {
      //     boardNo: 1525,
      //     boardTitle: '❗급해요 ️6개월 슈나우저 이갈이 관련 조언요!!',
      //     boardCategory: { id: 3, name: '육아 Q&A 게시판' },
      //     user: { nickname: 'dogmom' },
      //     stats: { likes: 22, views: 1800 },
      //     createdAt: '2025-07-09T08:45:00Z'
      //   },
      //   {
      //     boardNo: 1526,
      //     boardTitle: 'OO 선글라스 구입 후기 😎 (*강아지 사진 매우많음)',
      //     boardCategory: { id: 4, name: '쇼핑몰 후기 게시판' },
      //     user: { nickname: 'shoppingking' },
      //     stats: { likes: 25, views: 2300 },
      //     createdAt: '2025-07-09T07:20:00Z'
      //   },
      //   {
      //     boardNo: 1527,
      //     boardTitle: '저희 렉돌 자랑합니당',
      //     boardCategory: { id: 2, name: '자유 게시판' },
      //     user: { nickname: 'catlover' },
      //     stats: { likes: 15, views: 1200 },
      //     createdAt: '2025-07-09T06:10:00Z'
      //   },
      //   {
      //     boardNo: 1528,
      //     boardTitle: '반려동물 일기 관련 v 1.0 patch 후기',
      //     boardCategory: { id: 5, name: '공지사항 게시판' },
      //     user: { nickname: 'admin' },
      //     stats: { likes: 0, views: 3200 },
      //     createdAt: '2025-07-08T15:30:00Z'
      //   },
      //   {
      //     boardNo: 1529,
      //     boardTitle: '송파 지역 동물병원 추천 받아요!!!',
      //     boardCategory: { id: 2, name: '자유 게시판' },
      //     user: { nickname: 'seoul_pet' },
      //     stats: { likes: 13, views: 721 },
      //     createdAt: '2025-07-08T14:45:00Z'
      //   }
      // ],

      popularPhotos: [
        {
          boardNo: 1601,
          boardTitle: '귀여운 모리~',
          boardCategory: { id: 2, name: '자유게시판' },
          user: { nickname: 'user123' },
          imageUrl: '/image/mori.jpeg',
          stats: { likes: 89, views: 1200 },
          createdAt: '2025-07-09T11:00:00Z'
        },
        {
          boardNo: 1602,
          boardTitle: '귀여운 슈나우저~',
          boardCategory: { id: 6, name: '사진게시판' },
          user: { nickname: 'dogmom' },
          imageUrl: '/image/shu.png',
          stats: { likes: 76, views: 980 },
          createdAt: '2025-07-09T10:15:00Z'
        },
        {
          boardNo: 1603,
          boardTitle: '귀여운 웨스티 ~',
          boardCategory: { id: 2, name: '자유게시판' },
          user: { nickname: 'westielover' },
          imageUrl: '/image/white-westie.jpeg',
          stats: { likes: 64, views: 850 },
          createdAt: '2025-07-09T09:30:00Z'
        }
      ],

      // 입양안내 관련 데이터
      currentPetIndex: 0,
      adoptionPets: [
        {
          id: 1,
          name: '부',
          breed: '테리어믹스',
          gender: '여',
          age: '1세',
          location: '서울 용산구',
          organization: '사단법인 동물사랑 네스트',
          description: '겁이 많은 아이지만 사랑스러워요. 중성화 수술 완료했습니다.',
          image: '/image/boo-pet-shelter.jpeg'
        },
        {
          id: 2,
          name: '지용',
          breed: '코카-비숑 믹스',
          gender: '남',
          age: '3개월',
          location: '경기도 남양주',
          organization: 'happyhagae',
          description: '다른 아이들에게 치여도 언제나 가만히 있는 작고 귀여운 눈을 가진 아이랍니다.',
          image: '/image/maltese-shelter.jpeg'
        },
        {
          id: 3,
          name: '동주',
          breed: '진도',
          gender: '여',
          age: '4개월',
          location: '경기도 남양주',
          organization: 'happyhagae',
          description: '이름을 불러주면 꼬리를 사정없이 흔들어주는 이쁜 동주공주랍니다.',
          image: '/image/dongju-petshelter.jpeg'
        }
      ]
    }
  },

  computed: {
    swiperStyle() {
      return {
        transform: `translate3d(-${this.currentIndex * 100}%, 0px, 0px)`,
        transitionDuration: this.isTransitioning ? '600ms' : '0ms',
        transitionTimingFunction: 'ease-in-out'
      }
    }
  },

  mounted() {
    this.startAutoSlide()
    this.fetchPopularBoards()
    this.fetchPopularPhotos()
    this.fetchAdoptionPets()
  },

  beforeUnmount() {
    this.stopAutoSlide()
  },

  methods: {
    // 배너 관련 메서드들
    nextSlide() {
      if (this.isTransitioning) return

      this.isTransitioning = true
      this.currentIndex = (this.currentIndex + 1) % this.banners.length

      setTimeout(() => {
        this.isTransitioning = false
      }, 600)
    },

    prevSlide() {
      if (this.isTransitioning) return

      this.isTransitioning = true
      this.currentIndex = this.currentIndex === 0
        ? this.banners.length - 1
        : this.currentIndex - 1

      setTimeout(() => {
        this.isTransitioning = false
      }, 600)
    },

    goToSlide(index) {
      if (this.isTransitioning || index === this.currentIndex) return

      this.isTransitioning = true
      this.currentIndex = index

      setTimeout(() => {
        this.isTransitioning = false
      }, 600)

      this.resetAutoSlide()
    },

    startAutoSlide() {
      this.autoSlideTimer = setInterval(() => {
        if (!this.isPaused) {
          this.nextSlide()
        }
      }, this.slideSpeed)
    },

    stopAutoSlide() {
      if (this.autoSlideTimer) {
        clearInterval(this.autoSlideTimer)
        this.autoSlideTimer = null
      }
    },

    resetAutoSlide() {
      this.stopAutoSlide()
      this.startAutoSlide()
    },

    pauseAutoSlide() {
      this.isPaused = true
    },

    resumeAutoSlide() {
      this.isPaused = false
    },

    handleImageError(event) {
      console.error('이미지 로드 실패:', event.target.src)
      event.target.src = 'https://placehold.co/1920x650/cccccc/000000?text=Image+Not+Found'
    },

    // 커뮤니티 관련 메서드들
    formatViewCount(views) {
      if (views >= 1000) {
        return (views / 1000).toFixed(1) + 'K'
      }
      return views.toString()
    },

    goToBoard(categoryId, boardNo) {
      this.$router.push(`/board/${categoryId}/${boardNo}`)
    },

    async fetchPopularBoards() {
      try {
        console.log('인기글 요청 시작...')
        const { data } = await axios.get('/v1/board/popular', {
          params: {
            categoryNo: null,
            count: 7
          }
        })
        console.log('API 응답 데이터:', data)
        this.popularBoards = data
      } catch (error) {
        console.error('인기글 가져오기 실패:', error)
        console.error('에러 상세:', error.response?.data)
      }
    },

    async fetchPopularPhotos() {
      try {
        const response = await fetch('/api/boards/popular-photos?limit=3&period=daily')
        const data = await response.json()
        this.popularPhotos = data.boards
      } catch (error) {
        console.error('인기포토 가져오기 실패:', error)
      }
    },

    // 입양안내 관련 메서드들
    nextPet() {
      this.currentPetIndex = (this.currentPetIndex + 1) % this.adoptionPets.length
    },

    prevPet() {
      this.currentPetIndex = this.currentPetIndex === 0
        ? this.adoptionPets.length - 1
        : this.currentPetIndex - 1
    },

    async fetchAdoptionPets() {
      try {
        const response = await fetch('/api/adoption/pets?limit=10')
        const data = await response.json()
        this.adoptionPets = data.pets
      } catch (error) {
        console.error('입양동물 가져오기 실패:', error)
      }
    },

    // Features Section 버튼 클릭 메서드들
    goToMatching() {
      this.$router.push('/matching')
    },

    goToDiary() {
      this.$router.push('/diary')
    }
  }
}
</script>

<style scoped>
.hero {
  position: relative;
  width: 100%;
  height: 501px;
  /* 650px → 501px로 변경 */
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* Swiper 래퍼 */
.swiper-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  transition-property: transform;
  box-sizing: content-box;
}

/* 각 슬라이드 */
.swiper-slide {
  position: relative;
  width: 100%;
  height: 100%;
  flex: 0 0 100%;
  /* 각 슬라이드가 100% 너비 차지 */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 배너 이미지 컨테이너 */
.banner-image-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
}

/* 배너 오버레이 */
.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg,
      rgba(0, 0, 0, 0.4) 0%,
      rgba(0, 0, 0, 0.1) 50%,
      rgba(0, 0, 0, 0.4) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

/* 배너 콘텐츠 */
.banner-content {
  text-align: left;
  /* center → left로 변경 */
  color: white;
  max-width: 800px;
  padding: 0 20px;
  z-index: 3;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  /* 모든 요소를 왼쪽 정렬 */
}

.banner-title {
  width: auto;
  /* 텍스트 길이에 맞춰 자동 조정 */
  display: block;
  /* flex 제거하고 일반 블록으로 */
  color: #111111;
  font-size: 38px;
  font-weight: 600;
  line-height: 1.4;
  padding-left: 0;
  /* padding 제거 - 박스 시작점에서 텍스트 시작 */
  margin-bottom: 16px;
  white-space: nowrap;
  /* 줄바꿈 방지 */
  overflow: visible;
  /* 넘치는 텍스트 보이게 */
}

.banner-subtitle {
  width: auto;
  /* 텍스트 길이에 맞춰 자동 조정 */
  display: block;
  /* flex 제거하고 일반 블록으로 */
  color: #111111;
  font-size: 20px;
  font-weight: 400;
  line-height: 1.4;
  padding-left: 0;
  /* padding 제거 - 박스 시작점에서 텍스트 시작 */
  margin-bottom: 24px;
  white-space: nowrap;
  /* 줄바꿈 방지 */
  overflow: visible;
  /* 넘치는 텍스트 보이게 */
}

/* 배너 CTA */
.banner-cta {
  width: 158px;
  height: 60px;
  position: relative;
  overflow: hidden;
  outline: 1px white solid;
  outline-offset: -1px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-sizing: border-box;
  background: transparent;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  /* margin-top 제거 - 이미 subtitle에서 margin-bottom으로 간격 조정 */
}

.banner-cta:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.banner-cta span:first-child {
  color: white;
  font-size: 17px;
  font-family: 'Pretendard', sans-serif;
  font-weight: 400;
  line-height: 1.4;
  flex: 1;
}

.cta-arrow {
  color: white;
  font-size: 18px;
  font-weight: 300;
  transition: transform 0.3s ease;
}

.banner-cta:hover .cta-arrow {
  transform: translateX(4px);
}

/* 네비게이션 버튼 */
.swiper-button-prev,
.swiper-button-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.swiper-button-prev:hover,
.swiper-button-next:hover {
  background: white;
  transform: translateY(-50%) scale(1.1);
}

.swiper-button-prev {
  left: 20px;
}

.swiper-button-next {
  right: 20px;
}

/* 페이지네이션 */
.swiper-pagination {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 10;
}

.pagination-bullet {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-bullet-active {
  background: white;
  transform: scale(1.3);
}

.pagination-bullet:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.1);
}

.community-section {
  padding: 60px 0;
  background-color: #f8f9fa;
}

.community-section {
  padding: 60px 0;
  background-color: #f8f9fa;
}

.community-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  /* 가로 배치를 위한 flex */
  gap: 40px;
  /* 두 카드 사이 간격 */
  align-items: stretch;
  /* 높이를 동일하게 맞춤 */
}

.community-card {
  flex: 1;
  /* 동일한 비율로 공간 분할 */
  min-height: 400px;
  /* 최소 높이 설정 */
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.photo-gallery {
  flex: 1;
  /* 동일한 비율로 공간 분할 */
  min-height: 400px;
  /* 최소 높이 설정 */
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
}

.board-item {
  display: flex;
  justify-content: space-between;
  /* 양쪽 끝으로 배치 */
  align-items: center;
  /* 세로 중앙 정렬 */
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
  flex-direction: column;
  gap: 4px;
  flex: 1;
  /* 남은 공간 모두 차지 */
  min-width: 0;
  /* 텍스트 오버플로우 방지 */
}

.board-category {
  font-size: 12px;
  color: #888;
  font-weight: 500;
  white-space: nowrap;
  /* 줄바꿈 방지 */
}

.board-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  line-height: 1.4;
  overflow: hidden;
  /* 넘치는 텍스트 숨기기 */
  text-overflow: ellipsis;
  /* ... 표시 */
  white-space: nowrap;
  /* 줄바꿈 방지 */
  max-width: 100%;
  /* 최대 너비 제한 */
}

.board-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  /* 크기 고정 */
  min-width: 80px;
  /* 최소 너비 보장 */
}

.like-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.view-count {
  font-size: 12px;
  color: #666;
}

.gallery-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 40px;
}

.gallery-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  cursor: pointer;
}

.gallery-item:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

.gallery-img {
  width: 100%;
  height: 182px;
  object-fit: cover;
  border-radius: 8px;
}

.gallery-caption {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.gallery-caption span:first-child {
  font-size: 12px;
  color: #888;
}

.gallery-caption span:last-child {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* Features Section */
.features-section {
  padding: 40px 0 80px 0;
  background-color: #f8f9fa;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  display: flex;
  gap: 40px;
  /* 좌우 섹션 간격 */
}

.features-grid {
  display: flex;
  flex-direction: column;
  gap: 55px;
  flex: 1;
  /* 왼쪽 영역 */
}

/* 오른쪽 버튼 컨테이너 */
.features-buttons {
  width: 360px;
  /* 오른쪽 버튼 영역 고정 너비 */
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex-shrink: 0;
}

/* 매칭 버튼 */
.matching-button {
  width: 100%;
  height: 280px;
  background: linear-gradient(180deg, #E3F2FD 0%, #008BE6 100%);
  border: 1px solid #BFE6FF;
  border-radius: 12px;
  padding: 30px 25px;
  position: relative;
  cursor: pointer;
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-bottom: 60px;
}

.matching-button:hover {
  transform: translateY(-2px);
}

.matching-button-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.matching-button-title {
  font-size: 22px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  font-family: 'Pretendard', sans-serif;
}

.matching-button-description {
  font-size: 15px;
  font-weight: 500;
  color: #3D393A;
  line-height: 1.4;
  font-family: 'Pretendard', sans-serif;
}

.matching-button-arrow {
  position: absolute;
  bottom: 20px;
  left: 49px;
  width: 24px;
  height: 22px;
  background: rgba(211, 211, 211, 0.8);
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.matching-button-arrow::after {
  content: '';
  width: 12px;
  height: 12px;
  background: #111111;
  clip-path: polygon(0 0, 100% 50%, 0 100%);
}

/* 시선으로 보는 하루 버튼 */
.diary-button {
  width: 100%;
  height: 280px;
  background: linear-gradient(180deg, #FFE5E5 0%, #FFB3B3 100%);
  border: 1px solid #BFE6FF;
  border-radius: 12px;
  padding: 30px 25px;
  position: relative;
  cursor: pointer;
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-bottom: 60px;
}

.diary-button:hover {
  transform: translateY(-2px);
}

.diary-button-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.diary-button-title {
  font-size: 22px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  font-family: 'Pretendard', sans-serif;
}

.diary-button-description {
  font-size: 15px;
  font-weight: 500;
  color: #3D393A;
  line-height: 1.4;
  font-family: 'Pretendard', sans-serif;
}

.diary-button-arrow {
  position: absolute;
  bottom: 20px;
  left: 49px;
  width: 24px;
  height: 22px;
  background: rgba(211, 211, 211, 0.8);
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.diary-button-arrow::after {
  content: '';
  width: 12px;
  height: 12px;
  background: #111111;
  clip-path: polygon(0 0, 100% 50%, 0 100%);
}

.features-row {
  display: flex;
  gap: 82px;
  justify-content: center;
}

.feature-card {
  width: 357px;
  background: white;
  border-radius: 16px;
  padding: 40px 32px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.07);
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}

.feature-number {
  font-size: 25px;
  font-weight: 700;
  color: #333;
  font-family: 'Tahoma', sans-serif;
  line-height: 1.4;
  margin-bottom: 8px;
}

.feature-icon {
  font-size: 40px;
  line-height: 1.4;
  margin-bottom: 8px;
}

.feature-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  font-family: 'Tahoma', sans-serif;
  line-height: 1.4;
  margin-bottom: 12px;
}

.feature-description {
  font-size: 15px;
  font-weight: 400;
  color: #767676;
  font-family: 'Tahoma', sans-serif;
  line-height: 1.4;
  margin: 0;
}
</style>
