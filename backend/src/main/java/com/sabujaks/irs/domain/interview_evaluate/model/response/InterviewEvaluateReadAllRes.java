package com.sabujaks.irs.domain.interview_evaluate.model.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InterviewEvaluateReadAllRes {
    private Map<Long, List<InterviewEvaluateReadRes>> interviewEvaluateReadAllResMap;
}
