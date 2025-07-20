import PetAbandonmentList from "@/view/Abandonment/PetAbandonmentList.vue";
import PetAbandonmentDetail from "@/view/Abandonment/PetAbandonmentDetail.vue";

import PetFacilityList from "@/view/Facility/PetFacilityList.vue";
import PetFacilityDetail from "@/view/Facility/PetFacilityDetail.vue";

import PetShelterList from "@/view/Shelter/PetShelterList.vue";
import PetShelterDetail from "@/view/Shelter/PetShelterDetail.vue";
import PetShelterRegister from "@/view/Shelter/PetShelterRegister.vue";
import PetShelterUpdate from "@/view/Shelter/PetShelterUpdate.vue";

import UserPetList from "@/view/UserPet/UserPetList.vue";
import UserPetRegister from "@/view/UserPet/UserPetRegister.vue";
import UserPetUpdate from "@/view/UserPet/UserPetUpdate.vue";
import UserPetDetail from "@/view/UserPet/UserPetDetail.vue";
import DiaryList from "@/view/UserPet/diaryList.vue";
import DiaryRegister from "@/view/UserPet/diaryRegister.vue";

export default [
    // ================================
    // Abandonment
    // ================================
    { path: "/abandonment", name: "abandonmentList", component: PetAbandonmentList },
    { path: "/abandonment/:desertionNo", name: "abandonmentDetail", component: PetAbandonmentDetail },

    // ================================
    // Facility
    // ================================
    { path: "/facility", name: "facilityList", component: PetFacilityList },
    { path: "/facility/:facilityNo", name: "facilityDetail", component: PetFacilityDetail },

    // ================================
    // Shelter
    // ================================
    { path: "/shelter", name: "shelterList", component: PetShelterList },
    { path: "/shelter/register", name: "shelterRegister", component: PetShelterRegister },
    { path: "/shelter/:shelterNo", name: "shelterDetail", component: PetShelterDetail },
    { path: "/shelter/update/:shelterNo", name: "shelterUpdate", component: PetShelterUpdate },

    // ================================
    // User Pet
    // ================================
    { path: "/userPet", name: "userPetList", component: UserPetList },
    { path: "/userPet/register", name: "userPetRegister", component: UserPetRegister },
    { path: "/userPet/update/:petId", name: "userPetUpdate", component: UserPetUpdate },
    { path: "/userPet/detail/:petId", name: "userPetDetail", component: UserPetDetail },
    { path: "/userPet/diary/:petId", name: "diaryList", component: DiaryList },
    { path: "/userPet/diary/register", name: "diaryRegister", component: DiaryRegister },
]