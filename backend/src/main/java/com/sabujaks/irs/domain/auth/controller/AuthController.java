package com.sabujaks.irs.domain.auth.controller;

import com.sabujaks.irs.domain.auth.model.request.CompanyVerifyReq;
import com.sabujaks.irs.domain.auth.model.request.RecruiterSignupReq;
import com.sabujaks.irs.domain.auth.model.response.AuthSignupRes;
import com.sabujaks.irs.domain.auth.model.response.CompanyVerifyRes;
import com.sabujaks.irs.domain.auth.model.response.CrnApiRes;
import com.sabujaks.irs.domain.auth.service.AuthService;
import com.sabujaks.irs.domain.auth.service.CompanyVerifyService;
import com.sabujaks.irs.domain.auth.service.EmailVerifyService;
import com.sabujaks.irs.global.common.exception.BaseException;
import com.sabujaks.irs.global.common.responses.BaseResponse;
import com.sabujaks.irs.global.common.responses.BaseResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailVerifyService emailVerifyService;
    private final CompanyVerifyService crnVerifyService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AuthSignupRes>> signup (
        @RequestBody RecruiterSignupReq dto) throws BaseException {
        AuthSignupRes response = authService.signup(dto);
        String uuid = emailVerifyService.sendMail(response);
        emailVerifyService.save(dto.getEmail(), uuid);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_REGISTER_SUCCESS, response));
    }

    @GetMapping("/email-verify")
    public ResponseEntity<BaseResponse> emailVerify(
        String email, String role, String uuid) throws Exception, BaseException {
        if(emailVerifyService.isExist(email, uuid)){
            authService.activeMember(email, role);
            return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.EMAIL_VERIFY_SUCCESS));
        } else {
            return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.EMAIL_VERIFY_FAIL));
        }
    }

    @PostMapping("/company-verify")
    public ResponseEntity<BaseResponse<CrnApiRes>> companyVerify(
        @RequestBody CompanyVerifyReq dto) throws BaseException {
        CompanyVerifyRes response = crnVerifyService.companyVerify(dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.COMPANY_VERIFY_SUCCESS, response));
    }
}