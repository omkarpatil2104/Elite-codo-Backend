package com.bezkoder.springjwt.spec;

import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.payload.request.QuestionFilterDTO;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionMasterSpecification {


public static Specification<QuestionMaster> getQuestionsByFilter(QuestionFilterDTO filter) {
    return (Root<QuestionMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
        List<Predicate> predicates = new ArrayList<>();

        /*
         *  1. Apply GLOBAL SEARCH (`searchTerm`)
         */
        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            String likePattern = "%" + filter.getSearchTerm().toLowerCase() + "%";
            List<Predicate> orPredicates = new ArrayList<>();

            // ✅ Search in `QuestionMaster` fields
            orPredicates.add(cb.like(cb.lower(root.get("question")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("explanation")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("solution")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("option1")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("option2")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("option3")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("option4")), likePattern));
            orPredicates.add(cb.like(cb.lower(root.get("questionCategory")), likePattern));

            // ✅ Join & Search within Related Entities
            Join<?, ?> examJoin = root.join("entranceExamMaster", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(examJoin.get("entranceExamName")), likePattern));

            Join<?, ?> subjectJoin = root.join("subjectMaster", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(subjectJoin.get("subjectName")), likePattern));

            Join<?, ?> chapterJoin = root.join("chapterMaster", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(chapterJoin.get("chapterName")), likePattern));

            Join<?, ?> topicJoin = root.join("topicMaster", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(topicJoin.get("topicName")), likePattern));

            Join<?, ?> subTopicJoin = root.join("subTopicMaster", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(subTopicJoin.get("subTopicName")), likePattern));

            Join<?, ?> qTypeJoin = root.join("questionType", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(qTypeJoin.get("questionType")), likePattern));

            Join<?, ?> patternJoin = root.join("patternMaster", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(patternJoin.get("patternName")), likePattern));

            Join<?, ?> questionLevelJoin = root.join("questionLevel", JoinType.LEFT);
            orPredicates.add(cb.like(cb.lower(questionLevelJoin.get("questionLevel")), likePattern));

            orPredicates.add(cb.like(cb.lower(root.get("questionCategory")), likePattern));

            //  Combine OR conditions for `searchTerm`
            predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
        }

        /*
         *  2. Apply Filters (Exam, Subject, Chapter, etc.)
         */
        if (filter.getExam() != null && !filter.getExam().isEmpty()) {
            Join<?, ?> examJoin = root.join("entranceExamMaster", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(examJoin.get("entranceExamName")), "%" + filter.getExam().toLowerCase() + "%"));
        }

        if (filter.getSubject() != null && !filter.getSubject().isEmpty()) {
            Join<?, ?> subjectJoin = root.join("subjectMaster", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(subjectJoin.get("subjectName")), "%" + filter.getSubject().toLowerCase() + "%"));
        }

        if (filter.getChapter() != null && !filter.getChapter().isEmpty()) {
            Join<?, ?> chapterJoin = root.join("chapterMaster", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(chapterJoin.get("chapterName")), "%" + filter.getChapter().toLowerCase() + "%"));
        }

        if (filter.getTopic() != null && !filter.getTopic().isEmpty()) {
            Join<?, ?> topicJoin = root.join("topicMaster", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(topicJoin.get("topicName")), "%" + filter.getTopic().toLowerCase() + "%"));
        }

        if (filter.getSubTopic() != null && !filter.getSubTopic().isEmpty()) {
            Join<?, ?> subTopicJoin = root.join("subTopicMaster", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(subTopicJoin.get("subTopicName")), "%" + filter.getSubTopic().toLowerCase() + "%"));
        }

        if (filter.getQuestionType() != null && !filter.getQuestionType().isEmpty()) {
            Join<?, ?> qTypeJoin = root.join("questionType", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(qTypeJoin.get("questionType")), "%" + filter.getQuestionType().toLowerCase() + "%"));
        }

        if (filter.getQuestionCategory() != null && !filter.getQuestionCategory().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("questionCategory")), "%" + filter.getQuestionCategory().toLowerCase() + "%"));
        }

        if (filter.getPattern() != null && !filter.getPattern().isEmpty()) {
            Join<?, ?> patternJoin = root.join("patternMaster", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(patternJoin.get("patternName")), "%" + filter.getPattern().toLowerCase() + "%"));
        }

        if (filter.getIsPYQ() != null) {
            predicates.add(cb.equal(root.get("asked"), filter.getIsPYQ()));
        }
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("status")),
                    filter.getStatus().toLowerCase()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}
}
