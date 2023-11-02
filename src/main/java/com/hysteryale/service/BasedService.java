package com.hysteryale.service;

import com.hysteryale.repository.ImportFileStateRepository;
import com.hysteryale.rollbar.RollbarInitializer;
import com.hysteryale.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.*;


@Slf4j
public class BasedService extends RollbarInitializer {

    @Resource
    private ImportFileStateRepository importFileStateRepository;

    /**
     * To log info to console and rollbar
     *
     * @param message
     * @param exceptions
     */
    protected void logInfo(String message, Exception... exceptions) {
        log.info(message, exceptions);
        rollbar.info(message);
    }

    protected void logDebug(String message, Exception... exceptions) {
        log.debug(message, exceptions);
        rollbar.debug(message);
    }

    protected void logError(String message, Exception... exceptions) {
        log.error(message, exceptions);
        rollbar.error(message);

    }

    protected void logWarning(String message, Exception... exceptions) {
        log.warn(message, exceptions);
        rollbar.warning(message);

    }

    protected void updateStateImportFile(String pathFile) {
        //Get path of file imported-file.log
        String pathLogFile = EnvironmentUtils.getEnvironmentValue("import-files.imported");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathLogFile, true));
            bw.write(pathFile);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            logError(e.getMessage());
        }
        logInfo("End importing file: '" + pathFile + "'");
    }


    public boolean isImported(String pathFile) {
        //Get path of file imported-file.log
        String pathLogFile = EnvironmentUtils.getEnvironmentValue("import-files.imported");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathLogFile));
            String line = reader.readLine();

            while (line != null) {
                if (line.equals(pathFile))
                    return true;
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            logError(e.getMessage());
        }
        return false;
    }

}
