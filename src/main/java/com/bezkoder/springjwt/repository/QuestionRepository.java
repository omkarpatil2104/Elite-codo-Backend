package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.payload.response.QuestionResponse;
import com.bezkoder.springjwt.payload.response.TestQuestionsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionMaster, Integer>, JpaSpecificationExecutor<QuestionMaster> {

//    Page<QuestionMaster> findAll(Specification<QuestionMaster> spec, Pageable pageable);



    @Query("select q from QuestionMaster as q where q.chapterMaster.chapterId=:chapterId ")
   List<QuestionMaster> questionByChapterId(@Param("chapterId") Integer chapterId);
    @Query("select q from QuestionMaster as q where q.status='Active' order by q.questionId desc")
    List<QuestionMaster> getAllActiveQuestions();
//
//    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance,q.answer,q.explanation,q.date,q.status) from QuestionMaster as q where q.chapterMaster.chapterId=:chapterId ")
//    List<QuestionResponse> chapterWiseQuestions(@Param("chapterId") Integer chapterId);
//
//    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance,q.answer,q.explanation,q.date,q.status) from QuestionMaster as q where q.yearOfAppearance=:yearOfAppearance ")
//    List<QuestionResponse> yearOfAppearanceWiseQuestions(@Param("yearOfAppearance") String yearOfAppearance);


 @Query("SELECT q FROM TestMaster t JOIN t.questionMasters q WHERE t.testId = :testId")
 List<QuestionMaster> findAllByTestId(@Param("testId") Integer testId);

//    @Query("SELECT q FROM QuestionMaster q JOIN q.tests t WHERE t.testId = :testId")
//        // or if it's a OneToMany: "SELECT q FROM QuestionMaster q WHERE q.test.testId = :testId"
//    List<QuestionMaster> findAllByTestId(@Param("testId") Integer testId);

    @Query("select count(q) from QuestionMaster as q")
    Integer allQuestionsCount();


    @Query("select q from QuestionMaster as q where q.status=:status order by q.questionId desc")
    List<QuestionMaster> getAllByStatus(@Param("status") String status);

    @Query("select q from QuestionMaster as q where q.user.id=:id and q.status=:status order by q.questionId desc")
    List<QuestionMaster> getByUserIdAndStatus(@Param("id") Long id,@Param("status") String status);

    @Query("select count(q) from QuestionMaster as q where q.user.id=:id")
    Integer teacherWiseQuestionCount(@Param("id") Long id);

    @Query("select count(q) from QuestionMaster as q where q.user.id=:id and q.status='Pending'")
    Integer teacherWisePendingQuestionCount(@Param("id") Long id);

    @Query("select count(q) from QuestionMaster as q where q.user.id=:id and q.status='Accepted'")
    Integer teacherWiseAcceptedQuestionCount(@Param("id") Long id);

    @Query("select count(q) from QuestionMaster as q where q.user.id=:id and q.status='Rejected'")
    Integer teacherWiseRejectedQuestionCount(@Param("id") Long id);

    @Query("select count(q) from QuestionMaster as q where q.chapterMaster.chapterId=:chapterId")
    Integer chapterWiseQuestionCount(@Param("chapterId") Integer chapterId);

    @Query("select count(q) from QuestionMaster as q where q.subjectMaster.subjectId=:subjectId")
    Integer subjectWiseQuestionCount(@Param("subjectId") Integer subjectId);
    @Query("select count(q) from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId")
    Integer entranceExamWiseQuestionCount(@Param("entranceExamId") Integer entranceExamId);

    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance.yearOfAppearance,q.date,q.status,q.multiAnswers,q.solution,q.questionCategory) from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId")
    List<QuestionResponse> entranceExamWiseQuestions(@Param("entranceExamId") Integer entranceExamId);

    @Query("select q from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId and q.status='Accepted'")
    List<QuestionMaster> entranceExamWiseQuestionMaster(@Param("entranceExamId") Integer entranceExamId);

    @Query("select q from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId")
    List<QuestionMaster> getByEntranceExamWiseQuestions(@Param("entranceExamId") Integer entranceExamId);

    @Query("select q from QuestionMaster as q where q.subjectMaster.subjectId=:subjectId")
    List<QuestionMaster> getBySubjectWiseQuestions(@Param("subjectId") Integer subjectId);

    @Query("select q from QuestionMaster as q where q.topicMaster.topicId=:topicId")
    List<QuestionMaster> topicWiseQuestions(@Param("topicId") Integer topicId);

    @Query("select q from QuestionMaster as q where q.subTopicMaster.subTopicId=:subTopicId")
    List<QuestionMaster> subTopicWiseQuestions(@Param("subTopicId") Integer subTopicId);

    @Query("select q from QuestionMaster as q where q.questionType.questionTypeId=:questionTypeId")
    List<QuestionMaster> questionTypeWiseQuestions(@Param("questionTypeId") Integer questionTypeId);

    @Query("select q from QuestionMaster as q where q.questionLevel.questionLevelId=:questionLevelId")
    List<QuestionMaster> questionLevelWiseQuestions(@Param("questionLevelId") Integer questionLevelId);

    @Query("select q from QuestionMaster as q where q.patternMaster.patternId=:patternId")
    List<QuestionMaster> patternWiseQuestion(@Param("patternId") Integer patternId);

    @Query("select q from QuestionMaster as q where q.yearOfAppearance.yearOfAppearanceId=:yearOfAppearanceId")
    List<QuestionMaster> yearOfAppearanceWiseQuestions(@Param("yearOfAppearanceId") Integer yearOfAppearanceId);

    @Query("select q from QuestionMaster as q where q.questionCategory=:questionCategory")
    List<QuestionMaster> questionCategoryWiseQuestions(@Param("questionCategory") Integer questionCategory);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.TestQuestionsResponse(q.questionId, " +
            "q.entranceExamMaster.entranceExamId, q.entranceExamMaster.entranceExamName, " +
            "q.standardMaster.standardId, q.standardMaster.standardName, " +
            "q.subjectMaster.subjectId, q.subjectMaster.subjectName, " +
            "q.chapterMaster.chapterId, q.chapterMaster.chapterName, " +
            "q.topicMaster.topicId, q.topicMaster.topicName, " +
            "q.subTopicMaster.subTopicId, q.subTopicMaster.subTopicName, " +
            "q.yearOfAppearance.yearOfAppearanceId, q.yearOfAppearance.yearOfAppearance, " +
            "q.questionType.questionTypeId, q.questionType.questionType, " +
            "q.questionLevel.questionLevelId, q.questionLevel.questionLevel, " +
            "q.patternMaster.patternId, q.patternMaster.patternName, q.patternMaster.patternActualName, " +
            "q.status, q.marks, q.question, q.option1, q.option2, q.option3, q.option4, " +
            "q.date, q.solution, q.explanation) " +
            "FROM QuestionMaster q " +
            "WHERE q.yearOfAppearance.yearOfAppearanceId < :currentYear AND q.status = 'Accepted'")
    List<TestQuestionsResponse> findQuestionsByYearOfAppearanceBefore(@Param("currentYear") Integer currentYear);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.TestQuestionsResponse(q.questionId, " +
            "q.entranceExamMaster.entranceExamId, q.entranceExamMaster.entranceExamName, " +
            "q.standardMaster.standardId, q.standardMaster.standardName, " +
            "q.subjectMaster.subjectId, q.subjectMaster.subjectName, " +
            "q.chapterMaster.chapterId, q.chapterMaster.chapterName, " +
            "q.topicMaster.topicId, q.topicMaster.topicName, " +
            "q.subTopicMaster.subTopicId, q.subTopicMaster.subTopicName, " +
            "q.yearOfAppearance.yearOfAppearanceId, q.yearOfAppearance.yearOfAppearance, " +
            "q.questionType.questionTypeId, q.questionType.questionType, " +
            "q.questionLevel.questionLevelId, q.questionLevel.questionLevel, " +
            "q.patternMaster.patternId, q.patternMaster.patternName, q.patternMaster.patternActualName, " +
            "q.status, q.marks, q.question, q.option1, q.option2, q.option3, q.option4, " +
            "q.date, q.solution, q.explanation) " +
            "FROM QuestionMaster q " +
            "WHERE q.yearOfAppearance.yearOfAppearanceId = :currentYear AND q.status = 'Accepted'")
    List<TestQuestionsResponse> findQuestionsByYearOfAppearance(@Param("currentYear") Integer currentYear);



    @Query("select new com.bezkoder.springjwt.payload.response.TestQuestionsResponse(" +
            "q.questionId as questionId, q.entranceExamMaster.entranceExamId as entranceExamId, q.entranceExamMaster.entranceExamName as entranceExamName, " +
            "q.standardMaster.standardId as standardId, q.standardMaster.standardName as standardName, " +
            "q.subjectMaster.subjectId as subjectId, q.subjectMaster.subjectName as  subjectName, " +
            "q.chapterMaster.chapterId as chapterId , q.chapterMaster.chapterName as chapterName, " +
            "q.topicMaster.topicId as topicId, q.topicMaster.topicName as topicName, " +
            "q.subTopicMaster.subTopicId as subTopicId, q.subTopicMaster.subTopicName as subTopicName, " +
            "q.yearOfAppearance.yearOfAppearanceId as yearOfAppearanceId, q.yearOfAppearance.yearOfAppearance as yearOfAppearance, " +
            "q.questionType.questionTypeId as questionTypeId , q.questionType.questionType as questionType, " +
            "q.questionLevel.questionLevelId as questionLevelId , q.questionLevel.questionLevel as questionLevel, " +
            "q.patternMaster.patternId as patternId, q.patternMaster.patternName as patternName, q.patternMaster.patternActualName as patternActualName, q.status as status" +
            " ,q.marks as marks ,q.question as question,q.option1 as option1,q.option2 as option2,q.option3 as option3,q.option4 as option4,q.date as date,q.solution as solution ,q.explanation as explanation)" +
            " from com.bezkoder.springjwt.models.QuestionMaster as q " +
            "where q.status='Accepted'  and (:entranceExamId IS NULL OR q.entranceExamMaster.entranceExamId = :entranceExamId) AND " +
            "(:standardId IS NULL OR q.standardMaster.standardId = :standardId) AND " +
            "(:subjectId IS NULL OR q.subjectMaster.subjectId = :subjectId) AND " +
            "(:chapterId IS NULL OR q.chapterMaster.chapterId = :chapterId) AND " +
            "(:topicId IS NULL OR q.topicMaster.topicId = :topicId) AND " +
            "(:subTopicId IS NULL OR q.subTopicMaster.subTopicId = :subTopicId) AND " +
//            "(:yearOfAppearanceId IS NULL OR q.yearOfAppearance.yearOfAppearanceId = :yearOfAppearanceId) AND " +
            "(:questionTypeId IS NULL OR q.questionType.questionTypeId = :questionTypeId) AND " +
            "(:questionLevelId IS NULL OR q.questionLevel.questionLevelId = :questionLevelId) AND " +
            "(:patternId IS NULL OR q.patternMaster.patternId = :patternId) AND"+
            "(:questionCategory IS NULL OR q.questionCategory = :questionCategory)")
    List<TestQuestionsResponse> filter(
            @Param("entranceExamId") Integer entranceExamId,
            @Param("standardId") Integer standardId,
            @Param("subjectId") Integer subjectId,
            @Param("chapterId") Integer chapterId,
            @Param("topicId") Integer topicId,
            @Param("subTopicId") Integer subTopicId,
//            @Param("yearOfAppearanceId") Integer yearOfAppearanceId,
            @Param("questionTypeId") Integer questionTypeId,
            @Param("questionLevelId") Integer questionLevelId,
            @Param("patternId") Integer patternId,
            @Param("questionCategory") String questionCategory);

    @Query("select q from QuestionMaster q where q.question like :question")
    Boolean findDuplicateQuestions(@Param("question") String question);

    QuestionMaster findByQuestion(String question);

    @Query("select q.questionId from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId and q.standardMaster.standardId=:standardId and q.subjectMaster.subjectId=:subjectId and q.chapterMaster.chapterId=:chapterId and q.topicMaster.topicId=:topicId")
    List<Integer> getCountOfQuestionsByEntranceStandardSubjectChapterTopic(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId,@Param("chapterId") Integer chapterId,@Param("topicId") Integer topicId);


//    @Query("select q from QuestionMaster as q where q.chapterMaster.chapterId=:chapterId")
//    List<QuestionMaster> getAllQuestionMasterByChapterId(@Param("chapterId") Integer chapterId);


//    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance,q.answer,q.explanation,q.date,q.status) from QuestionMaster as q where q.standardMaster.standardId=:standardId")
//    List<QuestionResponse> standardWiseAllQuestions(@Param("standardId") Integer standardId);
//
////    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance,q.answer,q.explanation,q.date,q.status) from QuestionMaster as q where q.yearOfAppearance=:year and q.entranceExamMaster.entranceExamId=:entranceExamId")
////    List<QuestionResponse> yearWiseQuestionBank(@Param("year") String year,@Param("entranceExamId") Integer entranceExamId);
//
//    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance,q.answer,q.explanation,q.date,q.status) from QuestionMaster as q")
//    List<QuestionResponse> getAll();
//


    @Query("select new com.bezkoder.springjwt.payload.response.TestQuestionsResponse(" +
            "q.questionId as questionId, q.entranceExamMaster.entranceExamId as entranceExamId, q.entranceExamMaster.entranceExamName as entranceExamName, " +
            "q.standardMaster.standardId as standardId, q.standardMaster.standardName as standardName, " +
            "q.subjectMaster.subjectId as subjectId, q.subjectMaster.subjectName as  subjectName, " +
            "q.chapterMaster.chapterId as chapterId , q.chapterMaster.chapterName as chapterName, " +
            "q.topicMaster.topicId as topicId, q.topicMaster.topicName as topicName, " +
            "q.subTopicMaster.subTopicId as subTopicId, q.subTopicMaster.subTopicName as subTopicName, " +
            "q.yearOfAppearance.yearOfAppearanceId as yearOfAppearanceId, q.yearOfAppearance.yearOfAppearance as yearOfAppearance, " +
            "q.questionType.questionTypeId as questionTypeId , q.questionType.questionType as questionType, " +
            "q.questionLevel.questionLevelId as questionLevelId , q.questionLevel.questionLevel as questionLevel, " +
            "q.patternMaster.patternId as patternId, q.patternMaster.patternName as patternName, q.patternMaster.patternActualName as patternActualName, q.status as status" +
            " ,q.marks as marks ,q.question as question,q.option1 as option1,q.option2 as option2,q.option3 as option3,q.option4 as option4,q.date as date,q.solution as solution ,q.explanation as explanation )" +
            " from com.bezkoder.springjwt.models.QuestionMaster as q " +
            "where q.status='Accepted'  and q.asked=:b and (:entranceExamId IS NULL OR q.entranceExamMaster.entranceExamId = :entranceExamId) AND " +
            "(:standardId IS NULL OR q.standardMaster.standardId = :standardId) AND " +
            "(:subjectId IS NULL OR q.subjectMaster.subjectId = :subjectId) AND " +
            "(:chapterId IS NULL OR q.chapterMaster.chapterId = :chapterId) AND " +
            "(:topicId IS NULL OR q.topicMaster.topicId = :topicId) AND " +
            "(:subTopicId IS NULL OR q.subTopicMaster.subTopicId = :subTopicId) AND " +
            "(:questionTypeId IS NULL OR q.questionType.questionTypeId = :questionTypeId) AND " +
            "(:questionLevelId IS NULL OR q.questionLevel.questionLevelId = :questionLevelId) AND " +
            "(:patternId IS NULL OR q.patternMaster.patternId = :patternId) AND"+
            "(:questionCategory IS NULL OR q.questionCategory = :questionCategory)")
    List<TestQuestionsResponse> filter1(
            @Param("entranceExamId") Integer entranceExamId,
            @Param("standardId") Integer standardId,
            @Param("subjectId") Integer subjectId,
            @Param("chapterId") Integer chapterId,
            @Param("topicId") Integer topicId,
            @Param("subTopicId") Integer subTopicId,
            @Param("questionTypeId") Integer questionTypeId,
            @Param("questionLevelId") Integer questionLevelId,
            @Param("patternId") Integer patternId,
            @Param("questionCategory") String questionCategory,@Param("b") Boolean b);


    @Query("select new com.bezkoder.springjwt.payload.response.TestQuestionsResponse(" +
            "q.questionId as questionId, q.entranceExamMaster.entranceExamId as entranceExamId, q.entranceExamMaster.entranceExamName as entranceExamName, " +
            "q.standardMaster.standardId as standardId, q.standardMaster.standardName as standardName, " +
            "q.subjectMaster.subjectId as subjectId, q.subjectMaster.subjectName as  subjectName, " +
            "q.chapterMaster.chapterId as chapterId , q.chapterMaster.chapterName as chapterName, " +
            "q.topicMaster.topicId as topicId, q.topicMaster.topicName as topicName, " +
            "q.subTopicMaster.subTopicId as subTopicId, q.subTopicMaster.subTopicName as subTopicName, " +
            "q.yearOfAppearance.yearOfAppearanceId as yearOfAppearanceId, q.yearOfAppearance.yearOfAppearance as yearOfAppearance, " +
            "q.questionType.questionTypeId as questionTypeId , q.questionType.questionType as questionType, " +
            "q.questionLevel.questionLevelId as questionLevelId , q.questionLevel.questionLevel as questionLevel, " +
            "q.patternMaster.patternId as patternId, q.patternMaster.patternName as patternName, q.patternMaster.patternActualName as patternActualName, q.status as status" +
            " ,q.marks as marks ,q.question as question,q.option1 as option1,q.option2 as option2,q.option3 as option3,q.option4 as option4,q.date as date,q.solution as solution ,q.explanation as explanation)" +
            " from com.bezkoder.springjwt.models.QuestionMaster as q " +
            "where q.status='Accepted'  and q.asked=:b and (:entranceExamId IS NULL OR q.entranceExamMaster.entranceExamId = :entranceExamId) AND " +
            "(:standardId IS NULL OR q.standardMaster.standardId = :standardId) AND " +
            "(:subjectId IS NULL OR q.subjectMaster.subjectId = :subjectId) AND " +
            "(:chapterId IS NULL OR q.chapterMaster.chapterId = :chapterId) AND " +
            "(:topicId IS NULL OR q.topicMaster.topicId = :topicId) AND " +
            "(:subTopicId IS NULL OR q.subTopicMaster.subTopicId = :subTopicId) AND " +
            "(:questionTypeId IS NULL OR q.questionType.questionTypeId = :questionTypeId) AND " +
            "(:questionLevelId IS NULL OR q.questionLevel.questionLevelId = :questionLevelId) AND " +
            "(:patternId IS NULL OR q.patternMaster.patternId = :patternId) AND " +
            "(:questionCategory IS NULL OR q.questionCategory = :questionCategory)")
    List<TestQuestionsResponse> filter2(
            @Param("entranceExamId") Integer entranceExamId,
            @Param("standardId") Integer standardId,
            @Param("subjectId") Integer subjectId,
            @Param("chapterId") Integer chapterId,
            @Param("topicId") Integer topicId,
            @Param("subTopicId") Integer subTopicId,
            @Param("questionTypeId") Integer questionTypeId,
            @Param("questionLevelId") Integer questionLevelId,
            @Param("patternId") Integer patternId,
            @Param("questionCategory") String questionCategory,
            @Param("b") Boolean b);




    @Query("select q from QuestionMaster as q where q.asked=:b AND q.status='Accepted' ")
    List<QuestionMaster> getAllByAsk(@Param("b") boolean b);

//    @Query("select q.questionId from QuestionMaster as q where q.status='Accepted'")
//    List<Integer> questionsByChapterId(Integer chapter);

    @Query("select q.questionId from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId and q.standardMaster.standardId=:standardId and q.subjectMaster.subjectId=:subjectId and q.chapterMaster.chapterId=:chapter and q.status='Accepted'")
    List<Integer> getChapterQuestionsByEntranceStandardSubjectChapter(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId,@Param("chapter") Integer chapter);

    @Query("SELECT q FROM QuestionMaster q WHERE q.entranceExamMaster.entranceExamId = :entranceExamId AND q.standardMaster.standardId = :standardId AND q.subjectMaster.subjectId = :subjectId AND q.chapterMaster.chapterId = :chapterId AND (:questionLevel IS NULL OR q.questionLevel.questionLevelId = :questionLevel) AND (:questionType IS NULL OR q.questionType.questionTypeId = :questionType) AND (:asked IS NULL OR q.asked = :asked) AND q.status = 'Accepted'")
    List<QuestionMaster> questionsByChapterId(@Param("entranceExamId") Integer entranceExamId, @Param("standardId") Integer standardId, @Param("chapterId") Integer chapterId, @Param("subjectId") Integer subjectId, @Param("questionLevel") Integer questionLevel, @Param("questionType") Integer questionType, @Param("asked") Boolean asked);


    @Query("select q from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId and q.standardMaster.standardId = :standardId and q.subjectMaster.subjectId = :subjectId and q.chapterMaster.chapterId = :chapterId and q.topicMaster.topicId=:topic and (:questionLevel IS NULL OR q.questionLevel.questionLevelId = :questionLevel) AND (:questionType IS NULL OR q.questionType.questionTypeId = :questionType) AND (:asked IS NULL OR q.asked = :asked) AND q.status = 'Accepted'")
    List<QuestionMaster> getTopicWiseQuestions(@Param("entranceExamId") Integer entranceExamId, @Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId,@Param("chapterId") Integer chapterId,@Param("topic") Integer topic, @Param("questionLevel") Integer questionLevel, @Param("questionType") Integer questionType, @Param("asked") Boolean asked);

    @Query("select new com.bezkoder.springjwt.payload.response.QuestionResponse(q.questionId,q.standardMaster.standardName,q.chapterMaster.chapterName,q.marks,q.question,q.option1,q.option2,q.option3,q.option4,q.yearOfAppearance.yearOfAppearance,q.date,q.status,q.solution,q.questionCategory) from QuestionMaster as q where q.questionId=:questionId")
    QuestionResponse getQuestionByQuestionId(@Param("questionId") Integer questionId);

    @Query("select q from QuestionMaster as q where q.entranceExamMaster.entranceExamId=:entranceExamId and q.standardMaster.standardId=:standardId and q.status='Accepted'")
    List<QuestionMaster> entranceAndStandardWiseQuestions(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId);

    List<QuestionMaster> findAllByQuestionIdIn(List<Integer> questionIds);

//    @Query("SELECT q FROM QuestionMaster q WHERE q.questionId IN :questionIds")
//    List<QuestionMaster> findAllByQuestionIdIn(@Param("questionIds") List<Integer> questionIds);
//@Query("SELECT q FROM QuestionMaster q WHERE q.questionId IN :questionIds")
//Page<QuestionMaster> findAllByQuestionIdIn(@Param("questionIds") List<Integer> questionIds, Pageable pageable);



    List<QuestionMaster> findByQuestionIdIn(List<Integer> questionIds);

    long countByUserId(Long teacherId);

     @Query("SELECT COUNT(q) FROM QuestionMaster q WHERE q.entranceExamMaster.entranceExamId = :entranceExamId")
     int countByEntranceExamId(@Param("entranceExamId") Integer entranceExamId);

//   @Query(" SELECT COUNT(q)"+
//        "FROM QuestionMaster q"+
//        "WHERE q.entranceExamMaster.entranceExamId = :examId"+
//          "AND q.subjectMaster.subjectId = :subjectId"
//   )
//   int countByExamIdAndSubjectId(
//           @Param("examId") Integer examId,
//           @Param("subjectId") Integer subjectId
//   );
int countByEntranceExamMaster_EntranceExamIdAndSubjectMaster_SubjectId(
        Integer examId,
        Integer subjectId
);

   int countByChapterMaster_ChapterId(Integer chapterId);

   @Query(
        "select q "+
          "from QuestionMaster q "+
         "where q.entranceExamMaster.entranceExamId = :entranceExamId "+
           "and q.standardMaster.standardId       = :standardId "+
           "and q.topicMaster.topicId             = :topicId "+
           "and q.subjectMaster.subjectId         = :subjectId "+
           "and (:level   is null or q.questionLevel.questionLevelId = :level) "+
           "and (:typeVal is null or q.questionType.questionTypeId   = :typeVal) "+
           "and (:asked   is null or q.asked                      = :asked)"
        )
   List<QuestionMaster> questionsByTopicId(
           @Param("entranceExamId") Integer entranceExamId,
           @Param("standardId")     Integer standardId,
           @Param("topicId")        Integer topicId,
           @Param("subjectId")      Integer subjectId,
           @Param("level")          Integer level,
           @Param("typeVal")        Integer typeVal,
           @Param("asked")          Boolean asked
   );

   List<QuestionMaster> findByChapterMaster_ChapterIdAndStatus(Integer chapterId, String active);

//    @Query("select q.questionId from QuestionMaster as q where q.chapterMaster.chapterId=:")
//    List<Integer> questionsByChapterId(@Param("chapterId") Integer chapterId, Integer level, Integer type, Boolean asked);

    @Query("SELECT COUNT(q) FROM QuestionMaster q WHERE q.chapterMaster.chapterId = :chapterId")
    Long countQuestionsByChapter(@Param("chapterId") Integer chapterId);


    // For delete all questions
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM question_master", nativeQuery = true)
    void deleteAllQuestions();


}
