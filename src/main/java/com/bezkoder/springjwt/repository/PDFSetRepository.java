package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.PDFSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PDFSetRepository extends JpaRepository<PDFSet, Long> {

    Optional<PDFSet> findByCreatorIdAndExamNameAndDateAndExamSetName(
            Long creatorId,
            String examName,
            String date,
            String examSetName
    );

}
