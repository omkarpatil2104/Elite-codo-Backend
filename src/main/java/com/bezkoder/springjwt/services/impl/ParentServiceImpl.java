package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.bezkoder.springjwt.payload.response.StudentOverallPerformanceResponse.*;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Service
public class ParentServiceImpl implements ParentService {

    @Autowired
    private TestSubmissionRepository testSubmissionRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestOfflineSubmissionRepository testOfflineSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentLeaderboardRepository studentLeaderboardRepository;

    public double calculateAttendancePercentage(Long parentId) {
        Long studentId = userRepository.getStudentIdByParentId(parentId);
        long  submittedTests= testRepository.countTestsCreatedForStudentByTeacher(studentId);
        long totalTests = testSubmissionRepository.countSubmittedTestsByStudent(studentId);
        System.out.println("totalTest======"+totalTests);
        System.out.println("submittedTest======"+submittedTests);

        // Avoid division by zero if the teacher(s) haven't created any tests
        if (totalTests == 0) {
            return 0.0;
        }
        // (submitted / total) * 100
        double response= Math.round(((double) submittedTests / (double) totalTests * 100.0)*100)/100.0;
        if(response>100){
            return 100.00;
        }else{
            return response;
        }
    }

    @Override
    public double calculateOfflineAttendancePercentage(Long parentId) {
        Long studentId = userRepository.getStudentIdByParentId(parentId);
        // Count total offline tests created for the student by teacher
//        long totalOfflineTests = testRepository.countOfflineTestsCreatedForStudentByTeacher(studentId);
//        // Count offline test submissions by the student
//        long submittedOfflineTests = testOfflineSubmissionRepository.countOfflineSubmittedTestsByStudent(studentId);
//
//        // Avoid division by zero if no offline tests exist
//        if (totalOfflineTests == 0) {
//            return 0.0;
//        }
//        // (submitted / total) * 100
//        return (double) submittedOfflineTests / totalOfflineTests * 100.0;
        return 0;
    }


    @Override
    public double calculatePerformancePercentage(Long parentId) {

        Long studentId = userRepository.getStudentIdByParentId(parentId);


        Long teacherId = userRepository.findTeacherIdByStudentId(studentId);


        // Retrieve online test submissions for the student
        List<TestSubmission> onlineSubmissions = testSubmissionRepository.findByUser_IdAndTest_CreatedBy_Id(studentId,teacherId);
        // Retrieve offline test submissions for the student
        List<TestOfflineSubmission> offlineSubmissions = testOfflineSubmissionRepository.findByStudent_Id(studentId);

        List<Double> testPercentages = new ArrayList<>();

        // Process each online test submission and calculate percentage
        for (TestSubmission submission : onlineSubmissions) {
            TestMaster test = submission.getTest();
            double testTotalMarks = test.getQuestionMasters()
                    .stream()
                    .mapToDouble(q -> q.getMarks() != null ? q.getMarks() : 0)
                    .sum();

            double obtainedForThisTest = 0;
            for (TestSubmissionDetail detail : submission.getSubmissionDetails()) {
                QuestionMaster question = detail.getQuestion();
                if (isAnswerCorrect(detail.getSelectedAnswers(), question)) {
                    obtainedForThisTest += question.getMarks() != null ? question.getMarks() : 0;
                }
            }
            if (testTotalMarks > 0) {
                testPercentages.add((obtainedForThisTest / testTotalMarks) * 100);
            }
        }

        // Process each offline test submission and calculate percentage
        for (TestOfflineSubmission offlineSubmission : offlineSubmissions) {
            TestMaster test = offlineSubmission.getTest();
            double testTotalMarks = test.getMarks() != null ? test.getMarks() : 0;

            int obtained = offlineSubmission.getGrades().values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            if (testTotalMarks > 0) {
                testPercentages.add((obtained / testTotalMarks) * 100);
            }
        }

        // Compute the average percentage across all tests
        return testPercentages.isEmpty() ? 0 : testPercentages.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    // Helper method to verify if the student's selected answers match the correct answers.
    private boolean isAnswerCorrect(List<String> selectedAnswers, QuestionMaster question) {
        // If there are multiple correct answers
        if (question.getMultiAnswers() != null && !question.getMultiAnswers().isEmpty()) {
            Set<String> correctAnswers = question.getMultiAnswers();
            Set<String> providedAnswers = new HashSet<>();
            if (selectedAnswers != null) {
                providedAnswers.addAll(selectedAnswers);
            }
            return correctAnswers.equals(providedAnswers);
        } else {
            // Otherwise, use the single correct answer
            if (selectedAnswers != null && !selectedAnswers.isEmpty() && question.getAnswer() != null) {
                return selectedAnswers.get(0).trim().equalsIgnoreCase(question.getAnswer().trim());
            }
        }
        return false;
    }



    public StudentSubReportResponse generateReport(Long parentId) {
        try {
            // 1) Fetch student from DB
            Long studentId = userRepository.getStudentIdByParentId(parentId);

            if (studentId == null) {
                throw new RuntimeException("No student found for the given parent ID: " + parentId);
            }

            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Long teacherId = userRepository.findTeacherIdByStudentId(studentId);

            if (teacherId == null) {
                throw new RuntimeException("No teacher found for the given student ID: " + studentId);
            }

            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));


            // 2) Initialize response object
            StudentSubReportResponse response = new StudentSubReportResponse();
            response.setStudentName(student.getFirstName() + " " + student.getLastName());
            response.setStudentClass(teacher.getClassName());
            response.setRollNumber(student.getId().toString());

            // 3) Prepare a Map<SubjectMaster, Integer> to accumulate marks
            Map<SubjectMaster, Integer> subjectWiseMarks = new HashMap<>();
            Map<SubjectMaster, Integer> subjectWiseTotalMarks = new HashMap<>();

            // 4) Derive marks from online submissions
            List<TestSubmission> onlineSubmissions = testSubmissionRepository.findByUser_IdAndTest_CreatedBy_Id(studentId, teacherId);
//        System.out.println("-------------------"+onlineSubmissions);
            for (TestSubmission submission : onlineSubmissions) {
//            System.out.println("-------------------"+submission);
                // For each detail, if correct -> add question.marks to that subject
                for (TestSubmissionDetail detail : submission.getSubmissionDetails()) {
                    QuestionMaster question = detail.getQuestion();
                    // if question is from a certain subject
                    SubjectMaster subject = question.getSubjectMaster();
//                System.out.println("-------------------"+subject);
                    if (subject == null) continue;

                    boolean correct = isAnswerCorrect(detail.getSelectedAnswers(), question); // define your own logic
                    if (question.getMarks() != null) {
                        if (correct) {
                            subjectWiseMarks.merge(subject, question.getMarks(), Integer::sum);
                        }
                        subjectWiseTotalMarks.merge(subject, question.getMarks(), Integer::sum);
                    }

                }
            }

            // 5) Derive marks from offline submissions (via grades map)
            List<TestOfflineSubmission> offlineSubmissions = testOfflineSubmissionRepository.findByStudent_Id(studentId);
            for (TestOfflineSubmission offlineSubmission : offlineSubmissions) {
                TestMaster test = offlineSubmission.getTest();
                // Each offline submission has a Map<String, Integer> subject_name -> marks
                Map<String, Integer> gradesMap = offlineSubmission.getGrades();

                for (Map.Entry<String, Integer> entry : gradesMap.entrySet()) {
                    // Try to find the subject object in DB by name
                    Integer subjectName = Integer.valueOf(entry.getKey());
                    SubjectMaster subject = subjectRepository.findBySubjectName(subjectName);
//                System.out.println("-------------------"+subject);
                    if (subject != null) {
                        subjectWiseMarks.merge(subject, entry.getValue(), Integer::sum);
                        if (test.getMarks() != null) {
                            subjectWiseTotalMarks.merge(subject, test.getMarks().intValue(), Integer::sum);
                        }
                    }
                }

            }

            // 6) Build SubjectDTO for each subject
            double totalMarks = 0;
            int count = 0;
            List<SubjectWiseReportResponse> subjectDTOs = new ArrayList<>();
            for (Map.Entry<SubjectMaster, Integer> entry : subjectWiseMarks.entrySet()) {
                count++;
                SubjectMaster subject = entry.getKey();
                Integer obtainedMarks = entry.getValue();
                Integer totalMarkss = subjectWiseTotalMarks.get(subject);

                double normalizedMarks = (totalMarkss > 0) ? (obtainedMarks / (double) totalMarkss) * 100 : 0;

                SubjectWiseReportResponse dto = new SubjectWiseReportResponse();
                dto.setName(subject.getSubjectName());
                dto.setMarks(normalizedMarks);
                dto.setGrade(calculateGrade(normalizedMarks));           // your custom method
                dto.setTeacher(teacher.getFirstName() + " " + teacher.getLastName()); // your custom method
                dto.setRemarks(generateRemarks(normalizedMarks));        // your custom method

                subjectDTOs.add(dto);
                totalMarks += normalizedMarks;
            }

            response.setSubjects(subjectDTOs);

            // 7) Compute totals, attendance, rank
            response.setTotalMarks(totalMarks);
            double maxMarks = count * 100;
            double percentage = (maxMarks > 0) ? (totalMarks / maxMarks) * 100 : 0;
            response.setPercentage(percentage);

            double attendance = calculateAttendancePercentage(studentId);
            response.setAttendance(attendance);


            Integer rank = studentLeaderboardRepository.findRankByStudentId(studentId);
            response.setRank(rank);

            return response;
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while generating the report", e);
        }
    }

    private String calculateGrade(Double marks) {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B+";
        else if (marks >= 60) return "B";
        else if (marks >= 50) return "C+";
        else if (marks >= 40) return "C";
        else return "F";
    }


    private String generateRemarks(Double marks) {
        if (marks >= 90) return "Outstanding performance";
        else if (marks >= 70) return "Very good understanding";
        else if (marks >= 50) return "Satisfactory";
        else return "Needs improvement";
    }


    public List<RecentTestReportResponse> getRecentTestReports(Long parentId) {
        Long studentId = userRepository.getStudentIdByParentId(parentId);

        // 1) Fetch all submissions for the student
        List<TestSubmission> submissions = testSubmissionRepository
                .findByUser_IdAndTest_CreatedBy_IdOrderBySubmittedAtDesc(studentId,studentId);

        // 2) Transform them into RecentTestReportResponse
        List<RecentTestReportResponse> responses = new ArrayList<>();
        // Keep track of the previous score to calculate improvement
        int previousScore = 0;
        boolean first = true;  // no improvement on the first test

        for (TestSubmission submission : submissions) {
            RecentTestReportResponse dto = new RecentTestReportResponse();
            dto.setId(submission.getTestSubmissionId());

            // subjectNames -> if test can have multiple subjects, collect them
            List<String> subjects = new ArrayList<>();
            if (submission.getTest().getSubjectMaster() != null) {
                submission.getTest().getSubjectMaster()
                        .forEach(sub -> subjects.add(sub.getSubjectName()));
            }
            dto.setSubjectNames(subjects);

            // testName
            dto.setTestName(submission.getTest().getTestName());

            // date (convert LocalDateTime to, say, yyyy-MM-dd string)
            dto.setDate(submission.getSubmittedAt().toLocalDate().toString());

            // totalQuestions, correctAnswers
            int totalQuestions = 0;
            int correctAnswers = 0;
            int totalTimeSeconds = 0;

            if (submission.getSubmissionDetails() != null) {
                totalQuestions = submission.getSubmissionDetails().size();

                for (TestSubmissionDetail detail : submission.getSubmissionDetails()) {
                    QuestionMaster question = detail.getQuestion();
                    // If you store correctness in detail or compare detail.getSelectedAnswers() to question
                    boolean isCorrect = isAnswerCorrect(detail.getSelectedAnswers(),question);
                    if (isCorrect) {
                        correctAnswers++;
                    }
                    totalTimeSeconds += detail.getTimeSpentSeconds();
                }
            }
            dto.setTotalQuestions(totalQuestions);
            dto.setCorrectAnswers(correctAnswers);

            // timeTaken -> convert totalTimeSeconds into something like HH:MM:SS
            dto.setTimeTaken(convertSecondsToHHMMSS(totalTimeSeconds));

            // score (e.g., if you treat it as a percentage out of totalQuestions * 100)
            int score = 0;
            if (totalQuestions > 0) {
                score = (int) Math.round(((double) correctAnswers / totalQuestions) * 100);
            }
            dto.setScore(score);

            // improvement -> difference from previous test's score
            if (first) {
                dto.setImprovement(0);
                first = false;
            } else {
                dto.setImprovement(score - previousScore);
            }
            previousScore = score;

            responses.add(dto);
        }

        return responses;
    }

    /**
     * Example logic to determine correctness.
     * Adjust as needed, e.g. you might have a boolean "isCorrect" in detail,
     * or you compare detail.getSelectedAnswers() to the question's correctAnswers.
     */
    private boolean isAnswerCorrect(TestSubmissionDetail detail) {
        // For demonstration, always returning 'true'
        // In real logic, compare with question's correctAnswers
        return true;
    }

    /**
     * Convert total seconds into an HH:MM:SS string.
     */
    private String convertSecondsToHHMMSS(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int remainder = totalSeconds % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static final String[] COLOR_PALETTE = {
            "#1890FF", "#52C41A", "#722ED1", "#FA8C16",
            "#F5222D", "#13C2C2", "#EB2F96", "#FAAD14"
    };

    public List<SubjectPerformanceResponse> getSubjectPerformance(Long parentId) {
        // 1) Fetch all “practice” test submissions
        //    => test created by the same student who took it
        Long studentId = userRepository.getStudentIdByParentId(parentId);
        List<TestSubmission> submissions =
                testSubmissionRepository.findByUser_IdAndTest_CreatedBy_Id(studentId, studentId);

        // 2) subjectTotals -> map each SubjectMaster to a running total (or average)
        Map<SubjectMaster, Double> subjectTotals = new HashMap<>();
        // (You could also track number of attempts per subject if computing an average)

        // Example logic:
        for (TestSubmission submission : submissions) {
            List<TestSubmissionDetail> details = submission.getSubmissionDetails();
            if (details == null) continue;

            for (TestSubmissionDetail detail : details) {
                QuestionMaster question = detail.getQuestion();
                if (question.getSubjectMaster() == null) continue;

                SubjectMaster subject = question.getSubjectMaster();
                double marksForThisQ = (question.getMarks() != null) ? question.getMarks() : 0.0;

                // If you want to count correctness:
                boolean correct = isAnswerCorrect(detail.getSelectedAnswers(), question);
                double earned = correct ? marksForThisQ : 0.0;

                subjectTotals.merge(subject, earned, Double::sum);
            }
        }

        // 3) Convert these totals into a list of subject -> total
        //    Then sum them up to normalize
        double totalSum = subjectTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        // If totalSum == 0, we can skip or default everything to 0

        // 4) Build the final List<SubjectPerformanceResponse>
        List<SubjectPerformanceResponse> response = new ArrayList<>();
        int colorIndex = 0;

        for (Map.Entry<SubjectMaster, Double> entry : subjectTotals.entrySet()) {
            SubjectMaster subject = entry.getKey();
            double total = entry.getValue();

            System.out.println("----------total"+total);
            System.out.println("----------totalsum"+totalSum);
            double fraction = 0.0;
            if (totalSum > 0) {
                fraction = (total / totalSum) * 100.0;  // so sum is ~100
            }

            SubjectPerformanceResponse dto = new SubjectPerformanceResponse();
            dto.setName(subject.getSubjectName());
            dto.setValue(Math.round(fraction)); // or keep as double

            // Apply color from palette
            SubjectPerformanceResponse.ItemStyle style = new SubjectPerformanceResponse.ItemStyle();
            style.setColor(COLOR_PALETTE[colorIndex % COLOR_PALETTE.length]);
            colorIndex++;

            dto.setItemStyle(style);
            response.add(dto);
        }

        return response;
    }

    @Override
    public StudentOverallPerformanceResponse getStudentOverallPerformance(Long parentId) {

        /* 1) resolve student for this parent */
        Long studentId = userRepository.getStudentIdByParentId(parentId);
        if (studentId == null) {
            throw new RuntimeException("No student linked to parent " + parentId);
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Long teacherId = userRepository.findTeacherIdByStudentId(studentId);

        /* 2) fetch all of that student’s submissions, oldest-->newest */
        List<TestSubmission> online = testSubmissionRepository
                .findByUser_IdOrderBySubmittedAtAsc(studentId);
        List<TestOfflineSubmission> offline = testOfflineSubmissionRepository
                .findByStudentIdOrderByTestDateAsc(studentId);

        /* helper class to pair date + percentage score */
        class Rec {
            private final Date date;
            private final double score;
            Rec(Date date, double score) {
                this.date = date;
                this.score = score;
            }
            Date getDate() { return date; }
            double getScore() { return score; }
        }

        List<Rec> allRecs = new ArrayList<>();
        online.forEach(s -> allRecs.add(new Rec(
                Date.from(s.getSubmittedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                s.getScore())));
        offline.forEach(o -> {
            Date d = o.getTest().getTestDate();
            allRecs.add(new Rec(d, o.getScore()));
        });

        /* if nothing—return empty shell */
        if (allRecs.isEmpty()) {
            return new StudentOverallPerformanceResponse();
        }

        /* 3) overall grade + improvement */
        double overall = allRecs.stream()
                .mapToDouble(Rec::getScore)
                .average()
                .orElse(0.0);

        double improvement = allRecs.size() > 1
                ? allRecs.get(allRecs.size() - 1).getScore()
                - allRecs.get(0).getScore()
                : 0.0;

        /* 4) attendance = submitted / assigned * 100 */
        long assigned = testRepository.countByCreatedById(teacherId);
        int attendance = assigned == 0
                ? 0
                : (int) Math.round(
                ((double) online.size() + offline.size())
                        / assigned * 100);

        /* ---------------------------------------------------
           5) Rank & percentile – use StudentLeaderboard rows
           --------------------------------------------------- */
        List<StudentLeaderboard> board =
                studentLeaderboardRepository.findByTeacher(teacherId);
        if (board.isEmpty()) {
            board = Collections.singletonList(
                    new StudentLeaderboard(null, student, overall, 1, null));
        }
        board.sort(Comparator.comparingDouble(
                StudentLeaderboard::getTotalMarks).reversed());

        int rank = 0, denseRank = 0;
        double prevScore = Double.NaN;
        for (StudentLeaderboard sl : board) {
            rank++;
            if (Double.isNaN(prevScore) || sl.getTotalMarks() != prevScore) {
                denseRank = rank;
                prevScore = sl.getTotalMarks();
            }
            if (sl.getStudent().getId().equals(studentId)) break;
        }
        int total = board.size();
        int percentile = (int) Math.ceil(
                (1d - (denseRank - 1) / (double) total) * 100);

        /* 6) subject-wise aggregation */
        Map<String,List<Rec>> subjMap = new HashMap<>();

        /* 6a) offline % */
        offline.forEach(o -> {
            Date d = o.getTest().getTestDate();
            o.getGrades().forEach((sub, pct) ->
                    subjMap.computeIfAbsent(sub, k -> new ArrayList<>())
                            .add(new Rec(d, pct)));
        });

        /* 6b) online % */
        for (TestSubmission ts : online) {
            Date d = Date.from(ts.getSubmittedAt()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

            Map<String,Double> got = new HashMap<>(), tot = new HashMap<>();
            for (TestSubmissionDetail det : ts.getSubmissionDetails()) {
                QuestionMaster q = det.getQuestion();
                SubjectMaster sm = q.getSubjectMaster();
                if (sm == null) continue;

                String sub = sm.getSubjectName();
                double marks = q.getMarks();
                tot.merge(sub, marks, Double::sum);
                if (isCorrect(det.getSelectedAnswers(), q)) {
                    got.merge(sub, marks, Double::sum);
                }
            }
            for (String sub : tot.keySet()) {
                double pct = tot.get(sub) > 0
                        ? (got.getOrDefault(sub, 0.0) / tot.get(sub)) * 100.0
                        : 0.0;
                subjMap.computeIfAbsent(sub, k -> new ArrayList<>())
                        .add(new Rec(d, pct));
            }
        }

        /* 6c) class average per subject */
        Map<String,Double> classAvg = computeClassSubjectAverages(teacherId);

        List<SubjectPerformance> subjPerf = subjMap.entrySet().stream()
                .map(e -> {
                    String sName = e.getKey();
                    List<Rec> recs = e.getValue();
                    recs.sort(Comparator.comparing(Rec::getDate));

                    double stuAvg = recs.stream()
                            .mapToDouble(Rec::getScore)
                            .average()
                            .orElse(0.0);

                    double clsAvg = classAvg.getOrDefault(sName, 0.0);
                    double imp   = recs.size() > 1
                            ? recs.get(recs.size() - 1).getScore()
                            - recs.get(0).getScore()
                            : 0.0;
                    String trend = imp > 0  ? "up"
                            : imp < 0  ? "down"
                            :            "stable";

                    return new SubjectPerformance(
                            sName,
                            Math.round(stuAvg * 10) / 10.0,
                            Math.round(clsAvg * 10) / 10.0,
                            Math.round(imp * 10) / 10.0,
                            trend
                    );
                })
                .collect(Collectors.toList());

        /* 7) assemble DTO */
        StudentInfo sInfo = new StudentInfo(
                (student.getFirstName() + " " + student.getLastName()).trim(),
                null,//TODO: calculate overall grade
                student.getId().toString(),
                student.getProfilePicture()
        );
        ClassRank cRank = new ClassRank(denseRank, "Top " + percentile + "%");



        return new StudentOverallPerformanceResponse(
                sInfo,
                Math.round(overall * 10) / 10.0,
                attendance,
                cRank,
                Math.round(improvement * 10) / 10.0,  //TODO: this need to be checked
                subjPerf
        );
    }

//    @Override
//    public MockSummaryDTO getMockSummary(Long parentId) {
//
//        if (parentId == null || parentId <= 0)
//            throw new IllegalArgumentException("parentId must be positive");
//
//
//        Long studentId = userRepository.getStudentIdByParentId(parentId);
//        if (studentId == null)
//            throw new Apierrorr("No student linked to parent " + parentId,"404");
//
//        Long teacherId = userRepository.findTeacherIdByStudentId(studentId);
//
//        long onlineCompleted = testSubmissionRepository.countCompletedMocks(studentId,teacherId);
//        long offlineCompleted = testOfflineSubmissionRepository.countOfflineSubmittedTestsByStudent(studentId);
//        long total     = testRepository.countTestsCreatedForStudentByTeacher(studentId);
//
//        long totalCompleted = onlineCompleted+offlineCompleted;
//
//        long pending   = Math.max(total - totalCompleted, 0);
//
//        double sumOnline = testSubmissionRepository.sumMockScore(studentId);
//        double sumOffline = testOfflineSubmissionRepository.sumScoreForStud(studentId);
//
//        double average    = totalCompleted > 0
//                ? (sumOnline + sumOffline) / totalCompleted
//                : 0.0;
//
//        return new MockSummaryDTO(totalCompleted, pending, average);
//    }
@Override
public MockSummaryDTO getMockSummary(Long parentId) {

    if (parentId == null || parentId <= 0)
        throw new IllegalArgumentException("parentId must be positive");

    Long studentId = userRepository.getStudentIdByParentId(parentId);
    if (studentId == null)
        throw new Apierrorr("No student linked to parent " + parentId, "404");

    /* ---------- 1. counts ---------- */
//    Long teacherId = userRepository.findTeacherIdByStudentId(studentId);

    // teacher-assigned mock tests (total, regardless of completion)
    long teacherMocks = testRepository.countTestsCreatedForStudentByTeacher(studentId);

    // practice mocks created by the student
    long practiceMocks = testRepository.countPracticeMocks(studentId);

    long totalMocks = teacherMocks + practiceMocks;

    // for average calculation
    long onlineCompleted = testSubmissionRepository.countCompletedMocks(studentId);
    long offlineCompleted = testOfflineSubmissionRepository.countOfflineSubmittedTestsByStudent(studentId);
    long totalCompleted = onlineCompleted+offlineCompleted;

    /* for mock---------- 2. completed ---------- */
    long completedMock   = testSubmissionRepository.countAllCompletedOnline(studentId);


    /* -for mock--------- 3. pending ---------- */
    long pending = Math.max(totalMocks - completedMock, 0);

    /* ---------- 4. average score ---------- */
    double sumOnline   = testSubmissionRepository.sumALLScore(studentId);
    double sumOffline  = testOfflineSubmissionRepository.sumAllOfflineScores(studentId);
    double average     = totalCompleted > 0
            ? (sumOnline + sumOffline) / totalCompleted
            : 0.0;

    return new MockSummaryDTO(completedMock, pending, average);
}


    /** unchanged helper for correctness */
    private boolean isCorrect(List<String> sel, QuestionMaster q) {
        if (q == null) return false;
        if (q.getMultiAnswers() != null && !q.getMultiAnswers().isEmpty()) {
            Set<String> correct = q.getMultiAnswers().stream()
                    .map(String::trim).map(String::toLowerCase)
                    .collect(Collectors.toSet());
            Set<String> chosen = Optional.ofNullable(sel)
                    .orElse(Collections.<String>emptyList())
                    .stream()
                    .map(String::trim).map(String::toLowerCase)
                    .collect(Collectors.toSet());
            return chosen.equals(correct);
        }
        String ans = Optional.ofNullable(q.getAnswer())
                .orElse("")
                .trim().toLowerCase();
        String choice = Optional.ofNullable(sel)
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .orElse("")
                .trim().toLowerCase();
        return choice.equals(ans);
    }

    /**
     * Aggregates *all* online+offline submissions for tests & students under this teacher
     * into subject → average %.
     */
    public Map<String,Double> computeClassSubjectAverages(Long teacherId) {
        // a) students under teacher
        List<Long> studentIds = userRepository.findStudentsByTeacherId(teacherId);
        if (studentIds.isEmpty()) return Collections.emptyMap();

        // b) tests by teacher
        List<Integer> testIds = testRepository.findIdsByTeacher(teacherId);
        if (testIds.isEmpty()) return Collections.emptyMap();

        // c) fetch submissions
        List<TestSubmission>   online =
                testSubmissionRepository.findByTestIdsAndStudentIds(testIds, studentIds);
        List<TestOfflineSubmission> offline =
                testOfflineSubmissionRepository.findByTestIdsAndStudentIds(testIds, studentIds);

        // d) accumulate
        Map<String,DoubleSummaryStatistics> stats = new HashMap<>();

        // offline already in %
        offline.forEach(o ->
                o.getGrades().forEach((sub,pct)->
                        stats.computeIfAbsent(sub,k->new DoubleSummaryStatistics())
                                .accept(pct.doubleValue())));

        // online compute %
        online.forEach(ts -> {
            Map<String,Double> got = new HashMap<>(), tot = new HashMap<>();
            ts.getSubmissionDetails().forEach(det -> {
                QuestionMaster q = det.getQuestion();
                SubjectMaster sm = q.getSubjectMaster();
                if (sm == null) return;
                String sub = sm.getSubjectName();
                double marks = q.getMarks();
                tot.merge(sub, marks, Double::sum);
                if (isCorrect(det.getSelectedAnswers(), q))
                    got.merge(sub, marks, Double::sum);
            });
            for (String sub : tot.keySet()) {
                double pct = tot.get(sub) > 0
                        ? (got.getOrDefault(sub,0.0)/tot.get(sub)) * 100
                        : 0;
                stats.computeIfAbsent(sub,k->new DoubleSummaryStatistics())
                        .accept(pct);
            }
        });

        // e) to subject→avg%
        return stats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round(e.getValue().getAverage()*10)/10.0
                ));
    }
}
