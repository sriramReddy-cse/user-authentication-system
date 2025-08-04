package com.book.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.ToStringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils{


    public static byte[] readFileFromPath(String fileUrl) {

        if(StringUtils.isBlank(fileUrl)) {
             return null;
        }
        try{
            Path filePath = new File(fileUrl).toPath();
            return Files.readAllBytes(filePath);
        }catch (IOException ex) {
            log.warn("The file is not found at given url path {}"+fileUrl);
            ex.printStackTrace();
        }
        return null;
    }
}
