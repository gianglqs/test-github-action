package com.hysteryale.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.poifs.filesystem.FileMagic;

import java.io.*;
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

    /**
     * Verify whether the file's name is Excel file or not
     */
    public static boolean isExcelFile(String filePath) throws IOException {
        //Please note that FE should only accept file with ext is xlsx and xls so BE should only check of it is really an excel file.
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
//        FileInputStream fileInputStream = new FileInputStream(filePath);

        //OLE2 is XLS and OOXML is XLSX
        return (FileMagic.valueOf(bis) == FileMagic.OLE2) || (FileMagic.valueOf(bis) == FileMagic.OOXML);
    }

    public static boolean isExcelFile( InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);

        //OLE2 is XLS and OOXML is XLSX
        return (FileMagic.valueOf(bis) == FileMagic.OLE2) || (FileMagic.valueOf(bis) == FileMagic.OOXML);
    }

    /**
     * Hash the file's name for avoiding SQL Injection using MD5
     */
    public static String hashFileName(String fileName) {
        Pattern pattern = Pattern.compile("(.*).(.*)");
        Matcher matcher = pattern.matcher(fileName);

        String originalFileName = "";
        if(matcher.find()) {
            originalFileName = matcher.group(1);
        }
        return DigestUtils.md5Hex(originalFileName);
    }
}
