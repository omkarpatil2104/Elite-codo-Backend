package com.bezkoder.springjwt.utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;

public class PdfToWordConverter {


    public static byte[] convertPdfToWord(byte[] pdfBytes) throws Exception {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
            Document pdfDoc = new Document(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            pdfDoc.save(outputStream, SaveFormat.DocX);
            return outputStream.toByteArray();
        } catch (Exception e) {
            System.err.println("Error during PDF to Word conversion: " + e.getMessage());
           // e.printStackTrace();
            throw e; // Let controller handle it
        }
    }

}


