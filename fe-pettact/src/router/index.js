import { createRouter, createWebHistory } from "vue-router";

import userRoutes from "@/router/user/userRoutes";
import mypageRoutes from '@/router/user/mypageRoutes'
import adminRoutes from '@/router/admin/adminRoutes';
import productRoutes from "./product/productRoutes";
import petRoutes from "./pet/petRoutes";
import boardRoutes from "./board/boardRoutes";
import categoryRoutes from "./board/categoryRoutes";
import userPetRoutes from "./pet/userPetRoutes";
import Main from "@/components/Main.vue";

const routes = [
  { path: "/", name: "Main", component: Main },

  ...userRoutes,
  ...mypageRoutes,
  ...adminRoutes,

  ...productRoutes,
  ...petRoutes,
  ...userPetRoutes,

  ...boardRoutes,
  ...categoryRoutes
];

const router = createRouter({
  history: createWebHistory(),

  routes,
})

export default router;
