package com.book.file;


import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    //this value annotation should be from spring not from lombok
    @Value("${application.file.output.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
               @Nonnull  MultipartFile sourceFile,
               @Nonnull   Integer userId) {

        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile sourceFile, @Nonnull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath +  separator +fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if(!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Could not create target folder {}", targetFolder.getAbsolutePath());
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        //.uploads/users/1/123456483.jpg
        String targetFilePath = finalUploadPath + separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try{
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved successfully..");
            return targetFilePath;
        }catch (IOException ex){
            log.error("Could not write file {}",targetFilePath);
            ex.printStackTrace();
        }
        return null;
    }

    private String getFileExtension(String fileName) {
          if(fileName == null || fileName.isEmpty()) {
              return null;
          }
          //srk.jpg
          //we need just jpg so we substring it now
          int lastIndex = fileName.lastIndexOf(".");
          if(lastIndex == -1) {
              return null;
          }
          //JPG => jpg
          return fileName.substring(lastIndex + 1).toLowerCase();
    }
}
