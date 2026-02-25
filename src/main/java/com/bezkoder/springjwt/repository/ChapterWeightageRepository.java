package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ChapterWeightageMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterWeightageRepository extends JpaRepository<ChapterWeightageMaster,Integer> {
    @Query("select ch from ChapterWeightageMaster as ch where ch.chapterId=:chapterId")
    Optional<ChapterWeightageMaster> getByChapterId(@Param("chapterId") Integer chapterId);

    List<ChapterWeightageMaster> findBySubjectIdInAndStatus(
            List<Integer> subjectIds, String status);

//    @Query("SELECT c FROM ChapterWeightageMaster c " +
//            "WHERE (:subjectIds IS NULL OR c.subjectId IN :subjectIds) " +
//            "  AND (:chapterIds IS NULL OR c.chapterId IN :chapterIds) " +
//            "  AND c.status = 'Active'")
//    List<ChapterWeightageMaster> findActiveRows(
//            @Param("subjectIds")  List<Integer> subjectIds,
//            @Param("chapterIds")  List<Integer> chapterIds);
}
