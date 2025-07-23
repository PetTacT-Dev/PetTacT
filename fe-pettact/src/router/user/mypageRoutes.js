import MyReportDetail from "@/view/mypage/components/myActivity/MyReportDetail.vue";
import MyActivity from "@/view/mypage/MyActivity.vue";
import MyInfo from "@/view/mypage/MyInfo.vue";
import MyInfoUpdate from "@/view/mypage/MyInfoUpdate.vue";
import MyMarket from "@/view/mypage/MyMarket.vue";
import MyPageLayout from "@/view/mypage/MyPageLayout.vue";

export default [
    {
      path: '/user/mypage',
      component: MyPageLayout,
      children: [
        { path: '', name: 'myInfo', component: MyInfo },
        { path: 'update', name: 'myInfoUpdate', component: MyInfoUpdate },
        { path: 'myMarket', name: 'myMarket', component: MyMarket },
        { path: 'myActivity', name: 'myActivity', component: MyActivity },
        { path: 'report/:reportNo', name: 'myReportDetail', component: MyReportDetail },
        // 마이페이지 추가예정
      ]
    },
]