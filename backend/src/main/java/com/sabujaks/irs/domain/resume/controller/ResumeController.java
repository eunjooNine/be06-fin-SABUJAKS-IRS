package com.sabujaks.irs.domain.resume.controller;

import com.sabujaks.irs.domain.resume.model.request.ResumeCreateReq;
import com.sabujaks.irs.domain.resume.model.request.ResumeSubmitReq;
import com.sabujaks.irs.domain.resume.model.response.ResumeCreateRes;
import com.sabujaks.irs.domain.resume.model.response.ResumeReadRes;
import com.sabujaks.irs.domain.resume.model.response.ResumeReadSubmitInfoRes;
import com.sabujaks.irs.domain.resume.service.ResumeService;
import com.sabujaks.irs.global.common.exception.BaseException;
import com.sabujaks.irs.global.common.responses.BaseResponse;
import com.sabujaks.irs.global.common.responses.BaseResponseMessage;
import com.sabujaks.irs.global.security.CustomUserDetails;
import com.sabujaks.irs.global.utils.CloudFileUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    private final CloudFileUpload cloudFileUpload;

    // (지원자) 마이페이지 -> 지원서 등록
    @PostMapping("/create")
    public ResponseEntity<BaseResponse<ResumeCreateReq>> create(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart ResumeCreateReq dto,
            @RequestPart MultipartFile file,
            @RequestPart(required = false) MultipartFile[] portfolioFiles) throws BaseException {

        if(file.isEmpty()) {
            throw new BaseException(BaseResponseMessage.RESUME_REGISTER_FAIL_NOT_FOUND_FILE);
        }
        // 포트폴리오 파일 로직
        if(dto.getCodes().contains("resume_009")) {
            if (portfolioFiles != null && portfolioFiles.length > 0) {
                List<String> portfolioUrls = cloudFileUpload.multipleUpload(portfolioFiles);
                int fileIndex = 0;
                for (int i = 0; i < dto.getPortfolios().size(); i++) {
                    if (dto.getPortfolios().get(i).getPortfolioType().equals("파일")) {
                        // TYPE이 파일이고 URL이 null인 경우
                        if (fileIndex < portfolioFiles.length) {
                            dto.getPortfolios().get(i).setPortfolioUrl(portfolioUrls.get(fileIndex));
                            fileIndex++;
                        }
                    }
                }
            }
        }

        String fileUrl = cloudFileUpload.upload(file);
        ResumeCreateRes response = resumeService.create(customUserDetails, dto, fileUrl);

        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESUME_REGISTER_SUCCESS, response));
    }

    // (지원자) 공고 -> 지원서 등록
    @PostMapping("/submit")
    public ResponseEntity<BaseResponse<ResumeCreateReq>> submit(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart ResumeSubmitReq dto,
            @RequestPart MultipartFile file,
            @RequestPart(required = false) MultipartFile[] portfolioFiles) throws BaseException {

        if(file.isEmpty()) {
            throw new BaseException(BaseResponseMessage.RESUME_REGISTER_FAIL_NOT_FOUND_FILE);
        }

        // 포트폴리오 파일 로직
        if(dto.getCodes().contains("resume_009")) {
            if (portfolioFiles != null && portfolioFiles.length > 0) {
                List<String> portfolioUrls = cloudFileUpload.multipleUpload(portfolioFiles);
                int fileIndex = 0;
                for (int i = 0; i < dto.getPortfolios().size(); i++) {
                    if (dto.getPortfolios().get(i).getPortfolioType().equals("파일")) {
                        // TYPE이 파일이고 URL이 null인 경우
                        if (fileIndex < portfolioFiles.length) {
                            dto.getPortfolios().get(i).setPortfolioUrl(portfolioUrls.get(fileIndex));
                            fileIndex++;
                        }
                    }
                }
            }
        }

        String fileUrl = cloudFileUpload.upload(file);
        ResumeCreateRes response = resumeService.submit(customUserDetails, dto, fileUrl);

        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESUME_REGISTER_SUCCESS, response));
    }

    // (지원자) 공고 -> 지원서 제출 페이지 진입 시
    @GetMapping("/read/submit-info")
    public ResponseEntity<BaseResponse<ResumeSubmitReq>> readSubmitInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Long announcementIdx) throws BaseException {

        ResumeReadSubmitInfoRes response = resumeService.readSubmitInfo(customUserDetails, announcementIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESUME_READ_SUCCESS, response));
    }

    // (지원자) 마이페이지 -> 통합 지원서 조회
    @GetMapping("/read/integrated")
    public ResponseEntity<BaseResponse<ResumeSubmitReq>> readIntegrated(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException {

        ResumeReadRes response = resumeService.readIntegrated(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESUME_READ_SUCCESS, response));
    }

    // (지원자, 채용담당자) 지원서 상세 조회
    @GetMapping("/read")
    public ResponseEntity<BaseResponse<ResumeSubmitReq>> read(Long resumeIdx) throws BaseException {

        ResumeReadRes response = resumeService.read(resumeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.RESUME_READ_SUCCESS, response));
    }
}
