package com.bezkoder.springjwt.spec;

import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.models.TeacherQuestionUsage;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class TeacherUsedSpec {

    public static Specification<QuestionMaster>
    filter(String usedStatus, Long teacherId) {

        if (teacherId == null || usedStatus == null || "ALL".equalsIgnoreCase(usedStatus))
            return null;   // no extra filter

        return (root, query, cb) -> {

            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<TeacherQuestionUsage> u = sub.from(TeacherQuestionUsage.class);
            sub.select(u.get("questionId"))
                    .where(cb.equal(u.get("teacherId"), teacherId));

            if ("UNUSED".equalsIgnoreCase(usedStatus)) {
                return cb.not(root.get("questionId").in(sub));
            }
            if ("USED".equalsIgnoreCase(usedStatus)) {
                return root.get("questionId").in(sub);
            }
            return null;
        };
    }
}
