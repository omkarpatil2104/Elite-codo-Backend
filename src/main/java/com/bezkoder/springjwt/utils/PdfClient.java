package com.bezkoder.springjwt.utils;

import com.bezkoder.springjwt.dto.PDFChrunchAnsKeyReq;
import com.bezkoder.springjwt.dto.PDFChrunchAnsKeyRes;
import com.bezkoder.springjwt.dto.PDFCrunchRes;
import com.bezkoder.springjwt.payload.request.AnswerKeyReq;
import com.bezkoder.springjwt.payload.request.OmrReq;
import com.bezkoder.springjwt.payload.response.AnswerKeyRes;
import com.bezkoder.springjwt.payload.response.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class PdfClient {


    @Value("${my.node.url}")
    private String nodePdfBaseUrl;

    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    public PdfClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Send grouped JSON to Node and get the PDF bytes back.
     */
    public byte[] fetchPdf(ExamResponseDTO payload) {
        String url;
        if ("CHINTAMANI".equalsIgnoreCase(payload.getInstitute())) {
            // CHINTAMANI → “/formatted” on the same base
            url = nodePdfBaseUrl ;
        } else if ("TEST_PLANNERS".equalsIgnoreCase(payload.getInstitute())) {
            // TEST_PLANNERS → “/formatted3”
            url = nodePdfBaseUrl + "/formatted3";
        } else {
            // default → “/formatted”
            url = nodePdfBaseUrl + "/formatted";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        HttpEntity<ExamResponseDTO> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalStateException("Node service failed: " + response.getStatusCode());
    }

    /**
     * Example of a second PDF endpoint (for “chapter”) off the same base URL:
     *    Base = http://localhost:4000/api/pdf
     *    Then + "/chapter" → http://localhost:4000/api/pdf/chapter
     */
    public byte[] fetchPdf1(PDFCrunchRes payload) {
        String url = nodePdfBaseUrl + "/chapter";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        HttpEntity<PDFCrunchRes> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalStateException("Node service failed: " + response.getStatusCode());
    }

    /**
     * OMR‐PDF endpoint. We simply append “/omr” to the base.
     *
     * @param omrReq must contain at least instituteName and totalQuestions
     */
    public byte[] fetchOmrPdf(OmrReq omrReq) {
        // Build the full OMR URL out of the single base property.
        String url = nodePdfBaseUrl + "/omr";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        HttpEntity<OmrReq> entity = new HttpEntity<>(omrReq, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalStateException("Node service failed: " + response.getStatusCode());
    }

    public byte[] fetchAnswerKeyPdf(AnswerKeyRes answerKeyRes) {
        String url = nodePdfBaseUrl + "/ansKey";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        HttpEntity<AnswerKeyRes> entity = new HttpEntity<>(answerKeyRes, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalStateException("Node service failed: " + response.getStatusCode());
    }

    public byte[] fetchAnswerKeyPdfCh(PDFChrunchAnsKeyRes res) {
        String url = nodePdfBaseUrl + "/ansKeyCh";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        HttpEntity<PDFChrunchAnsKeyRes> entity = new HttpEntity<>(res, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalStateException("Node service failed: " + response.getStatusCode());

    }
}
