package com.bezkoder.springjwt.config;


import com.bezkoder.springjwt.payload.response.FileUploadResDto;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/file")
public class FileHandlingController {

    @RequestMapping(value = "/uploadfile",method = RequestMethod.POST)
    public ResponseEntity fileHandelDemo(@RequestParam(value = "uploadfile") MultipartFile file) throws Exception {

        String s = file.getOriginalFilename();
        String extension = s.substring(s.lastIndexOf(".") + 1);

        if (extension.equalsIgnoreCase("png")  || extension.equalsIgnoreCase("txt")
                || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif") ||
                extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("pdf") ||
                extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("mp4") ||
                extension.equalsIgnoreCase("xlsx") ||extension.equalsIgnoreCase("xls") ||
                extension.equalsIgnoreCase("mov") || extension.equalsIgnoreCase("avi")) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            FileUploadResDto fileUploadResDto = new FileUploadResDto();
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            String host = "zplusglobalmarketinsights.com";
            int port = 21;
            String user = "kab8f0gfd7hd";
            String pwd = "Tech@Feb25#";

            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(host, port);
                ftpClient.login(user, pwd);
                ftpClient.enterLocalPassiveMode();

                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                File sourceFile = new File(filename);
                InputStream inputStream = new FileInputStream(convFile);

                boolean done = ftpClient.storeFile("public_html/auctionBanksImages/" + filename, inputStream);
                inputStream.close();
                if (done) {
                    System.out.println("file is uploaded successfully..............");
                }

            } catch (IOException e) {
                System.out.println("Exception occured while ftp : " + e);
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException e) {
                    System.out.println("Exception occured while ftp logout/disconnect : " + e);
                }
            }

            File deleteFile = new File(filename);

            if (deleteFile.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete the file");
            }
            fileUploadResDto.setPath("https://zplusglobalmarketinsights.com/auctionBanksImages/"+filename);
            fileUploadResDto.setStatus(true);
            return new ResponseEntity(fileUploadResDto, HttpStatus.OK);
        }
        else
        {
            FileUploadResDto fileUploadResDto = new FileUploadResDto();
            fileUploadResDto.setStatus(false);
            fileUploadResDto.setPath("Upload Image Only");
            return new ResponseEntity(fileUploadResDto, HttpStatus.OK);
        }
    }
}