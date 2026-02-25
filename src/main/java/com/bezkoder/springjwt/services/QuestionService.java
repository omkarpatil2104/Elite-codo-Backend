package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.payload.request.DuplicateQuestionRequest;
import com.bezkoder.springjwt.payload.request.QuestionFilterDTO;
import com.bezkoder.springjwt.payload.request.QuestionRequest;
import com.bezkoder.springjwt.payload.request.ShuffleQuestionReq;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.QuestionResponse;
import com.bezkoder.springjwt.payload.response.QuestionResponse1;
import com.bezkoder.springjwt.payload.response.ShuffleQuestionsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuestionService {
    MainResponse create(QuestionRequest questionRequest);

    MainResponse update(QuestionRequest questionRequest);

    QuestionResponse questionById1(Integer questionId);

//    List<QuestionMaster> getAll();

    List<QuestionResponse> getAllActiveQuestions();
//
    List<QuestionResponse> chapterWiseQuestions(Integer chapterId);
//
//    List<QuestionResponse> yearOfAppearanceWiseQuestions(String yearOfAppearance);
//
    Integer allQuestionsCount();

    List<QuestionResponse1> getAllByStatus(String status);

    List<QuestionResponse1> getByUserIdAndStatus(Long id, String status);

    MainResponse acceptOrRejectQuestion(Integer questionId,String status);

    MainResponse delete(Integer questionId);


    // For delete all questions
    MainResponse deleteAllQuestions();


    Integer chapterWiseQuestionCount(Integer chapterId);

    Integer subjectWiseQuestionCount(Integer subjectId);

    Integer entranceExamWiseQuestionCount(Integer entranceExamId);

    List<QuestionResponse> entranceExamWiseQuestions(Integer entranceExamId);

    List<QuestionResponse> subjectWiseQuestions(Integer subjectId);

    List<QuestionResponse> topicWiseQuestions(Integer topicId);

    List<QuestionResponse> subTopicWiseQuestions(Integer subTopicId);

    List<QuestionResponse> questionTypeWiseQuestions(Integer questionTypeId);

    List<QuestionResponse> questionLevelWiseQuestions(Integer questionLevelId);

    List<QuestionResponse> patternWiseQuestion(Integer patternId);

    List<QuestionResponse> yearOfAppearanceWiseQuestions(Integer yearOfAppearanceId);

    List<QuestionResponse> questionCategoryWiseQuestions(Integer questionCategory);

    MainResponse findDuplicateQuestions(String question);

    List  asked();

    List<QuestionMaster> entranceAndStandardWiseQuestions(Integer entranceExamId, Integer standardId);

    List<ShuffleQuestionsResponse> shuffleQuestions(ShuffleQuestionReq shuffleQuestionReq);

    public Page<QuestionResponse1> getQuestionsByFilter(QuestionFilterDTO filterDTO, int page, int size);

//    List<QuestionMaster> getUnusedQuestionsForTeacher(Long teacherId);


//
//    List<QuestionResponse> standardWiseAllQuestions(Integer standardId);
//
////    List<QuestionResponse> yearWiseQuestionBank(String year, Integer entranceExamId);
}
