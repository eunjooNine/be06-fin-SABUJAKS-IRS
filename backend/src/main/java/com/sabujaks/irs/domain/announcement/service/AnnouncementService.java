package com.sabujaks.irs.domain.announcement.service;

import com.sabujaks.irs.domain.announcement.model.entity.Announcement;
import com.sabujaks.irs.domain.announcement.model.entity.CustomForm;
import com.sabujaks.irs.domain.announcement.model.entity.CustomLetterForm;
import com.sabujaks.irs.domain.announcement.model.request.AnnouncementCreateReq;
import com.sabujaks.irs.domain.announcement.model.request.CustomFormCreateReq;
import com.sabujaks.irs.domain.announcement.model.response.*;
import com.sabujaks.irs.domain.announcement.repository.AnnouncementRepository;
import com.sabujaks.irs.domain.announcement.repository.CustomFormRepository;
import com.sabujaks.irs.domain.announcement.repository.CustomLetterFormRepository;
import com.sabujaks.irs.domain.auth.model.entity.Recruiter;
import com.sabujaks.irs.domain.auth.repository.RecruiterRepository;
import com.sabujaks.irs.domain.company.model.entity.Company;
import com.sabujaks.irs.domain.company.repository.CompanyBenefitsRepository;
import com.sabujaks.irs.domain.company.repository.CompanyRepository;
import com.sabujaks.irs.domain.data_init.entity.BaseInfo;
import com.sabujaks.irs.domain.data_init.repository.BaseInfoRepository;
import com.sabujaks.irs.global.common.exception.BaseException;
import com.sabujaks.irs.global.common.responses.BaseResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sabujaks.irs.domain.company.model.entity.CompanyBenefits;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final RecruiterRepository recruiterRepository;
    private final AnnouncementRepository announcementRepository;
    private final CustomFormRepository customFormRepository;
    private final CustomLetterFormRepository letterFormRepository;
    private final BaseInfoRepository baseInfoRepository;
    private final CompanyBenefitsRepository companyBenefitsRepository;
    private final CompanyRepository companyRepository;

    /*******채용담당자 공고 등록 (step1)***********/
    public AnnouncementCreateRes createAnnouncement(
            Long recruiterIdx,
            String fileUrl,
            AnnouncementCreateReq dto) throws BaseException {

        // 채용담당자 확인
        Optional<Recruiter> resultRecruiter = recruiterRepository.findByRecruiterIdx(recruiterIdx);
        if(resultRecruiter.isPresent()) {
            // 셀렉트 폼이 트루면 = 파일url이 있으면 파일만 공고 엔티티에 저장
            if(dto.getSelectForm()) {
                Announcement announcement = Announcement.builder()
                        .recruiter(resultRecruiter.get())
                        .selectForm(dto.getSelectForm())
                        .title(dto.getTitle())
                        .imgUrl(fileUrl)
                        .build();
                announcementRepository.save(announcement);

                // 응답
                return AnnouncementCreateRes.builder()
                        .announcementIdx(announcement.getIdx())
                        .build();

            } else { // 셀렉트 폼이 폴스면 = 파일url이 없으면 들어온 dto만 저장
                Announcement announcement = Announcement.builder()
                        .recruiter(resultRecruiter.get())
                        .selectForm(dto.getSelectForm())
                        .title(dto.getTitle())
                        .jobCategory(dto.getJobCategoryList().toString())
                        .jobTitle(dto.getJobTitle())
                        .recruitedNum(dto.getRecruitedNum())
                        .careerBase(dto.getCareerBase())
                        .intro(dto.getIntro())
                        .positionQuali(dto.getPositionQuali())
                        .jobType(dto.getJobType())
                        .salary(dto.getSalary())
                        .conditions(dto.getConditions())
                        .region(dto.getRegion())
                        .benefits(dto.getBenefits())
                        .managerName(dto.getManagerName())
                        .managerContact(dto.getManagerContact())
                        .managerEmail(dto.getManagerEmail())
                        .process(dto.getProcess())
                        .announcementStart(dto.getAnnouncementStart())
                        .announcementEnd(dto.getAnnouncementEnd())
                        .interviewNum(dto.getInterviewNum())
                        .note(dto.getNote())
                        .build();
                announcementRepository.save(announcement);

                // 응답
                return AnnouncementCreateRes.builder()
                        .announcementIdx(announcement.getIdx())
                        .build();
            }
        } else {
            // 채용담당자 유저에서 찾을 수 없을 때
            throw new BaseException(BaseResponseMessage.ANNOUNCEMENT_REGISTER_STEP_ONE_FAIL_NOT_RECRUITER);
        }
    }


    /*******채용담당자 지원서 폼 조립 + 자기소개서 문항 등록 (step2)***********/
    public CustomFormCreateRes createCustomForm(CustomFormCreateReq dto) throws BaseException {
        // 클라이언트에서 넣을 폼을 선택 -> dto에 그 폼의 코드값이 들어옴

        // 예외 처리) 공고가 잘 저장되어 있는지 먼저 확인 필요
        Optional<Announcement> resultAnnouncement = announcementRepository.findByAnnounceIdx(dto.getAnnounceIdx());
        if (!resultAnnouncement.isPresent()) {
            throw new BaseException(BaseResponseMessage.ANNOUNCEMENT_REGISTER_STEP_TWO_FAIL_NOT_FOUND);
        }

        // 1. 리스트 길이만큼 커스텀 폼 엔티티 생성 후 저장
        List<String> formCodes = dto.getCode();
        for (String code : formCodes) {
            CustomForm customForm = CustomForm.builder()
                    .code(code)
                    .announcement(resultAnnouncement.get())
                    .build();
            customFormRepository.save(customForm);
        }

        // 2. baseInfo 조회 - groupName과 code 리스트로 조회
        String groupName = "custom_form";
        List<BaseInfo> resultBaseInfoList = baseInfoRepository.findAllByGroupNameAndCodeIn(groupName, formCodes);

        // 3. 저장 배열 생성 (인덱스와 설명 리스트)
        List<Long> emsiIdx = resultBaseInfoList.stream()
                .map(BaseInfo::getIdx)
                .collect(Collectors.toList());

        List<String> emsiDes = resultBaseInfoList.stream()
                .map(BaseInfo::getDescription)
                .collect(Collectors.toList());

        // 4. 추가한 자기소개서 문항 등록 (리스트 길이만큼 커스텀 레터 폼 엔티티 생성 후 저장)
        List<Long> emsiLetterIdx = new ArrayList<>();
        for (int i = 0; i < dto.getTitleList().size(); i++) {
            CustomLetterForm customLetterForm = CustomLetterForm.builder()
                    .title(dto.getTitleList().get(i))
                    .chatLimit(dto.getChatLimitList().get(i))
                    .announcement(resultAnnouncement.get())
                    .build();
            letterFormRepository.save(customLetterForm);
            emsiLetterIdx.add(customLetterForm.getIdx());
        }

        // 5. 응답 생성
        return CustomFormCreateRes.builder()
                .baseInfoIdxList(emsiIdx)
                .descriptionList(emsiDes)
                .customFormLetterIdList(emsiLetterIdx)
                .build();
    }


    /*******공고 등록 페이지 클릭시 채용담당자 정보 조회***********/
    public RecruiterInfoReadRes readRecruiterInfo(Long recruiterIdx) throws BaseException {
        // 채용담당자 확인
        Optional<Recruiter> resultRecruiter = recruiterRepository.findByRecruiterIdx(recruiterIdx);
        if(resultRecruiter.isPresent()) {
            String phoneNumber = resultRecruiter.get().getPhoneNumber();

            String phoneNumberDasi = phoneNumber.replace("-", "");

            String phone1 = phoneNumberDasi.substring(0, 3);
            String phone2 = phoneNumberDasi.substring(3, 7);
            String phone3 = phoneNumberDasi.substring(7);

            return RecruiterInfoReadRes.builder()
                    .managerName(resultRecruiter.get().getName())
                    .managerContact(phoneNumber)  // 전체 전화번호를 유지하는 경우
                    .phone1(phone1)               // 첫 번째 부분
                    .phone2(phone2)               // 두 번째 부분
                    .phone3(phone3)               // 세 번째 부분
                    .managerEmail(resultRecruiter.get().getEmail())
                    .build();
        } else {
            // 채용담당자 유저에서 찾을 수 없을 때
            throw new BaseException(BaseResponseMessage.ANNOUNCEMENT_REGISTER_STEP_ONE_FAIL_NOT_RECRUITER);
        }
    }


    /*******공고 등록 페이지 클릭시 채용담당자 기업 복리후생 조회***********/
    public CompanyInfoReadRes readCompanyInfo(Long recruiterIdx) throws BaseException {
        // 채용담당자 확인
        Optional<Recruiter> resultRecruiter = recruiterRepository.findByRecruiterIdx(recruiterIdx);
        if(resultRecruiter.isPresent()) {
            // 채용담당자가 등록한 기업 확인
            Optional<Company> resultCompany = companyRepository.findByRecruiterIdx(recruiterIdx);
            if (resultCompany.isPresent()) {
                Long companyIdx = resultCompany.get().getIdx();

                // 기업 복리후생 테이블에서 해당 기업의 복리후생 코드들을 가져옴
                // 스트림 - 리스트 내의 각 요소에 대해 반복작업 쉽게 가능
                List<String> companyBenefitsCodes = companyBenefitsRepository
                        .findAllByCompanyIdx(companyIdx)
                        .stream()
                        .map(CompanyBenefits::getCode)
                        .collect(Collectors.toList());

                // 기준 코드 테이블에서 가져온 복리후생 코드를 조회
                List<BaseInfo> benefitsBaseInfos = baseInfoRepository.findByCodeIn(companyBenefitsCodes);

                // 대분류와 소분류를 저장할 맵
                Map<String, List<String>> benefitsMap = new HashMap<>();

                // 기준 코드 테이블에서 응답 생성
                for (BaseInfo baseInfo : benefitsBaseInfos) {
                    String category = baseInfo.getDescription();
                    String parentCode = baseInfo.getParentCode();

                    if (parentCode == null) {
                        // 부모 코드가 없으면 대분류로 처리
                        benefitsMap.putIfAbsent(category, new ArrayList<>());
                    } else {
                        // 부모 코드가 있으면 대분류의 소분류에 추가
                        String parentCategory = baseInfoRepository.findByCode(parentCode).getDescription();
                        benefitsMap.computeIfAbsent(parentCategory, k -> new ArrayList<>()).add(category);
                    }
                }

                // 응답 생성
                // .entrySet() : Map의 각 항목을 Map.Entry 객체(키-값 쌍)로 반환
                List<BenefitCategory> benefitCategories = benefitsMap.entrySet().stream()
                        .map(entry -> new BenefitCategory(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());

                // 최종 응답 반환
                return CompanyInfoReadRes.builder()
                        .benefitsDataList(benefitCategories)
                        .build();
            } else {
                // 기업을 찾을 수 없을 때
                throw new BaseException(BaseResponseMessage.COMPANY_INFO_FAIL_NOT_REGISTER);
            }
        } else {
            // 채용담당자 유저에서 찾을 수 없을 때
            throw new BaseException(BaseResponseMessage.ANNOUNCEMENT_REGISTER_STEP_ONE_FAIL_NOT_RECRUITER);
        }
    }
}