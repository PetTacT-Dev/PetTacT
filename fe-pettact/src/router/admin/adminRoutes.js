import AdminLayout from "@/view/admin/AdminLayout.vue";
import AdminBoardDetail from "@/view/admin/board/AdminBoardDetail.vue";
import AdminBoardList from "@/view/admin/board/AdminBoardList.vue";
import DashBoard from "@/view/admin/dashboard/DashBoard.vue";
import AdminReportDetail from "@/view/admin/report/AdminReportDetail.vue";
import AdminReportList from "@/view/admin/report/AdminReportList.vue";
import AdminSellerList from "@/view/admin/user/AdminSellerList.vue";
import AdminSellerRequests from "@/view/admin/user/AdminSellerRequests.vue";
import AdminUserDetail from "@/view/admin/user/AdminUserDetail.vue";
import AdminUserList from "@/view/admin/user/AdminUserList.vue";
import Log from "@/view/log/log.vue";


export default [
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: 'dashboard', name: 'adminDashboard', component: DashBoard },
      { path: 'user', name: 'adminUserList', component: AdminUserList },
      { path: 'user/:userNo', name: 'adminUserDetail', component: AdminUserDetail },
      { path: 'seller/requests', name: 'adminSellerRequests', component: AdminSellerRequests },
      { path: 'seller', name: 'adminSellerList', component: AdminSellerList },
      { path: 'board', name: 'adminBoardList', component: AdminBoardList },
      { path: 'board/:boardNo', name: 'adminBoardDetail', component: AdminBoardDetail },
      { path: 'report', name: 'adminReportList', component: AdminReportList },
      { path: 'report/:reportNo', name: 'adminReportDetail', component: AdminReportDetail },
      { path: "/log", name: "logs", component: Log }
    ]
  },
]