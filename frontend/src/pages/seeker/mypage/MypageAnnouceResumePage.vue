<template>
    <div class="body-a">
        <SeekerHeaderComponent></SeekerHeaderComponent>
        <div class="main_div">
            <div class="container-a pt-150">
                <!-- 사이드 바 -->
                <SeekerSideBarComponent></SeekerSideBarComponent>

                <!-- 메인 컨텐츠 -->
                <div class="main-content">
                    <div class="header">
                        <h1>공고별 지원서 관리</h1>
                    </div>

                    <div class="content">
                        <!-- 지원서 항목 리스트 -->
                        <div v-for="(announceResume, index) in paginatedResumes" :key="index" class="application-item">
                            <div class="status">{{ checkApplicationResult(
                                announceResume.resumeResult,
                                announceResume.interviewOneResult,
                                announceResume.interviewTwoResult,
                                announceResume.finalResult) }}</div>
                            <div class="application-details">
                                <div class="application-title">{{ announceResume.resumeTitle }}</div>
                                <div class="company-name">{{ announceResume.announcementTitle }} / {{
                                    announceResume.companyName }}</div>
                                <div class="date">{{ formatDate(announceResume.resumedAt) }}</div>
                            </div>
                            <div class="application-actions">
                                <router-link :to="`/seeker/announce/detail/${announceResume.announcementIdx}`"><button
                                        class="announce-btn">공고 보기</button></router-link>
                                <router-link :to="`/seeker/resume/detail/${announceResume.resumeIdx}`"><button
                                        class="resume-btn">지원서 보기</button></router-link>
                            </div>
                        </div>
                    </div>
                    <div class="pagination">
                        <button v-for="page in totalPages" :key="page" @click="fetchResumes(page)"
                            :class="{ active: currentPage === page }">
                            {{ page }}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import SeekerHeaderComponent from "@/components/seeker/SeekerHeaderComponent.vue";
import SeekerSideBarComponent from "@/components/seeker/SeekerSideBarComponent.vue";

import { onMounted, ref, computed } from 'vue';
import { UseResumeStore } from '@/stores/UseResumeStore';
import { useRouter } from 'vue-router';

const router = useRouter();
const resumeStore = UseResumeStore();

const currentPage = ref(1);
const pageSize = 10;

onMounted(async () => {
    await fetchResumes(currentPage.value);
});

const fetchResumes = async (page) => {
    currentPage.value = page;
    await resumeStore.readAll(router, page - 1, pageSize);
};

const paginatedResumes = computed(() => {
    return resumeStore.announceResumeList;
});

const totalPages = computed(() => {
    return resumeStore.announceResumeListPage.totalPages || 0;
});

const formatDate = (dateString) => {
    return dateString.split('T')[0];
}

// 결과를 확인하는 함수
const checkApplicationResult = (resumeResult, interviewOneResult, interviewTwoResult, finalResult) => {
    if (finalResult === false) {
        return '최종 불합격';
    } else if (finalResult === true) {
        return '최종 합격';
    } else if (interviewTwoResult === false) {
        return '2차 면접 불합격';
    } else if (interviewTwoResult === true) {
        return '2차 면접 합격';
    } else if (interviewOneResult === false) {
        return '1차 면접 불합격';
    } else if (interviewOneResult === true) {
        return '1차 면접 합격';
    } else if (resumeResult === false) {
        return '서류 불합격';
    } else if (resumeResult === true) {
        return '서류 합격';
    } else {
        return '대기';
    }
};

</script>

<style scoped>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.body-a {
    font-family: 'Arial', sans-serif;
    background-color: white;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.header-a {
    background-color: #ffffff;
    border-bottom: 1px solid #ddd;
    padding: 10px 0;
}

.header-container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-container h1 {
    font-size: 24px;
    color: #333;
}

.header-right {
    position: relative;
    /* 부모 요소를 기준으로 드롭다운 위치 설정 */
}

.header-right a {
    margin-left: 20px;
    text-decoration: none;
    color: #666;
    font-size: 14px;
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

.container-a {
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

.profile {
    text-align: center;
}

.profile-img {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    margin-bottom: 10px;
}

.btn {
    background-color: #212b36;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    margin-top: 10px;
}

.btn:hover {
    background-color: #212b36;
}

nav ul {
    list-style: none;
    margin-top: 20px;
}

nav ul li {
    margin-bottom: 15px;
}

nav ul li a {
    text-decoration: none;
    color: #333;
    font-size: 16px;
}

.main-content {
    background-color: white;
    width: 80%;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header h1 {
    font-size: 24px;
    margin-bottom: 20px;
}

.content {
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.application-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
    border-bottom: 1px solid #ddd;
}

.application-item:last-child {
    border-bottom: none;
}

.status {
    font-size: 14px;
    color: #999;
    background-color: #f2f2f2;
    padding: 5px 10px;
    border-radius: 5px;
    margin-right: 20px;
    width: 120px;
    /* 너비 고정 */
    text-align: center;
    /* 텍스트 중앙 정렬 */
    white-space: nowrap;
    /* 텍스트 줄바꿈 방지 */
}

.application-details {
    flex-grow: 1;
}

.application-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 5px;
}

.company-name {
    font-size: 14px;
    color: #555;
}

.date {
    font-size: 12px;
    color: #999;
}

.application-actions {
    display: flex;
    gap: 10px;
}

.announce-btn,
.resume-btn {
    padding: 8px 16px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 14px;
}

.announce-btn {
    background-color: #212b36;
    color: white;
}

.resume-btn {
    background-color: white;
    color: #555;
    border: 1px solid #ddd;
}

.announce-btn:hover {
    background-color: #212b36;
}

.resume-btn:hover {
    background-color: #f2f2f2;
}


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

.pagination {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.pagination button {
  background-color: #f1f1f1;
  border: none;
  border-radius: 5px;
  padding: 10px 15px;
  margin: 0 5px;
  cursor: pointer;
}

.pagination button.active {
    background-color: #212b36;
    color: white;
}
</style>