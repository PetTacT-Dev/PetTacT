import UserPetList from "@/view/UserPet/UserPetList.vue";
import UserPetRegister from "@/view/UserPet/UserPetRegister.vue";
import UserPetUpdate from "@/view/UserPet/UserPetUpdate.vue";
import UserPetDetail from "@/view/UserPet/UserPetDetail.vue";
import DiaryList from "@/view/UserPet/diaryList.vue";
import DiaryRegister from "@/view/UserPet/diaryRegister.vue";


export default [
    { path: "/userPet", name: "userPetList", component: UserPetList },
    { path: "/userPet/register", name: "userPetRegister", component: UserPetRegister },
    { path: "/userPet/update/:petId", name: "userPetUpdate", component: UserPetUpdate },
    { path: "/userPet/detail/:petId", name: "userPetDetail", component: UserPetDetail },
    { path: "/userPet/diary/:petId", name: "diaryList", component: DiaryList },
    { path: "/userPet/diary/register", name: "diaryRegister", component: DiaryRegister },
]