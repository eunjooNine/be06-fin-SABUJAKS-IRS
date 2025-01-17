package com.sabujaks.irs.domain.interview_evaluate.model.entity;

import com.sabujaks.irs.domain.announcement.model.entity.Announcement;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewEvaluateForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private String q5;
    private String q6;
    private String q7;
    private String q8;
    private String q9;
    private String q10;

    @ManyToOne
    @JoinColumn(name = "announcement_uuid", referencedColumnName = "uuid")
    private Announcement announcement;

    @OneToMany(mappedBy = "interviewEvaluateForm")
    private List<InterviewEvaluate> interviewEvaluateList;
 }
