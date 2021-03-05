package com.minio.poc;

import io.minio.messages.Item;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
public class MinioController {
    @Autowired
    MinioService minioService;

    @PostMapping
    public void addAttachement(@RequestParam("file") MultipartFile file) {
        Path path = Path.of(file.getOriginalFilename());
        try {
            MinioService.upload( path, file.getInputStream(), file.getContentType());
        } catch (Exception e) {
            throw new IllegalStateException("The file cannot be read", e);
        }
    }


    @GetMapping("/{object}")
    public void getObject(@PathVariable("object") String object, HttpServletResponse response) {

        try {
            InputStream inputStream = minioService.getFile(Path.of(object));

            response.addHeader("Content-disposition", "attachment;filename=" + object);
            response.setContentType(URLConnection.guessContentTypeFromName(object));

            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @GetMapping("/{object}/dlw")
    public void getObjectDLW(@PathVariable("object") String object, HttpServletResponse response) {
        try{
            minioService.getAndSave(object);
        }catch (Exception e){
            System.out.println(e);
        }
    }


    @DeleteMapping("/{object}")
    public void removeObject(@PathVariable("object") String object, HttpServletResponse response) {
        try{
            minioService.remove(object);
        }catch (Exception e){
            System.out.println(e);
        }
    }


    // Save file
        // TTL - new Policy // set Header
    // Return file -


    // List all files the bucket


}