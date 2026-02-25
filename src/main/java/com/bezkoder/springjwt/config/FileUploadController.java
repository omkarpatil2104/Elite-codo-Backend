package com.bezkoder.springjwt.config;

import com.bezkoder.springjwt.payload.response.FileResponse;
import com.bezkoder.springjwt.services.impl.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/file")
    public ResponseEntity uploadFile(@RequestParam("file")MultipartFile file)
    {

        FileResponse fileResponse=new FileResponse();

        try
        {
            String fileurl= cloudinaryService.uploadFile(file);
            fileResponse.setUrl(fileurl);
            return ResponseEntity.ok(fileResponse);
        }catch (Exception e)
        {
            return ResponseEntity.status(500).body(" File Upload Failed ");
        }
    }
}
