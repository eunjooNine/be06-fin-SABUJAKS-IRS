<template>
  <body class="body-m">
  <SeekerHeaderComponent></SeekerHeaderComponent>
  <div class="main_div">
    <div class="container-m pt-150">
      <!-- 사이드 바 -->
      <SeekerSideBarComponent></SeekerSideBarComponent>

      <!-- 메인 컨텐츠 -->
      <div class="main-content">
        <div class="header">
          <h1>회원 정보</h1>
          <!-- <button class="btn edit-profile">내 프로필 수정하기</button> -->
        </div>

        <div class="content">
          <!-- 기본 정보 -->
          <div class="info-section">
            <h2>기본 정보</h2>
            <div class="info-row">
              <span class="label">이메일</span>
              <span class="value">{{ readSeeker.email }}</span>
            </div>
            <div class="info-row">
              <span class="label">이름</span>
              <span class="value">{{ readSeeker.name }}</span>
            </div>
            <div class="info-row">
              <span class="label">닉네임</span>
              <span class="value">{{ readSeeker.nickname }}</span>
            </div>
            <div class="info-row">
              <span class="label">성별</span>
              <span class="value">{{ readSeeker.gender ? '남자' : '여자'}}</span>
            </div>
            <div class="info-row">
              <span class="label">생년월일</span>
              <span class="value">{{ readSeeker.birth }}</span>
            </div>
            <div class="info-row">
              <span class="label">핸드폰</span>
              <span class="value">{{ readSeeker.phoneNumber }}</span>
            </div>
            <div class="info-row">
              <span class="label">주소</span>
              <span class="value">{{ readSeeker.address }}</span>
            </div>
            <div class="info-row">
              <span class="label">소셜 로그인 타입</span>
              <span class="value">{{ readSeeker.socialType }}</span>
            </div>
            <div class="info-row">
              <span class="label">프로필 사진</span>
              <span class="value">
                  <img :src="readSeeker.profileImage" alt="프로필 사진" class="profile-img">
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  </body>
</template>

<script setup>
import SeekerHeaderComponent from "@/components/seeker/SeekerHeaderComponent.vue";
import SeekerSideBarComponent from "@/components/seeker/SeekerSideBarComponent.vue";
import { ref, onMounted } from 'vue';
import { UseAuthStore } from '@/stores/UseAuthStore';
import { useRouter } from 'vue-router';
import {useToast} from "vue-toastification";

const router = useRouter();
const toast = useToast();
const authStore = UseAuthStore();

const readSeeker = ref({});

onMounted(async () => {
  const response = await authStore.readSeeker();
  if (response.success) {
    readSeeker.value = response.result;
  } else {
    toast.error(response.message);
    router.push('/seeker/login');
  }
});

</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.body-m {
  font-family: 'Arial', sans-serif;
  background-color: white;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.main_div {
  width: 100%;
  background-color: rgba(255, 255, 255, 0);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  /* 수직 간격을 추가 */
}

.container-m {
  display: flex;
  width: 100%;
  max-width: 1200px;
  background-color: rgba(255, 255, 255, 0);
  /* box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); */
  border-radius: 10px;
  margin: 20px;
  /* 수직, 수평 여백 추가 */
  gap: 20px;
  /* 사이드바와 메인 컨텐츠 사이의 간격 추가 */
}

.main-content {
  width: 80%;
  padding: 30px;
  border-radius: 10px;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  font-weight: bold;
}

.edit-profile {
  background-color: #212b36;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.edit-profile:hover {
  background-color: #212b36;
}

.content {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
}

.info-section {
  margin-bottom: 20px;
}

.info-section h2 {
  font-size: 18px;
  margin-bottom: 20px;
}

.info-section img {
  width: 140px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding-top: 40px;
  padding-bottom: 10px;
  margin: 5px;
  border-bottom: 1px solid #e2e2e2;
}

.label {
  font-weight: bold;
  color: #333;
}

.value {
  color: #555;
}


/*  */

/* 드롭다운 메뉴 스타일 */
.dropdown-menu {
  display: none;
  /* 처음에는 숨김 */
  position: absolute;
  background-color: white;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  padding: 10px;
  border-radius: 5px;
  z-index: 100;
  width: 120px;
  top: 100%;
  /* 클릭한 요소 아래에 위치 */
  right: 0;
  /* 오른쪽에 맞춰 정렬 */
}

.logout-btn {
  display: block;
  /* padding: 4px 16px; */
  margin: 0 0 0 0;
  text-decoration: none;
  color: #333;
  text-align: center;
  border-radius: 4px;
  /* background-color: #83a5ea; */
  color: white;
}

.logout-btn:hover {
  /* background-color: #83a5ea; */
}
</style>