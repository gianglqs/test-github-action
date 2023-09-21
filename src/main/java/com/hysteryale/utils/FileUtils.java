package com.hysteryale.utils;

import lombok.extern.slf4j.Slf4j;

import javax.management.RuntimeErrorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileUtils {

    /**
     * Return all file names in a folder
     * @param folderPath
     * @return @{@link List}
     */
    public static List<String> getAllFilesInFoler(String folderPath) {
        File file = new File(folderPath);
        List<String> fileNames = new ArrayList<>();

        if(file.isDirectory() && file.exists()){
            if(file.canRead()){
                for (File f : file.listFiles()) {
                    fileNames.add(file.getName());
                }
            }else{
                log.info(folderPath + " can't be read");
            }
        }else{
            log.info(folderPath + " does not exist or it is not a folder");
        }

        return new ArrayList<>();
    }

}
