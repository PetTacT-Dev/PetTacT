import BoardList from '@/view/Board/BoardList.vue'

const boardRoutes = [
  { path: '/board/:categoryNo', name: 'BoardList', component: BoardList },
  { path: '/category/:categoryNo/create', name: 'BoardForm', component: () => import('@/view/Board/BoardForm.vue') },
   { path: '/board/detail/:boardNo', name: 'BoardDetail', component: () => import('@/view/Board/BoardDetail.vue'), props: true },
  { path: '/board/:boardNo/edit', name: 'BoardEdit', component: () => import('@/view/Board/BoardEdit.vue'), props: true }
]

export default boardRoutes