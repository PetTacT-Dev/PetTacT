import { defineStore } from 'pinia';
import axios from 'axios';

export const useProductStore = defineStore('product', {
  state: () => ({
    productList: [],
    productDetail: null,
    categories: [],      // 카테고리 상태 추가
    totalPages: 0,
    totalElements: 0,
    currentPage: 0,
    loading: false,
    error: null,
  }),
  actions: {

      async fetchCategories() {
        try {
          const res = await axios.get('/v1/product/categories'); // ← 이 경로로 수정
          console.log('카테고리 API 응답:', res.data);
          this.categories = res.data.items; // ← items 배열만 할당
          console.log('저장된 categories 상태:', this.categories);
        } catch (err) {
          console.error('카테고리 불러오기 실패:', err);
          
          this.error = err;
        }
      },

      // ✅ 검색어를 query string으로 포함
      async fetchProductList(keyword = '', categoryNo = '', sort = '', page = 0, size = 10) {
        this.loading = true;
        try {
          const res = await axios.get('/v1/product', {
            params: {
              keyword: keyword.trim(),
              categoryNo: categoryNo || undefined,
              sort: sort || undefined,
              page,   // 🔥 페이지 번호 (0부터 시작)
              size    // 🔥 페이지 크기
            }
          });

          // 🔥 응답 구조: Page<ProductDTO>
          this.productList = res.data.content;         // 실제 상품 목록
          this.totalPages = res.data.totalPages;       // 총 페이지 수
          this.totalElements = res.data.totalElements; // 전체 상품 수
          this.currentPage = res.data.number;          // 현재 페이지 번호 (0부터)
        } catch (err) {
          this.error = err;
        } finally {
          this.loading = false;
        }
      },

    async fetchProductDetail(productNo) { // 상품 상세
      this.loading = true;
      try {
        const res = await axios.get(`/v1/product/${productNo}`);
        this.productDetail = res.data;
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },

    async createProduct(productData, files = []) {
      this.loading = true;
      try {
        const formData = new FormData();
        formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));

        console.log("JSON PRODUCTDATA", JSON.stringify(productData))

        files.forEach((file) => {
          formData.append('files', file);
        });

            // ✅👀 여기에서 FormData 디버깅 로그 출력
        for (let pair of formData.entries()) {
          console.log('[FormData]', pair[0], pair[1]);
        }

    const res = await axios.post('/v1/product/create', formData, {
    headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return res.data;  // <---- 여기서 서버가 보내준 productNo가 포함되어야 함
  } catch (err) {
    this.error = err;
    throw err;
  } finally {
    this.loading = false;
  }
},

//     async createProduct(productData) { // 상품 등록
//       this.loading = true;
//       try {
//         await api.post('/v1/product/create', productData);

// return res.data; //sdadsawldnj

//       } catch (err) {
//         this.error = err;
//         throw err;
//       } finally {
//         this.loading = false;
//       }
//     },

    // async updateProduct(productNo, productData) { // 상품 수정
    //   this.loading = true;
    //   try {
    //     await api.put(`/v1/product/update/${productNo}`, productData);
    //   } catch (err) {
    //     this.error = err;
    //     throw err;
    //   } finally {
    //     this.loading = false;
    //   }
    // },
    async updateProduct(productNo, productData, selectedFiles = []) {
      this.loading = true;

      try {
        const formData = new FormData();

        // ✅ 1. DTO(JSON)를 문자열로 감싸서 FormData에 추가
        const jsonBlob = new Blob([JSON.stringify(productData)], { type: "application/json" });
        formData.append("dto", jsonBlob); // 서버에서는 @RequestPart("dto")로 받아야 함

        // ✅ 2. 파일 배열을 하나씩 추가
        selectedFiles.forEach((file) => {
          formData.append("files", file); // 서버에서는 @RequestPart("files")로 받아야 함
        });

        // ✅ 3. API 요청 보내기
        await axios.put(`/v1/product/update/${productNo}`, formData, {
        });

      } catch (err) {
        this.error = err;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async deleteProduct(productNo) {// 상품 삭제
      this.error = null;
      try {
        await axios.patch(`/v1/product/delete/${productNo}`);
        // 성공 시 상태 초기화
        this.productDetail = null;
      } catch (err) {
        this.error = err;
      }
    },
  },
});