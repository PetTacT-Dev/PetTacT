<template>
    <div class="container mt-5" style="max-width: 700px;">
        <h2 class="text-center mb-4 fw-bold">추가 정보 입력</h2>
        <hr class="flex-grow-1">
        <form @submit.prevent="handleSocialJoin">

            <!-- 이메일 (readonly) -->
            <div class="mb-3 row align-items-center">
                <label for="userEmail" class="col-sm-3 col-form-label">이메일</label>
                <div class="col-sm-9">
                    <input type="email" v-model="userEmail" id="userEmail" class="form-control" readonly>
                </div>
            </div>

            <!-- 이름 -->
            <div class="mb-3 row align-items-center">
                <label for="userName" class="col-sm-3 col-form-label">이름 <span class="text-danger">*</span></label>
                <div class="col-sm-9">
                    <input type="text" v-model="userName" id="userName" class="form-control" required>
                </div>
            </div>

            <!-- 닉네임 -->
            <div class="mb-3 row align-items-center">
                <label for="userNickname" class="col-sm-3 col-form-label">닉네임 <span class="text-danger">*</span></label>
                <div class="col-sm-9">
                    <div class="input-group">
                        <input type="text" v-model="userNickname" id="userNickname" class="form-control" required>
                        <button type="button" class="btn btn-outline-secondary" @click="checkNicknameDuplication">중복
                            확인</button>
                    </div>
                    <span v-if="isNicknameChecked" class="text-success">중복확인 완료</span>
                </div>
            </div>

            <!-- 휴대폰 번호 -->
            <div class="mb-3 row align-items-center">
                <label for="userTel" class="col-sm-3 col-form-label">휴대폰 번호 <span class="text-danger">*</span></label>
                <div class="col-sm-9">
                    <input type="tel" v-model="userTel" id="userTel" class="form-control" placeholder="숫자만 입력해주세요"
                        required>
                </div>
            </div>

            <!-- 생년월일 -->
            <div class="mb-3 row align-items-center">
                <label for="userBirth" class="col-sm-3 col-form-label">생년월일 <span class="text-danger">*</span></label>
                <div class="col-sm-9">
                    <input type="date" v-model="userBirth" id="userBirth" class="form-control" required>
                </div>
            </div>

            <hr class="flex-grow-1">


            <!-- 우편번호 -->
            <div class="mb-3 row align-items-center">
                <label for="userZipcode" class="col-sm-3 col-form-label">우편번호</label>
                <div class="col-sm-9">
                    <div class="input-group">
                        <input type="text" v-model="userZipcode" id="userZipcode" class="form-control" readonly
                            placeholder="우편번호를 입력해주세요">
                        <button type="button" class="btn btn-outline-secondary" @click="openPostcode">찾기</button>
                    </div>
                </div>
            </div>

            <!-- 주소 -->
            <div class="mb-3 row align-items-center">
                <label for="userStreet1" class="col-sm-3 col-form-label">주소</label>
                <div class="col-sm-9">
                    <input type="text" v-model="userStreet1" id="userStreet1" class="form-control" readonly
                        placeholder="주소를 입력해주세요">
                </div>
            </div>

            <!-- 상세주소 -->
            <div class="mb-3 row align-items-center">
                <label for="userDetailAddress" class="col-sm-3 col-form-label">상세주소</label>
                <div class="col-sm-9">
                    <input type="text" v-model="userDetailAddress" id="userDetailAddress" class="form-control"
                        placeholder="상세주소를 입력해주세요">
                </div>
            </div>

            <!-- 반려동물 여부 -->
            <div class="mb-3 row align-items-center">
                <label class="col-sm-3 col-form-label">반려동물</label>
                <div class="col-sm-9">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" v-model="userHasPet" id="userHasPet">
                        <label class="form-check-label" for="userHasPet">
                            반려동물이 있습니다.
                        </label>
                    </div>
                </div>
            </div>

            <!-- 이메일 수신 여부 -->
            <!-- <div class="mb-3 row align-items-center">
                <label class="col-sm-3 col-form-label">이메일 수신</label>
                <div class="col-sm-9">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" v-model="userEmailReceiveAgree"
                            id="userEmailChecked">
                        <label class="form-check-label" for="userEmailChecked">
                            이메일 수신에 동의합니다.
                        </label>
                    </div>
                </div>
            </div> -->

            <hr class="flex-grow-1">

            <!-- 약관동의 -->
            <div class="mb-4">
                <div class="card">
                    <div class="card-body">
                        <label class="form-label fw-bold mb-2">약관동의 <span class="text-danger">*</span></label>

                        <!-- 전체 동의 -->
                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" v-model="allAgree" id="allAgree"
                                @change="toggleAll">
                            <label class="form-check-label fw-bold" for="allAgree">전체 동의합니다.</label>
                        </div>

                        <!-- 개별 약관 -->
                        <div class="ms-3">
                            <!-- 이용약관 -->
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" v-model="agreeTerms" id="agreeTerms">
                                <label class="form-check-label" for="agreeTerms">
                                    이용약관 동의 (필수)
                                </label>
                            </div>
                            <div class="bg-light p-2 mb-3 small rounded">
                                본 서비스는 연습용으로 제공되며, 회원 정보는 학습 목적으로만 사용됩니다.
                            </div>

                            <!-- 개인정보 처리방침 -->
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" v-model="agreePrivacy"
                                    id="agreePrivacy">
                                <label class="form-check-label" for="agreePrivacy">
                                    개인정보 처리방침 동의 (필수)
                                </label>
                            </div>
                            <div class="bg-light p-2 mb-3 small rounded">
                                수집된 정보는 연습용 DB에 저장되며, 제3자에게 제공되지 않습니다.
                            </div>

                            <!-- 마케팅 수신 동의 -->
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" v-model="userEmailReceiveAgree"
                                    id="userEmailChecked">
                                <label class="form-check-label" for="userEmailChecked">
                                    마케팅 정보 수신 동의 (선택)
                                </label>
                            </div>
                            <div class="bg-light p-2 small rounded">
                                이벤트, 공지사항, 프로모션 정보를 이메일로 수신합니다.
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 완료 버튼 -->
            <div class="mb-4 mt-3">
                <button type="submit" class="btn btn-primary w-100 py-2">추가 정보 등록</button>
            </div>

            <div v-if="errorMessage" class="mt-3 text-danger">{{ errorMessage }}</div>

        </form>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { watch } from 'vue';

const router = useRouter();
const userStore = useUserStore();

const userEmail = ref('');
const userName = ref('');
const userNickname = ref('');
const userTel = ref('');
const userBirth = ref('');
const userZipcode = ref('')
const userStreet1 = ref('')
const userDetailAddress = ref('')
const userHasPet = ref(false)

const allAgree = ref(false)
const agreeTerms = ref(false)
const agreePrivacy = ref(false)
const userEmailReceiveAgree = ref(false)    // 이메일 수신여부

const isNicknameChecked = ref(false);
const errorMessage = ref('');

onMounted(async () => {
    await userStore.fetchUser();

    if (!userStore.accessToken) {
        console.error('토큰 없음, 재로그인 필요');
        router.push('/login');
        return;
    }

    userEmail.value = userStore.user?.userEmail || '';
});

const checkNicknameDuplication = async () => {
    try {
        const res = await axios.get(`/v1/user/nickname/check?nickname=${userNickname.value}`);
        if (res.data) {
            alert('이미 사용 중인 닉네임입니다.');
            isNicknameChecked.value = false;
        } else {
            alert('사용 가능한 닉네임입니다.');
            isNicknameChecked.value = true;
        }
    } catch (error) {
        console.error(error);
        alert('닉네임 중복 확인 실패');
        isNicknameChecked.value = false;
    }
};

// 우편번호 api
const openPostcode = () => {
    new window.daum.Postcode({
        oncomplete: function (data) {
            userZipcode.value = data.zonecode;
            userStreet1.value = data.address;
            document.getElementById("userDetailAddress").focus();
        }
    }).open();
};

const handleSocialJoin = async () => {
    errorMessage.value = '';

    if (!isNicknameChecked.value) {
        errorMessage.value = '닉네임 중복확인을 해주세요.';
        return;
    }

    if (!agreeTerms.value || !agreePrivacy.value) {
        errorMessage.value = '필수 약관에 동의해주세요.';
        return;
    }

    try {
        await axios.post('/v1/user/social/join', {
            userName: userName.value,
            userNickname: userNickname.value,
            userTel: userTel.value,
            userBirth: userBirth.value,
            userZipcode: userZipcode.value,
            userStreet1: userStreet1.value,
            userDetailAddress: userDetailAddress.value,
            userHasPet: userHasPet.value,
            userEmailChecked: userEmailReceiveAgree.value
        });
        alert('추가 정보 등록이 완료되었습니다.');
        router.push('/');
    } catch (error) {
        console.error('추가 정보 등록 실패:', error);
        errorMessage.value = error.response?.data || '추가 정보 등록 실패';
    }
};

const toggleAll = () => {
    agreeTerms.value = allAgree.value;
    agreePrivacy.value = allAgree.value;
    userEmailReceiveAgree.value = allAgree.value;
};

// 개별 동의 변경 시 전체 동의 체크 여부 갱신
watch([agreeTerms, agreePrivacy, userEmailReceiveAgree], ([terms, privacy, marketing]) => {
    if (terms && privacy && marketing) {
        allAgree.value = true;
    } else {
        allAgree.value = false;
    }
});
</script>
