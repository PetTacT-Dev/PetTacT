<template>
  <!-- 댓글 허용 여부 체크 -->
  <div v-if="allowReply" class="replies-container">
    <!-- 인기 댓글 -->
    <div class="popular-replies" v-if="popularReplies.length > 0">
      <h3>인기 댓글</h3>
      <div
        v-for="reply in popularReplies"
        :key="'popular-' + reply.replyNo"
        class="reply-item"
      >
        <div class="reply-content" :style="{ marginLeft: (reply.depth * 20) + 'px' }">
          <div class="reply-header">
            <span class="nickname">{{ reply.userNickname }}</span>
            <span class="date">{{ formatDate(reply.createdAt) }}</span>
            <span class="recommend-count" v-if="reply.recommendCount > 0">
              👍 {{ reply.recommendCount }}
            </span>
            <span class="popular-badge">인기</span>
          </div>
          
          <!-- 댓글 내용 (일반 보기/수정 모드) -->
          <div v-if="editingReply !== reply.replyNo" class="reply-text">
            {{ reply.content }}
          </div>
          
          <!-- 댓글 수정 폼 -->
          <div v-else class="reply-edit-form">
            <textarea 
              v-model="editReplyContent" 
              placeholder="댓글을 수정하세요..." 
              rows="3" 
              class="edit-textarea"
            />
            <div class="form-actions">
              <button @click="submitEditReply(reply.replyNo)" :disabled="!editReplyContent.trim()">
                수정 완료
              </button>
              <button @click="cancelEditReply">취소</button>
            </div>
          </div>
          
          <div class="reply-actions">
            <button @click="toggleReplyForm(reply.replyNo)" class="action-btn reply-btn">
              답글
            </button>
            
            <!-- 추천 버튼 (토글 방식) -->
            <button 
              @click="toggleReplyRecommend(reply.replyNo)" 
              :class="['action-btn', 'recommend-btn', { 
                'active': isReplyRecommended(reply.replyNo),
                'recommending': isReplyRecommending(reply.replyNo)
              }]"
              :disabled="isReplyRecommending(reply.replyNo)"
            >
              <span v-if="!isReplyRecommending(reply.replyNo)">
                {{ isReplyRecommended(reply.replyNo) ? '👍 추천 취소' : '👍 추천하기' }}
              </span>
              <span v-else>처리중...</span>
            </button>
            
            <!-- 수정 버튼 (작성자만) -->
            <button 
              v-if="canDelete(reply)" 
              @click="startEditReply(reply)" 
              class="action-btn edit-btn"
              :disabled="editingReply === reply.replyNo"
            >
              수정
            </button>
            
            <!-- 삭제 버튼 (작성자/관리자만) -->
            <button 
              v-if="canDelete(reply)" 
              @click="deleteReply(reply.replyNo)" 
              class="action-btn delete-btn"
            >
              삭제
            </button>
          </div>
          
          <!-- 답글 작성 폼 -->
          <div v-if="showReplyForm === reply.replyNo" class="reply-form">
            <textarea 
              v-model="newReplyContent" 
              placeholder="답글 입력..." 
              rows="3" 
              class="reply-textarea"
            />
            <div class="form-actions">
              <button @click="submitReply(reply.replyNo)" :disabled="!newReplyContent.trim()">
                답글 작성
              </button>
              <button @click="cancelReply">취소</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 전체 댓글 -->
    <div class="all-replies">
      <h3>전체 댓글 ({{ totalElements }}개)</h3>
      <div class="new-reply-form">
        <textarea v-model="newMainReplyContent" placeholder="댓글을 입력하세요..." rows="3" />
        <div class="form-actions">
          <button @click="submitNewReply" :disabled="!newMainReplyContent.trim()">댓글 작성</button>
        </div>
      </div>

      <div class="replies-list">
        <div v-for="reply in replies" :key="reply.replyNo" class="reply-item">
            <!-- ✅ 클래스 기반으로 변경 -->
            <div :class="['reply-content', `depth-${reply.depth}`]">
                <!-- <div style="background: yellow; padding: 2px; font-size: 10px;">
                depth: {{ reply.depth }}, replyNo: {{ reply.replyNo }}
                </div> -->
                <div class="reply-header">
                    <span class="nickname">{{ reply.userNickname }}</span>
                    <span class="date">{{ formatDate(reply.createdAt) }}</span>
                    <span class="recommend-count" v-if="reply.recommendCount > 0">
                        👍 {{ reply.recommendCount }}
                    </span>
                </div>
            
            <!-- 댓글 내용 (일반 보기/수정 모드) -->
            <div v-if="editingReply !== reply.replyNo" class="reply-text">
              {{ reply.content }}
            </div>
            
            <!-- 댓글 수정 폼 -->
            <div v-else class="reply-edit-form">
              <textarea 
                v-model="editReplyContent" 
                placeholder="댓글을 수정하세요..." 
                rows="3" 
                class="edit-textarea"
              />
              <div class="form-actions">
                <button @click="startEditReply(reply)" class="action-btn edit-btn">
                <!-- <button @click="submitEditReply(reply.replyNo)" :disabled="!editReplyContent.trim()"> -->
                  수정 완료
                </button>
                <!-- <button @click="cancelEditReply">삭제</button> -->
                <button @click="deleteReply(reply.replyNo)" class="action-btn delete-btn"></button>
              </div>
            </div>
            
            <div class="reply-actions">
              <button @click="toggleReplyForm(reply.replyNo)" class="action-btn reply-btn">
                답글
              </button>
              
              <!-- 추천 버튼 (토글 방식) -->
              <button 
                @click="toggleReplyRecommend(reply.replyNo)" 
                :class="['action-btn', 'recommend-btn', { 
                  'active': isReplyRecommended(reply.replyNo),
                  'recommending': isReplyRecommending(reply.replyNo)
                }]"
                :disabled="isReplyRecommending(reply.replyNo)"
              >
                <span v-if="!isReplyRecommending(reply.replyNo)">
                  {{ isReplyRecommended(reply.replyNo) ? '👍 추천 취소' : '👍 추천하기' }}
                </span>
                <span v-else>처리중...</span>
              </button>
              
              <!-- 수정 버튼 (작성자만) -->
              <button 
                v-if="canEdit(reply)" 
                @click="startEditReply(reply)" 
                class="action-btn edit-btn"
                :disabled="editingReply === reply.replyNo"
              >
                수정
              </button>
              
              <!-- 삭제 버튼 (작성자/관리자만) -->
              <button 
                v-if="canDelete(reply)" 
                @click="deleteReply(reply.replyNo)" 
                class="action-btn delete-btn"
              >
                삭제
              </button>
            </div>
            
            <!-- 답글 작성 폼 -->
            <div v-if="showReplyForm === reply.replyNo" class="reply-form">
              <textarea 
                v-model="newReplyContent" 
                placeholder="답글 입력..." 
                rows="3" 
                class="reply-textarea"
              />
              <div class="form-actions">
                <button @click="submitReply(reply.replyNo)" :disabled="!newReplyContent.trim()">
                  답글 작성
                </button>
                <button @click="cancelReply">취소</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 페이징 -->
    <div class="pagination" v-if="totalPages > 1">
      <button
        v-for="page in totalPages"
        :key="page"
        @click="changePage(page)"
        :class="{ active: currentPage === page }"
      >
        {{ page }}
      </button>
    </div>
  </div>

  <!-- 댓글 비허용 시 안내 문구 -->
  <div v-else class="no-replies-message">
    이 게시글은 댓글 기능이 비활성화되어 있습니다.
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  boardNo: {
    type: Number,
    required: true
  },
  allowReply: {
    type: Boolean,
    default: true  // 기본값은 댓글 허용
  }
})

const userStore = useUserStore()

// 댓글 관련 데이터
const replies = ref([])
const popularReplies = ref([])

// 댓글 작성 관련
const newMainReplyContent = ref('')
const newReplyContent = ref('')
const showReplyForm = ref(null)

// 댓글 수정 관련
const editingReply = ref(null)  // 현재 수정 중인 댓글 ID
const editReplyContent = ref('') // 수정 중인 댓글 내용

// 페이지네이션
const currentPage = ref(1)
const pageSize = ref(20)
const totalPages = ref(0)
const totalElements = ref(0)

// 추천 상태 관리
const replyRecommendStatus = ref({}) // { replyNo: { isRecommended: boolean, isRecommending: boolean } }

// 페이지 변경
const changePage = (page) => {
  currentPage.value = page
  loadReplies()
}

// 댓글 목록 로드
const loadReplies = async () => {
  try {
    const response = await axios.get(`/v1/board/${props.boardNo}/replies`, {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })

    console.log('받은 댓글 데이터:', response.data.content)
    console.log('댓글 개수:', response.data.content.length)
  
    response.data.content.forEach((reply, index) => {
      console.log(`${index}: replyNo=${reply.replyNo}, depth=${reply.depth}, parentReplyNo=${reply.parentReplyNo || 'null'}, content="${reply.content}"`)
    })
    
    // ✅ depth별 댓글 개수 확인
    const depthCount = response.data.content.reduce((acc, reply) => {
      acc[reply.depth] = (acc[reply.depth] || 0) + 1
      return acc
    }, {})
    console.log('depth별 댓글 개수:', depthCount)
    
    const parentReplies = response.data.content.filter(reply => reply.depth === 0)
    const childReplies = response.data.content.filter(reply => reply.depth > 0)
    console.log('부모 댓글 개수:', parentReplies.length)
    console.log('자식 댓글 개수:', childReplies.length)
    console.log('자식 댓글 목록:', childReplies.map(r => `${r.replyNo}(부모:${r.parentReplyNo})`))

    replies.value = response.data.content
    totalPages.value = response.data.totalPages
    totalElements.value = response.data.totalElements
    
    // 각 댓글의 추천 상태 초기화
    await loadReplyRecommendStatus()
    
  } catch (error) {
    console.error('댓글 로드 실패:', error)
  }
}

// 인기 댓글 로드
const loadPopularReplies = async () => {
  if (!props.boardNo) return
  try {
    const response = await axios.get(`/v1/board/${props.boardNo}/replies/popular`)
    popularReplies.value = response.data
  } catch (error) {
    console.error('인기 댓글 로드 실패:', error)
  }
}

// 댓글 추천 상태 로드
const loadReplyRecommendStatus = async () => {
  const isUserLoggedIn = userStore.user && localStorage.getItem('accessToken')
  if (!isUserLoggedIn) return
  
  const allReplies = [...replies.value, ...popularReplies.value]
  
  for (const reply of allReplies) {
    try {
      const response = await axios.get(`/v1/replies/${reply.replyNo}/recommend`)
      replyRecommendStatus.value[reply.replyNo] = {
        isRecommended: response.data,
        isRecommending: false
      }
    } catch (error) {
      console.error(`댓글 ${reply.replyNo} 추천 상태 확인 실패:`, error)
      replyRecommendStatus.value[reply.replyNo] = {
        isRecommended: false,
        isRecommending: false
      }
    }
  }
}

// 새 댓글 작성
const submitNewReply = async () => {
  if (!newMainReplyContent.value.trim()) return
  console.log("💬 새 댓글 전송 시도:", newMainReplyContent.value)

  try {
    await axios.post(`/v1/board/${props.boardNo}/replies`, {
      content: newMainReplyContent.value,
      parentReplyNo: null
    })
    newMainReplyContent.value = ''
    await loadReplies()
    await loadPopularReplies()
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    alert('댓글 작성에 실패했습니다.')
  }
}

// 답글 작성
const submitReply = async (parentReplyNo) => {
  if (!newReplyContent.value.trim()) return
  
  const requestData = {
    content: newReplyContent.value,
    parentReplyNo
  }

  try {
    const response = await axios.post(`/v1/board/${props.boardNo}/replies`, requestData)
    console.log("✅ 응답 데이터:", response.data)

    newReplyContent.value = ''
    showReplyForm.value = null
    await loadReplies()
    await loadPopularReplies()

  } catch (error) {
    console.error('답글 작성 실패:', error)
    alert('답글 작성에 실패했습니다.')
  }
}

// 댓글 추천 토글
const toggleReplyRecommend = async (replyNo) => {
  console.log('🎯 댓글 추천 토글 호출 - replyNo:', replyNo)
  
  // 로그인 확인
  const isUserLoggedIn = userStore.user && localStorage.getItem('accessToken')
  
  if (!isUserLoggedIn) {
    console.log('❌ 로그인되지 않은 사용자')
    alert('로그인이 필요한 기능입니다.')
    return
  }

  // 추천 상태 초기화 (없으면)
  if (!replyRecommendStatus.value[replyNo]) {
    replyRecommendStatus.value[replyNo] = {
      isRecommended: false,
      isRecommending: false
    }
  }

  // 이미 처리 중이면 리턴
  if (replyRecommendStatus.value[replyNo].isRecommending) {
    return
  }

  replyRecommendStatus.value[replyNo].isRecommending = true

  try {
    const currentStatus = replyRecommendStatus.value[replyNo].isRecommended
    
    if (currentStatus) {
      // 추천 취소
      console.log('📤 댓글 추천 취소 요청 전송 - replyNo:', replyNo)
      await axios.delete(`/v1/replies/${replyNo}/recommend`)
      console.log('✅ 댓글 추천 취소 성공')
      
      replyRecommendStatus.value[replyNo].isRecommended = false
      
      // 댓글 카운트 수동 업데이트
      updateReplyRecommendCount(replyNo, -1)
      
      // ✅ 인기 댓글 → 일반 댓글 이동 체크
      checkAndMoveFromPopularToNormal(replyNo)
      
    } else {
      // 추천 하기
      console.log('📤 댓글 추천 생성 요청 전송 - replyNo:', replyNo)
      await axios.post(`/v1/replies/${replyNo}/recommend`)
      console.log('✅ 댓글 추천 생성 성공')
      
      replyRecommendStatus.value[replyNo].isRecommended = true
      
      // 댓글 카운트 수동 업데이트
      updateReplyRecommendCount(replyNo, 1)
      
      // ✅ 일반 댓글 → 인기 댓글 이동 체크
      checkAndMoveFromNormalToPopular(replyNo)
    }
    
  } catch (error) {
    console.error('❌ 댓글 추천 토글 실패:', error)
    console.error('❌ 에러 상세:', error.response?.data)
    alert('댓글 추천 처리 중 오류가 발생했습니다.')
  } finally {
    replyRecommendStatus.value[replyNo].isRecommending = false
  }
}

// 댓글 추천 수 업데이트 헬퍼 함수
const updateReplyRecommendCount = (replyNo, delta) => {
  // 일반 댓글에서 찾기
  const replyIndex = replies.value.findIndex(reply => reply.replyNo === replyNo)
  if (replyIndex !== -1) {
    replies.value[replyIndex].recommendCount = Math.max(0, replies.value[replyIndex].recommendCount + delta)
    console.log('📊 일반 댓글 카운트 업데이트:', replies.value[replyIndex].recommendCount)
  }
  
  // 인기 댓글에서도 찾기
  const popularIndex = popularReplies.value.findIndex(reply => reply.replyNo === replyNo)
  if (popularIndex !== -1) {
    popularReplies.value[popularIndex].recommendCount = Math.max(0, popularReplies.value[popularIndex].recommendCount + delta)
    console.log('📊 인기 댓글 카운트 업데이트:', popularReplies.value[popularIndex].recommendCount)
  }
}

// ✅ 일반 댓글 → 인기 댓글 이동
const checkAndMoveFromNormalToPopular = (replyNo) => {
  const replyIndex = replies.value.findIndex(reply => reply.replyNo === replyNo)
  
  if (replyIndex !== -1) {
    const reply = replies.value[replyIndex]
    
    // 추천 수가 1개 이상이면 인기 댓글로 이동
    if (reply.recommendCount >= 1) {
      console.log('📈 일반 댓글 → 인기 댓글 이동:', reply.replyNo)
      
      // 인기 댓글에 추가
      popularReplies.value.push(reply)
      
      // 일반 댓글에서 제거
      replies.value.splice(replyIndex, 1)
      
      // 총 개수 업데이트
      totalElements.value = replies.value.length
    }
  }
}

// ✅ 인기 댓글 → 일반 댓글 이동
const checkAndMoveFromPopularToNormal = (replyNo) => {
  const popularIndex = popularReplies.value.findIndex(reply => reply.replyNo === replyNo)
  
  if (popularIndex !== -1) {
    const reply = popularReplies.value[popularIndex]
    
    // 추천 수가 1개 미만이면 일반 댓글로 이동
    if (reply.recommendCount < 1) {
      console.log('📉 인기 댓글 → 일반 댓글 이동:', reply.replyNo)
      
      // 일반 댓글에 추가
      replies.value.push(reply)
      
      // 인기 댓글에서 제거
      popularReplies.value.splice(popularIndex, 1)
      
      // 총 개수 업데이트
      totalElements.value = replies.value.length
    }
  }
}

// 댓글 수정 시작
const startEditReply = (reply) => {
  console.log('📝 댓글 수정 시작 - replyNo:', reply.replyNo)
  editingReply.value = reply.replyNo
  editReplyContent.value = reply.content
  
  // 다른 폼들 닫기
  showReplyForm.value = null
  newReplyContent.value = ''
}

// 댓글 수정 완료
const submitEditReply = async (replyNo) => {
  if (!editReplyContent.value.trim()) {
    alert('수정할 내용을 입력해주세요.')
    return
  }

  console.log('📝 댓글 수정 제출 - replyNo:', replyNo, 'content:', editReplyContent.value)

  try {
    await axios.put(`/v1/replies/${replyNo}`, {
      content: editReplyContent.value.trim()
    })
    
    console.log('✅ 댓글 수정 성공')
    
    // 수정 모드 종료
    editingReply.value = null
    editReplyContent.value = ''
    
    // 댓글 목록 새로고침
    await loadReplies()
    await loadPopularReplies()
    
    alert('댓글이 수정되었습니다.')
    
  } catch (error) {
    console.error('❌ 댓글 수정 실패:', error)
    console.error('❌ 에러 상세:', error.response?.data)
    alert('댓글 수정에 실패했습니다.')
  }
}

// 댓글 수정 취소
const cancelEditReply = () => {
  console.log('📝 댓글 수정 취소')
  editingReply.value = null
  editReplyContent.value = ''
}

// 댓글 삭제
const deleteReply = async (replyNo) => {
  if (!confirm('정말로 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/v1/replies/${replyNo}`)
    await loadReplies()
    await loadPopularReplies()
    alert('댓글이 삭제되었습니다.')
  } catch (error) {
    console.error('삭제 실패:', error)
    alert('삭제에 실패했습니다.')
  }
}

// 답글 폼 토글
const toggleReplyForm = (replyNo) => {
  showReplyForm.value = showReplyForm.value === replyNo ? null : replyNo
  newReplyContent.value = ''
  
  // 수정 모드 닫기
  editingReply.value = null
  editReplyContent.value = ''
}

// 답글 작성 취소
const cancelReply = () => {
  showReplyForm.value = null
  newReplyContent.value = ''
}

// 권한 확인 함수들
const canDelete = (reply) => {
  // userStore.user 안에 있는 실제 필드명 사용
  const currentUserNickname = userStore.user?.nickname || userStore.user?.userNickname
  const isAdmin = userStore.user?.isAdmin || userStore.user?.role === 'ADMIN'
  
  console.log('canDelete 체크:', {
    replyUserNickname: reply.userNickname,
    currentUserNickname,
    isAdmin,
    result: reply.userNickname === currentUserNickname || isAdmin
  })
  
  return reply.userNickname === currentUserNickname || isAdmin
}

const canEdit = (reply) => {
  const currentUserNickname = userStore.user?.nickname || userStore.user?.userNickname
  const isAdmin = userStore.user?.isAdmin || userStore.user?.role === 'ADMIN'
  
  return reply.userNickname === currentUserNickname || isAdmin
}

// 댓글 추천 상태 확인 헬퍼 함수
const isReplyRecommended = (replyNo) => {
  return replyRecommendStatus.value[replyNo]?.isRecommended || false
}

// 댓글 추천 처리 중 상태 확인 헬퍼 함수
const isReplyRecommending = (replyNo) => {
  return replyRecommendStatus.value[replyNo]?.isRecommending || false
}

// 날짜 포맷팅
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 컴포넌트 마운트시 실행
onMounted(async () => {
  console.log('ReplyItem mounted with boardNo:', props.boardNo, 'allowReply:', props.allowReply)
  await loadReplies()
  await loadPopularReplies()
})

// 템플릿에서 사용할 함수들 expose
defineExpose({
  toggleReplyRecommend,
  isReplyRecommended,
  isReplyRecommending,
  startEditReply,
  submitEditReply,
  cancelEditReply
})
</script>

<style scoped>
.replies-container {
  margin-top: 20px;
}

.popular-replies {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.popular-badge {
  background: #ff6b6b;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: bold;
}

.reply-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f5f5f5;
}

.reply-btn:hover {
  border-color: #007bff;
  color: #007bff;
}

.recommend-btn {
  border-color: #28a745;
  color: #28a745;
}

.recommend-btn.active {
  background: #28a745;
  color: white;
}

.recommend-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.edit-btn {
  border-color: #ffc107;
  color: #ffc107;
}

.edit-btn:hover {
  background: #ffc107;
  color: white;
}

.delete-btn {
  border-color: #dc3545;
  color: #dc3545;
}

.delete-btn:hover {
  background: #dc3545;
  color: white;
}

.reply-edit-form {
  margin: 8px 0;
}

.edit-textarea,
.reply-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.form-actions button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.form-actions button:first-child {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.form-actions button:first-child:hover {
  background: #0056b3;
}

.form-actions button:first-child:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-actions button:last-child {
  background: #6c757d;
  color: white;
  border-color: #6c757d;
}

.form-actions button:last-child:hover {
  background: #545b62;
}

.reply-item {
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}

.reply-content {
  transition: margin-left 0.2s ease;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 8px;
  /* position: relative; */
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.nickname {
  font-weight: bold;
  color: #333;
}

.date {
  color: #666;
  font-size: 12px;
}

/* 부모 댓글 (depth=0) */
.reply-content[style*="marginLeft: 0px"] {
  background-color: #ffffff;
  border: 1px solid #e9ecef;
}

.reply-content.depth-0 {
  margin-left: 0px !important;
  background-color: #ffffff !important;
  border: 1px solid #e9ecef !important;
}

.reply-content.depth-1 {
  margin-left: 40px !important;
  background-color: #f8f9fa !important;
  border-left: 4px solid #007bff !important;
  border: 1px solid #dee2e6 !important;
}

.reply-content.depth-2 {
  margin-left: 80px !important;
  background-color: #fff3cd !important;
  border-left: 4px solid #ffc107 !important;
  border: 1px solid #ffeaa7 !important;
}

.reply-content.depth-3 {
  margin-left: 120px !important;
  background-color: #d4edda !important;
  border-left: 4px solid #28a745 !important;
}

/* 호버 효과 개선 */
.reply-content:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 대댓글 연결선 (선택사항) */
.reply-content[style*="marginLeft: 20px"] {
  position: relative;
}

.reply-content[style*="marginLeft: 20px"]::after {
  content: "";
  position: absolute;
  left: -10px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #007bff;
  opacity: 0.3;
}


.recommend-count {
  color: #28a745;
  font-size: 12px;
  font-weight: bold;
}

.reply-text {
  color: #333;
  line-height: 1.5;
  margin-bottom: 8px;
  white-space: pre-wrap;
}

.new-reply-form {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.new-reply-form textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 5px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
}

.pagination button.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.no-replies-message {
  text-align: center;
  padding: 40px;
  color: #666;
  background: #f8f9fa;
  border-radius: 8px;
  margin-top: 20px;
}
</style>
