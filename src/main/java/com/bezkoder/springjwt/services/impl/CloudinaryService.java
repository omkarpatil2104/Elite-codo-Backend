package com.bezkoder.springjwt.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {


    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        try {

            String folderName="Devesh";
            HashMap<Object,Object> option=new HashMap<>();
            option.put("folder",folderName);

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String publicId= (String) uploadResult.get("public_id");

            String rurl=cloudinary.url().secure(true).generate(publicId);

            return uploadResult.get("url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
}
