package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.payload.request.EntranceExamRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.StandardResponse;
import com.bezkoder.springjwt.services.EntranceExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/entranceexam")
public class EntranceExamController {

    @Autowired
    private EntranceExamService entranceExamService;

    // ---------------- CREATE ----------------
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EntranceExamRequest entranceExamRequest) {
        MainResponse mainResponse = entranceExamService.create(entranceExamRequest);
        return Boolean.TRUE.equals(mainResponse.getFlag())
                ? ResponseEntity.ok(mainResponse)
                : ResponseEntity.badRequest().body(mainResponse);
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody EntranceExamRequest entranceExamRequest) {
        MainResponse mainResponse = entranceExamService.update(entranceExamRequest);
        return Boolean.TRUE.equals(mainResponse.getFlag())
                ? ResponseEntity.ok(mainResponse)
                : ResponseEntity.badRequest().body(mainResponse);
    }

    // ---------------- GET BY ID ----------------
    @GetMapping("/entranceexambyid/{entranceExamId}")
    public ResponseEntity<?> entranceExamById(@PathVariable String entranceExamId) {

        Integer examId = validateAndParseId(entranceExamId);
        if (examId == null) {
            return ResponseEntity.badRequest()
                    .body("entranceExamId must be a valid number");
        }

        EntranceExamMaster exam =
                entranceExamService.entranceExamById(examId);
        return ResponseEntity.ok(exam);
    }

    // ---------------- GET ALL ----------------
    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        List<EntranceExamMaster> exams = entranceExamService.getAll();
        return ResponseEntity.ok(exams);
    }

    // ---------------- GET ACTIVE ----------------
    @GetMapping("/activeentranceexam")
    public ResponseEntity<?> allActiveEntranceExam() {
        List<EntranceExamMaster> exams =
                entranceExamService.allActiveEntranceExam();
        return ResponseEntity.ok(exams);
    }

    // ---------------- ENTRANCE EXAM WISE STANDARD ----------------
    @GetMapping("/entranceexamwisestandard/{entranceExamId}")
    public ResponseEntity<?> entranceExamWiseStandard(
            @PathVariable String entranceExamId) {

        Integer examId = validateAndParseId(entranceExamId);
        if (examId == null) {
            return ResponseEntity.badRequest()
                    .body("entranceExamId must be a valid number");
        }

        StandardResponse response =
                entranceExamService.entranceExamWiseStandard(examId);
        return ResponseEntity.ok(response);
    }

    // ---------------- ENTRANCE EXAM WISE ACTIVE STANDARD ----------------
    @GetMapping("/entranceexamwiseActiveStandard/{entranceExamId}")
    public ResponseEntity<?> entranceExamWiseActiveStandard(
            @PathVariable String entranceExamId) {

        Integer examId = validateAndParseId(entranceExamId);
        if (examId == null) {
            return ResponseEntity.badRequest()
                    .body("entranceExamId must be a valid number");
        }

        StandardResponse response =
                entranceExamService.entranceexamwiseActiveStandard(examId);
        return ResponseEntity.ok(response);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/delete/{entranceExamId}")
    public ResponseEntity<?> delete(@PathVariable String entranceExamId) {

        Integer examId = validateAndParseId(entranceExamId);
        if (examId == null) {
            return ResponseEntity.badRequest()
                    .body("entranceExamId must be a valid number");
        }

        MainResponse response = entranceExamService.delete(examId);
        return ResponseEntity.ok(response);
    }

    // ---------------- COMMON VALIDATION METHOD ----------------
    private Integer validateAndParseId(String id) {
        if (id == null || id.trim().isEmpty() || id.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
