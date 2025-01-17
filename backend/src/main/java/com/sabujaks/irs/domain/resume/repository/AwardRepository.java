package com.sabujaks.irs.domain.resume.repository;

import com.sabujaks.irs.domain.resume.model.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {
    @Query("SELECT a FROM Award a WHERE a.resumeInfo.idx = :resumeInfoIdx")
    List<Award> findAllByResumeInfoIdx(Long resumeInfoIdx);
}
