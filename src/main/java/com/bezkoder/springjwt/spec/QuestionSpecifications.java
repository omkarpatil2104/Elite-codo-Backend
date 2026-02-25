package com.bezkoder.springjwt.spec;

import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.payload.request.QuestionFilterRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;
import java.util.List;

public class QuestionSpecifications {

    public static Specification<QuestionMaster> withFilters(QuestionFilterRequest f) {

        return Specification
                .where(inList ("standardMaster","standardId",  f.getStandardIds()))
                .and  (inList ("subjectMaster", "subjectId",    f.getSubjectIds()))
                .and  (inList ("chapterMaster","chapterId",     f.getChapterIds()))
                .and  (inList ("topicMaster",  "topicId",       f.getTopicIds()))
                .and  (eq     ("questionLevel","questionLevel",     f.getQuestionLevel()))
                .and  (eq     (null,"questionCategory",         f.getQuestionCategory()))
                .and  (pyqClause(f.getPyq()));
    }

    // ---------- helpers -------------------------------------------------

    private static Specification<QuestionMaster> inList(
            String join, String field, List<Integer> ids) {

        return (ids==null || ids.isEmpty())
                ? null
                : (root, q, cb) ->
                (join==null
                        ? root.get(field)
                        : root.get(join).get(field))
                        .in(ids);
    }

    private static <T> Specification<QuestionMaster> eq(
            String join, String field, T value) {

        return (value==null)
                ? null
                : (root, q, cb) ->
                cb.equal(join==null
                        ? root.get(field)
                        : root.get(join).get(field), value);
    }

    /*  pyq = true  →  year <> currentYear
        pyq = false →  year =  currentYear
        pyq = null  →  ignore                                        */
    private static Specification<QuestionMaster> pyqClause(Boolean pyq){
        if (pyq == null) return null;

        String current = String.valueOf(java.time.Year.now().getValue());

        return (root, q, cb) -> pyq
                ? cb.notEqual(
                root.get("yearOfAppearance")      // YearOfAppearance entity
                        .get("yearOfAppearance"),      // <── correct field
                current)
                : cb.equal(
                root.get("yearOfAppearance")
                        .get("yearOfAppearance"),
                current);
    }
}
