package com.hysteryale.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    /**
     * Return all file names in a folder
     * @param folderPath
     * @return @{@link List}
     */
    public static List<String> getAllFilesInFolder(String folderPath) {
        File file = new File(folderPath);
        List<String> fileNames = new ArrayList<>();

        if(file.isDirectory() && file.exists()){
            if(file.canRead()){
                for (File f : file.listFiles()) {
                    fileNames.add(f.getName());
                }
            }else{
                log.info(folderPath + " can't be read");
            }
        }else{
            log.info(folderPath + " does not exist or it is not a folder");
        }

        return new ArrayList<>(fileNames);
    }


    /**
     * Return all file names in a folder
     * @param folderPath
     * @param @{@link Pattern pattern}
     * @return @{@link List}
     */
    public static List<String> getAllFilesInFolderWithPattern(String folderPath, Pattern pattern) {
        //Pattern pattern = Pattern.compile("^(01. Bookings Register).*(.xlsx)$");

        List<String> fileList = new ArrayList<String>();
        Matcher matcher;
        try {
            DirectoryStream<Path> folder = Files.newDirectoryStream(Paths.get(folderPath));
            for(Path path : folder) {
                matcher = pattern.matcher(path.getFileName().toString());
                if(matcher.matches())
                    fileList.add(path.getFileName().toString());
                else
                    log.error("Wrong formatted file's name: " + path.getFileName().toString());
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        log.info("File list: " + fileList);
        return fileList;
    }

}
