package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.TestQuestionDraft;
import com.bezkoder.springjwt.payload.response.TestQuestionsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TestQuestionDraftRepository extends JpaRepository<TestQuestionDraft, Integer> {

    @Query("SELECT q.questionId FROM TestQuestionDraft q WHERE q.userId = :userId")
    List<Integer> findUsedQuestionsByUser(@Param("userId") Long userId);


    @Query("SELECT COUNT(t) > 0 FROM TestQuestionDraft AS t WHERE t.userId=:id AND (:entranceExamId IS NULL OR t.entranceExamId=:entranceExamId) AND (:standardId IS NULL OR t.standardId=:standardId) AND (:subjectId IS NULL OR t.subjectId=:subjectId) AND (:chapterId IS NULL OR t.chapterId=:chapterId) AND (:topicId IS NULL OR t.topicId=:topicId) AND (:subTopicId IS NULL OR t.subTopicId=:subTopicId) AND (:yearOfAppearanceId IS NULL OR t.yearOfAppearanceId=:yearOfAppearanceId) AND (:questionTypeId IS NULL OR t.questionTypeId=:questionTypeId) AND (:questionLevelId IS NULL OR t.questionLevelId=:questionLevelId) AND (:patternId IS NULL OR t.patternId=:patternId)")
    Boolean checkFilteredData(@Param("id") Long id, @Param("entranceExamId") Integer entranceExamId, @Param("standardId") Integer standardId, @Param("subjectId") Integer subjectId, @Param("chapterId") Integer chapterId, @Param("topicId") Integer topicId, @Param("subTopicId") Integer subTopicId, @Param("yearOfAppearanceId") Integer yearOfAppearanceId, @Param("questionTypeId") Integer questionTypeId, @Param("questionLevelId") Integer questionLevelId, @Param("patternId") Integer patternId);


    @Query("select t.questionId from TestQuestionDraft as t where t.userId=:id AND (:entranceExamId IS NULL OR t.entranceExamId=:entranceExamId) AND (:standardId IS NULL OR t.standardId=:standardId) AND (:subjectId IS NULL OR t.subjectId=:subjectId) AND (:chapterId IS NULL OR t.chapterId=:chapterId) AND (:topicId IS NULL OR t.topicId=:topicId) AND (:subTopicId IS NULL OR t.subTopicId=:subTopicId) AND (:yearOfAppearanceId IS NULL OR t.yearOfAppearanceId=:yearOfAppearanceId) AND (:questionTypeId IS NULL OR t.questionTypeId=:questionTypeId) AND (:questionLevelId IS NULL OR t.questionLevelId=:questionLevelId) AND (:patternId IS NULL OR t.patternId=:patternId) AND (:questionCategory IS NULL OR t.questionCategory=:questionCategory)")
    List<Integer> checkedQuestionIdsList(@Param("id") Long id, @Param("entranceExamId") Integer entranceExamId, @Param("standardId") Integer standardId, @Param("subjectId") Integer subjectId, @Param("chapterId") Integer chapterId, @Param("topicId") Integer topicId, @Param("subTopicId") Integer subTopicId, @Param("yearOfAppearanceId") Integer yearOfAppearanceId, @Param("questionTypeId") Integer questionTypeId, @Param("questionLevelId") Integer questionLevelId, @Param("patternId") Integer patternId, @Param("questionCategory") String questionCategory);

    @Transactional
    @Modifying
    @Query("DELETE FROM TestQuestionDraft t WHERE t.questionId IN :questionIds and t.userId=:id")
    void deleteByQuestionIds(@Param("questionIds") List<Integer> questionIds,@Param("id") Long id);
}
