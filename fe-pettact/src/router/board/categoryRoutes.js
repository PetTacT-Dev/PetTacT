import BoardCategoryForm from '@/view/Category/BoardCategoryForm.vue';
import BoardCategoryList from '@/view/Category/BoardCategoryList.vue';
import BoardCategoryDetail from '@/view/Category/BoardCategoryDetail.vue';
import BoardCategoryEdit from '@/view/Category/BoardCategoryEdit.vue';

const categoryRoutes = [
  { path: '/boardCategoryForm', name: 'BoardCategoryForm', component: BoardCategoryForm },
  { path: '/boardCategoryList', name: 'BoardCategoryList', component: BoardCategoryList },
  { path: '/boardCategoryDetail/:categoryNo', name: 'BoardCategoryDetail', component: BoardCategoryDetail, props: true },
  { path: '/boardCategory/:categoryNo/edit', name: 'BoardCategoryEdit', component: BoardCategoryEdit, props: true }
]

export default categoryRoutes