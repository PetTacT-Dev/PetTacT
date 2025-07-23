// src/stores/cart.js
import { defineStore } from 'pinia';

import axios from 'axios';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],
    loading: false,
    totalPages: 0,
    currentPage: 0,
    error: null,
  }),
  actions: {
    async fetchCart(page = 0, size = 5) { // 장바구니 목록
      this.loading = true;
      try {
        const res = await axios.get(`/v1/cart?page=${page}&size=${size}`);
        this.cartItems = res.data.content;
        this.totalPages = res.data.totalPages;
        this.currentPage = res.data.number;
      } catch (err) {
        this.error = err;
      } finally {
        this.loading = false;
      }
    },

    async addToCart(productNo, productStock) { // 장바구니 상품 추가
      try {
        await axios.post('/v1/cart/add', { productNo, productStock });
        await this.fetchCart(); // 갱신
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    
    async updateCartItem(productNo, productStock) { // 장바구니 수량 변경
      try {
        await axios.patch(`/v1/cart/${productNo}`, { productNo, productStock });
      } catch (err) {
        this.error = err;
        throw err;
      }
    },
    async deleteCartItem(cartNo) { // 장바구니 삭제
      try {
        await axios.delete(`/v1/cart/delete/${cartNo}`);
        // fetchCart() 대신 cartItems에서 직접 제거
        this.cartItems = this.cartItems.filter(item => item.cartNo !== cartNo);
      } catch (err) {
        this.error = err;
        throw err;  // 오류를 호출한 쪽으로 던져서 처리 가능하게
      }
    },
    // async deleteCartItem(cartNo) { // 장바구니 삭제
    //   try {
    //     await api.delete(`/v1/cart/delete/${cartNo}`);
    //     await this.fetchCart();
    //   } catch (err) {
    //     this.error = err;
    //   }
    // },
  },
});
