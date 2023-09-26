package com.hysteryale.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
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

    /**
     * Extract month and year from file Name. ex "01. Bookings Register - Apr -2023 (Jason).xlsx" =>
     * @param fileName
     * @return
     */
    public static Calendar extractMonthAndYearFromFileName(String fileName) {
        //because the file names are not well-defined therefore we need to do some extra steps

        //try to remove all space first
        fileName = fileName.replaceAll("\\s", "");

        //try to remove all special characters
        fileName = fileName.replaceAll("[^a-zA-Z0-9]", " ");

        int year = Integer.parseInt(fileName.replaceAll("[^0-9]", ""));
        int month =

        return
    }

    private Calend

}
