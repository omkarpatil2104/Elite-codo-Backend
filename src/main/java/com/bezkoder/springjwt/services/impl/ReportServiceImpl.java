package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.TestOfflineSubmissionRepository;
import com.bezkoder.springjwt.repository.TestRepository;
import com.bezkoder.springjwt.repository.TestSubmissionRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestSubmissionRepository submissionRepository;

    @Autowired
    private TestOfflineSubmissionRepository offlineRepository;

    @Override
    public List<TeacherReportResponse> getTeachersReportByIntitute(Long instituteId) {
        // 1) fetch only id + name for all teachers in the institute
        List<TeacherIdNameProjection> teachers =
                userRepository.findTeachersByInstituteId(instituteId);

        // 2) for each teacher, call your new countStudentsByTeacherId(...)
        return teachers.stream()
                .map(t -> {
                    TeacherReportResponse res = new TeacherReportResponse();
                    res.setId(t.getId());
                    res.setName(t.getName());

                    // use the new repository method
                    Long studentCount = userRepository.countStudentsByTeacherId(t.getId());
                    res.setStudentCount(studentCount != null
                            ? studentCount.intValue()
                            : 0);

                    long testCount = testRepository.countByCreatedById(t.getId());
                    res.setTestCount((int) testCount);
                    // TODO: similarly call countTestsByTeacherId, avgScoreByTeacherId, completionRateByTeacherId
                    List<TestSubmission> onlineSubs =
                            submissionRepository.findByTest_CreatedBy_Id(t.getId());
                    List<TestOfflineSubmission> offlineSubs =
                            offlineRepository.findByTest_CreatedBy_Id(t.getId());

                    int totalSubs = onlineSubs.size() + offlineSubs.size();

                    // 5) Average score across all submissions
                    double avgScore = 0.0;
                    if (totalSubs > 0) {
                        double sum = 0.0;
                        for (TestSubmission s : onlineSubs) {
                            sum += s.getScore();
                        }
                        for (TestOfflineSubmission s : offlineSubs) {
                            sum += s.getScore();
                        }
                        avgScore = sum / totalSubs;
                    }
                    res.setAvgScore(avgScore);

                    // 6) Completion rate = (total submissions) / (studentCount * testCount) * 100
                    double completionRate = 0.0;
                    if (studentCount > 0 && testCount > 0) {
                        completionRate = (double) Math.round((double) totalSubs / (studentCount * testCount) * 100.0 * 100.0) /100 ;
                    }
                    res.setCompletionRate(completionRate);

                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentPerfResponse> getTeacherPerformance(Long teacherId) {
        List<TeacherIdNameProjection> students = userRepository.findStudentsIdNameByTeacherId(teacherId);

        // 2) Total tests created by teacher
        long totalTests = testRepository.countByCreatedById(teacherId);

        List<StudentPerfResponse> list = students.stream().map(student -> {
            // 3) Fetch submissions *for this teacher's tests* by this student
            List<TestSubmission> online = submissionRepository.findByStudentAndTeacher(student.getId(), teacherId);
            List<TestOfflineSubmission> offline = offlineRepository.findByStudentAndTeacher(student.getId(), teacherId);

            // 4) Combine and compute averageScore
            List<Double> allScores = new ArrayList<>();
            online.forEach(s -> allScores.add(s.getScore()));
            offline.forEach(o -> allScores.add(o.getScore()));

            double average = allScores.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            // 5) completionRate = 100 * (# submissions) / (totalTests)
            double completionRate = totalTests == 0
                    ? 0.0
                    : (double) Math.round((allScores.size() / (double) totalTests) * 100.0 * 100.0) /100;

            // 6) Build DTO
            return new StudentPerfResponse(
                    student.getName(),
                    average,
                    completionRate
            );
        }).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<TestGroupResponse> getTeacherTests(Long teacherId) {
        // 1) ensure teacher exists
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherId));

        // 2) fetch all tests created by this teacher
        List<TestMaster> tests = testRepository.findAllByCreatedBy(teacher);
        if (tests.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) count tests per month
        Map<Integer,Integer> testsByMonth = new HashMap<>();
        for (TestMaster t : tests) {
            if (t.getTestDate() == null) continue;
            int m = t.getTestDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .getMonthValue();
            testsByMonth.merge(m, 1, Integer::sum);
        }

        // 4) gather all submissions across those tests
        List<TestSubmission> onlineSubs  = submissionRepository.findByTest_CreatedBy_Id(teacherId);
        List<TestOfflineSubmission> offSubs = offlineRepository.findByTest_CreatedBy_Id(teacherId);

        // 5) bucket submission scores by month of the testDate
        Map<Integer, List<Double>> scoresByMonth = new HashMap<>();
        for (int m = 1; m <= 12; m++) scoresByMonth.put(m, new ArrayList<>());

        for (TestSubmission s : onlineSubs) {
            if (s.getTest().getTestDate() == null) continue;
            int m = s.getTest().getTestDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .getMonthValue();
            scoresByMonth.get(m).add(s.getScore());
        }
        for (TestOfflineSubmission s : offSubs) {
            if (s.getTest().getTestDate() == null) continue;
            int m = s.getTest().getTestDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .getMonthValue();
            scoresByMonth.get(m).add(s.getScore());
        }

        // 6) build the month-group DTOs, only for months that have tests
        List<TestGroupResponse> groups = testsByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    int monthNum = e.getKey();
                    int count    = e.getValue();
                    List<Double> scores = scoresByMonth.getOrDefault(monthNum, Collections.emptyList());
                    double avg = scores.isEmpty()
                            ? 0.0
                            : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                    String monthName = java.time.Month.of(monthNum)
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                    return new TestGroupResponse(monthName, count, avg);
                })
                .collect(Collectors.toList());

        return groups;
    }

    @Override
    public List<QuestionPerformanceResponse>  getTestPerformance(Integer testId) {
        List<TestSubmissionDetail> details = submissionRepository.findByTestId(testId);

        Map<Integer, List<TestSubmissionDetail>> groupedByQuestion = details.stream()
                .collect(Collectors.groupingBy(d -> d.getQuestion().getQuestionId()));

        List<QuestionPerformanceResponse> questionStats = groupedByQuestion.entrySet().stream()
                .map(entry -> {
                    Integer qNum = entry.getKey();
                    List<TestSubmissionDetail> qDetails = entry.getValue();

                    long correctCount = qDetails.stream()
                            .filter(d -> isCorrect(d.getSelectedAnswers(), d.getQuestion()))
                            .count();

                    double avgTime = qDetails.stream()
                            .mapToInt(TestSubmissionDetail::getTimeSpentSeconds)
                            .average()
                            .orElse(0.0);

                    //TODO: this logic maybe need to update . testing remeaning
                    double correctPercentage = (qDetails.size() == 0) ? 0.0 :
                            (correctCount * 100.0) / qDetails.size();

                    return new QuestionPerformanceResponse(qNum, correctPercentage, avgTime);
                })
                .sorted(Comparator.comparingInt(QuestionPerformanceResponse::getNumber))
                .collect(Collectors.toList());

//        TestPerformanceResponse response = new TestPerformanceResponse();
//        response.setQuestions(questionStats);
        return questionStats;
    }

    @Override
    public List<ScoreRangeDTO> getScoreDistribution(Integer testId) {
        // ensure test exists by checking any submission or via testRepo if you prefer
        // (optional) userRepo or testRepo check omitted here

        // 1) fetch all scores for this test
        List<Double> scores = new ArrayList<>();

        List<TestSubmission> online = submissionRepository.findOnlineSubsByTestId(testId);
        for (TestSubmission s : online) {
            scores.add(s.getScore());
        }

        List<TestOfflineSubmission> offline = offlineRepository.findOfflineSubsByTestId(testId);
        for (TestOfflineSubmission s : offline) {
            scores.add(s.getScore());
        }

        // 2) define the buckets
        int[][] buckets = {
                {   0,  20},
                {  21,  40},
                {  41,  60},
                {  61,  80},
                {  81, 100}
        };

        // 3) compute counts per bucket
        List<ScoreRangeDTO> ranges = Arrays.stream(buckets)
                .map(range -> {
                    int low = range[0], high = range[1];
                    long count = scores.stream()
                            .filter(score -> score >= low && score <= high)
                            .count();
                    String label = low + "-" + high + "%";
                    return new ScoreRangeDTO(label, count);
                })
                .collect(Collectors.toList());

//        return new ScoreDistributionResponse(ranges);
        return ranges;
    }

    @Override
    public List<StudentSummaryResponse> getAllStudents(Long instituteId) {
        // 1) Fetch both sets of students
        List<User> byInst    = userRepository.findStudentsByInstituteId(instituteId);
        List<User> byTeachers= userRepository.findStudentsByTeacherInstituteId(instituteId);

        // 2) Union them uniquely
        Set<User> students = new HashSet<>();
        students.addAll(byInst);
        students.addAll(byTeachers);

        // 3) Build intermediate list with stats, rank=0 placeholder
        List<StudentSummaryResponse> list = students.stream()
                .map(u -> {
                    Long sid = u.getId();
                    // submissions
                    List<TestSubmission> online = submissionRepository.findByUser_Id(sid);
                    List<TestOfflineSubmission> offline = offlineRepository.findByStudent_Id(sid);

                    int testsCompleted = online.size() + offline.size();

                    // collect scores
                    List<Double> scores = new ArrayList<>();
                    online.forEach(s -> scores.add(s.getScore()));
                    offline.forEach(s -> scores.add(s.getScore()));

                    double avgScore = scores.isEmpty()
                            ? 0.0
                            : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                    double highest = scores.isEmpty()
                            ? 0.0
                            : scores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

                    String name = (u.getFirstName()==null?"":u.getFirstName().trim())
                            + " " +
                            (u.getLastName()==null?"":u.getLastName().trim());

                    return new StudentSummaryResponse(
                            sid,
                            name.trim(),
                            testsCompleted,
                            Math.round(avgScore),
                            Math.round(highest),
                            0  // placeholder for rank
                    );
                })
                .collect(Collectors.toList());

        // 4) Sort by avgScore desc and assign dense ranks
        list.sort(Comparator.comparingDouble(StudentSummaryResponse::getAvgScore).reversed());
        double prevScore = Double.NaN;
//        int rank = 0, position = 0;
        int rank=0;
        for (StudentSummaryResponse r : list) {
//            position++;
            if (Double.isNaN(prevScore) || r.getAvgScore() != prevScore) {
//                rank = position;
                rank++;
                prevScore = r.getAvgScore();
            }
            r.setRank(rank);
        }

        return list;
    }

    @Override
    public List<TestProgressDTO > getStudentProgress(Long studentId) {
        // 1) fetch student's own submissions
        List<TestSubmission> onlineSubs  = submissionRepository.findByUser_Id(studentId);
        List<TestOfflineSubmission> offSubs = offlineRepository.findByStudent_Id(studentId);

        // 2) build unified list of records with testId, date, score
        class Record {
            Integer testId;
            Date    date;
            double  score;
            Record(Integer testId, Date date, double score) {
                this.testId = testId; this.date = date; this.score = score;
            }
        }
        List<Record> records = new ArrayList<>();
        onlineSubs.forEach(s ->
                records.add(new Record(
                        s.getTest().getTestId(),
                        Date.from(s.getSubmittedAt().atZone(ZoneId.systemDefault()).toInstant()),
                        s.getScore()
                ))
        );
        offSubs.forEach(o -> {
            Date d = o.getTest().getTestDate();
            records.add(new Record(
                    o.getTest().getTestId(),
                    d,
                    o.getScore()
            ));
        });

        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) sort by date ascending
        records.sort(Comparator.comparing(r -> r.date));

        // 4) gather all testIds and fetch class scores
        List<Integer> testIds = records.stream()
                .map(r -> r.testId)
                .distinct()
                .collect(Collectors.toList());

        List<TestSubmission> allOnline = submissionRepository.findByTestIds(testIds);
        List<TestOfflineSubmission> allOffline = offlineRepository.findByTestIdIn(testIds);

        // 5) compute class-average per testId
        Map<Integer, Double> avgByTest = new HashMap<>();
        for (Integer tid : testIds) {
            // collect scores for this test
            List<Double> scores = allOnline.stream()
                    .filter(s -> s.getTest().getTestId().equals(tid))
                    .map(TestSubmission::getScore)
                    .collect(Collectors.toList());
            allOffline.stream()
                    .filter(o -> o.getTest().getTestId().equals(tid))
                    .map(TestOfflineSubmission::getScore)
                    .forEach(scores::add);

            double avg = scores.isEmpty()
                    ? 0.0
                    : scores.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            avgByTest.put(tid, avg);
        }

        // 6) build the response DTOs
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        List<TestProgressDTO> dtoList = records.stream()
                .map(r -> {
                    // format date
                    String dateStr = r.date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(fmt);
                    double classAvg = avgByTest.getOrDefault(r.testId, 0.0);
                    return new TestProgressDTO(dateStr, r.score, Math.round(classAvg));
                })
                .collect(Collectors.toList());

//        return new StudentProgressResponse(dtoList);
        return dtoList;
    }

    @Override
    public List<SubjectDataDTO > getStudentSubjects(Long studentId) {
        // 1) Fetch student’s submissions
        List<TestSubmission>   onlineSubs  = submissionRepository.findByUser_Id(studentId);
        List<TestOfflineSubmission> offSubs = offlineRepository.findByStudent_Id(studentId);

        // 2) Collect all subject names this student has seen
        Set<String> subjectNames = new HashSet<>();
        offSubs.forEach(o -> subjectNames.addAll(o.getGrades().keySet()));
        for (TestSubmission s : onlineSubs) {
            for (TestSubmissionDetail d : s.getSubmissionDetails()) {
                QuestionMaster q = d.getQuestion();
                if (q.getSubjectMaster() != null) {
                    subjectNames.add(q.getSubjectMaster().getSubjectName());
                }
            }
        }

        // 3) Gather all testIds the student has taken
        Set<Integer> testIds = new HashSet<>();
        onlineSubs.forEach(s -> testIds.add(s.getTest().getTestId()));
        offSubs   .forEach(o -> testIds.add(o.getTest().getTestId()));

        // 4) Fetch ALL submissions across those tests, to build class‐wide stats
        List<TestSubmission>   allOnline = submissionRepository.findByTestIdIn(new ArrayList<>(testIds));
        List<TestOfflineSubmission> allOffline = offlineRepository.findByTestIdIn(new ArrayList<>(testIds));

        // 5) For each subject compute studentAvg and classAvg
        List<SubjectDataDTO> subjects = subjectNames.stream().map(subj -> {
            // A) Student’s subject‐wise percentages
            List<Double> studentPerc = new ArrayList<>();

            // Offline grades assumed as percentages
            for (TestOfflineSubmission o : offSubs) {
                Integer grade = o.getGrades().get(subj);
                if (grade != null) studentPerc.add(grade.doubleValue());
            }
            // Online: compute per‐submission % for this subject
            for (TestSubmission s : onlineSubs) {
                double obtained = 0, possible = 0;
                for (TestSubmissionDetail d : s.getSubmissionDetails()) {
                    QuestionMaster q = d.getQuestion();
                    if (q.getSubjectMaster() != null
                            && subj.equals(q.getSubjectMaster().getSubjectName())) {
                        possible += q.getMarks();
                        if (isCorrect(d.getSelectedAnswers(), q)) {
                            obtained += q.getMarks();
                        }
                    }
                }
                if (possible > 0) {
                    studentPerc.add((obtained / possible) * 100.0);
                }
            }
            double studentAvg = studentPerc.isEmpty()
                    ? 0.0
                    : studentPerc.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            // B) Class‐wide percentages
            List<Double> classPerc = new ArrayList<>();
            for (TestOfflineSubmission o : allOffline) {
                Integer grade = o.getGrades().get(subj);
                if (grade != null) classPerc.add(grade.doubleValue());
            }
            for (TestSubmission s : allOnline) {
                double obtained = 0, possible = 0;
                for (TestSubmissionDetail d : s.getSubmissionDetails()) {
                    QuestionMaster q = d.getQuestion();
                    if (q.getSubjectMaster() != null
                            && subj.equals(q.getSubjectMaster().getSubjectName())) {
                        possible += q.getMarks();
                        if (isCorrect(d.getSelectedAnswers(), q)) {
                            obtained += q.getMarks();
                        }
                    }
                }
                if (possible > 0) {
                    classPerc.add((obtained / possible) * 100.0);
                }
            }
            double classAvg = classPerc.isEmpty()
                    ? 0.0
                    : classPerc.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            return new SubjectDataDTO(
                    subj,
                    Math.round(studentAvg),
                    Math.round(classAvg)
            );
        }).collect(Collectors.toList());

//        return new StudentSubjectResponse(subjects);
        return  subjects;
    }

    // Helper method to check answer correctness
    private boolean isCorrect(List<String> selectedAnswers, QuestionMaster question) {
        Function<String,String> normalize = s ->
                Optional.ofNullable(s)
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .orElse("");

        // multi-answer?
        if (question.getMultiAnswers() != null && !question.getMultiAnswers().isEmpty()) {
            Set<String> correctSet = question.getMultiAnswers().stream()
                    .map(normalize)
                    .collect(Collectors.toSet());

            Set<String> provided = Optional.ofNullable(selectedAnswers)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(normalize)
                    .collect(Collectors.toSet());

            return provided.equals(correctSet);
        }

        // single-answer
        String correct = normalize.apply(question.getAnswer());
        String given   = Optional.ofNullable(selectedAnswers)
                .filter(list -> !list.isEmpty())
                .map(list -> normalize.apply(list.get(0)))
                .orElse("");

        return given.equals(correct);
    }

}
