package com.sabujaks.irs.domain.resume.repository;

import com.sabujaks.irs.domain.resume.model.entity.CustomLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomLetterRepository extends JpaRepository<CustomLetter, Long> {
    @Query("SELECT cl FROM CustomLetter cl WHERE cl.resumeInfo.idx = :resumeInfoIdx")
    List<CustomLetter> findAllByResumeInfoIdx(Long resumeInfoIdx);
}
