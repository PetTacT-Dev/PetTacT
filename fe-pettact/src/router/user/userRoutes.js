import NotificationList from "@/components/notification/NotificationList.vue";
import EmailFind from "@/view/user/EmailFind.vue";
import EmailVerified from "@/view/user/EmailVerified.vue";
import Join from "@/view/user/Join.vue";
import Login from "@/view/user/Login.vue";
import Oauth2Success from "@/view/user/Oauth2-success.vue";
import PasswordFind from "@/view/user/PasswordFind.vue";
import PasswordReset from "@/view/user/PasswordReset.vue";
import SocialJoin from "@/view/user/SocialJoin.vue";

export default [
    { path: "/notifications", name: "notifications", component: NotificationList },
    { path: "/user/login", name: "login", component: Login },
    { path: "/oauth2/success", name: "oauth2Success", component: Oauth2Success },
    { path: "/user/join", name: "join", component: Join },
    { path: "/user/social/join", name: "socialJoin", component: SocialJoin },
    { path: "/user/email/verify", name: "emailVerify", component: EmailVerified },
    { path: "/user/email/find", name: "emailFind", component: EmailFind },
    { path: "/user/password/find", name: "passwordFind", component: PasswordFind },
    { path: "/user/password/reset", name: "passwordReset", component: PasswordReset },
]