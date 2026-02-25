package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.dto.PDFChrunchAnsKeyReq;
import com.bezkoder.springjwt.dto.PDFChrunchAnsKeyRes;
import com.bezkoder.springjwt.dto.PDFCrunchReq;
import com.bezkoder.springjwt.dto.PDFCrunchRes;
import com.bezkoder.springjwt.payload.request.AnswerKeyReq;
import com.bezkoder.springjwt.payload.request.ExamRequestDTO;
import com.bezkoder.springjwt.payload.request.OmrReq;
import com.bezkoder.springjwt.payload.response.AnswerKeyRes;
import com.bezkoder.springjwt.payload.response.ExamResponseDTO;
import com.bezkoder.springjwt.services.TestService;
import com.bezkoder.springjwt.utils.PdfClient;
import com.bezkoder.springjwt.utils.PdfToWordConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private TestService testService;

    @Autowired
    private PdfClient pdfClient;

    ////convert json format subject wise for pdf generation
//    @PostMapping("/pdf/convert_json")
//    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody ExamRequestDTO request) {
//        ExamResponseDTO response = testService.createExam(request);
//        return ResponseEntity.ok(response);
//    }
    @PostMapping(
            value    = "/createPDF",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> convertJson(
            @RequestBody ExamRequestDTO request) {

        // 1) Build grouped JSON
        ExamResponseDTO grouped = testService.createExam(request);

        // 2) Fetch PDF  bytes from Node
        byte[] pdfBytes = pdfClient.fetchPdf(grouped);

        // 3) Stream them back
        HttpHeaders hdrs = new HttpHeaders();
        hdrs.setContentDisposition(
                ContentDisposition.attachment().filename("exam.pdf").build()
        );
        hdrs.setContentLength(pdfBytes.length);
        if (!request.getIsPdf()) { // user wants DOCX
            try {
                byte[] wordBytes = PdfToWordConverter.convertPdfToWord(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("exam.docx").build());
                headers.setContentLength(wordBytes.length);

                return new ResponseEntity<>(wordBytes, headers, HttpStatus.OK);

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
            return new ResponseEntity<>(pdfBytes, hdrs, HttpStatus.OK);
    }

    @PostMapping(
            value    = "/createPDF1",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    ///this api for pdf generation of
    public ResponseEntity<byte[]> convertJson1(
            @RequestBody PDFCrunchReq request) {

        // 1) Build grouped JSON
        PDFCrunchRes grouped = testService.createPDF(request);

        // 2) Fetch PDF bytes from Node
        byte[] pdfBytes = pdfClient.fetchPdf1(grouped);

        // 3) Stream them back
        HttpHeaders hdrs = new HttpHeaders();
        hdrs.setContentDisposition(
                ContentDisposition.attachment().filename("exam.pdf").build()
        );
        if (!request.getIsPdf()) { // user wants DOCX
            try {
                byte[] wordBytes = PdfToWordConverter.convertPdfToWord(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("exam.docx").build());
                headers.setContentLength(wordBytes.length);

                return new ResponseEntity<>(wordBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
            return new ResponseEntity<>(pdfBytes, hdrs, HttpStatus.OK);
    }

    @PostMapping(
            value    = "/omr",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateOmr(@RequestBody OmrReq omrReq){

        // 2) Fetch PDF bytes from Node
        byte[] pdfBytes = pdfClient.fetchOmrPdf(omrReq);

        // 3) Stream them back
        HttpHeaders hdrs = new HttpHeaders();
        hdrs.setContentDisposition(
                ContentDisposition.attachment().filename("omr.pdf").build()
        );
        hdrs.setContentLength(pdfBytes.length);
        if (!omrReq.getIsPdf()) { // user wants DOCX
            try {
                byte[] wordBytes = PdfToWordConverter.convertPdfToWord(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("omr.docx").build());
                headers.setContentLength(wordBytes.length);

                return new ResponseEntity<>(wordBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
            return new ResponseEntity<>(pdfBytes, hdrs, HttpStatus.OK);
    }

    @PostMapping(
            value    = "/answerKey",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateAnsKey(@RequestBody AnswerKeyReq answerKeyReq) throws JsonProcessingException {

        AnswerKeyRes res = testService.createAnswerKey(answerKeyReq);
        // 2) Fetch PDF bytes from Node
        byte[] pdfBytes = pdfClient.fetchAnswerKeyPdf(res);

        // 3) Stream them back
        HttpHeaders hdrs = new HttpHeaders();
        hdrs.setContentDisposition(
                ContentDisposition.attachment().filename("answerKey.pdf").build()
        );
        hdrs.setContentLength(pdfBytes.length);
        if (!answerKeyReq.getIsPdf()) { // user wants DOCX
            try {
                byte[] wordBytes = PdfToWordConverter.convertPdfToWord(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("answerKey.docx").build());
                headers.setContentLength(wordBytes.length);

                return new ResponseEntity<>(wordBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
            return new ResponseEntity<>(pdfBytes, hdrs, HttpStatus.OK);
    }

    @PostMapping(
            value    = "/answerKeyCh",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateAnsKeyCh(@RequestBody PDFChrunchAnsKeyReq answerKeyReq) throws JsonProcessingException {

        PDFChrunchAnsKeyRes res = testService.createAnswerKeyCh(answerKeyReq);
        // 2) Fetch PDF bytes from Node
        byte[] pdfBytes = pdfClient.fetchAnswerKeyPdfCh(res);

        // 3) Stream them back
        HttpHeaders hdrs = new HttpHeaders();
        hdrs.setContentDisposition(
                ContentDisposition.attachment().filename("answerKey.pdf").build()
        );
        hdrs.setContentLength(pdfBytes.length);
        if (!answerKeyReq.getIsPdf()) { // user wants DOCX
            try {
                byte[] wordBytes = PdfToWordConverter.convertPdfToWord(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("answerKey.docx").build());
                headers.setContentLength(wordBytes.length);

                return new ResponseEntity<>(wordBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
            return new ResponseEntity<>(pdfBytes, hdrs, HttpStatus.OK);
    }

}
