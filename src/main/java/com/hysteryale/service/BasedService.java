package com.hysteryale.service;

import com.hysteryale.model.ImportFileState;
import com.hysteryale.repository.ImportFileStateRepository;
import com.hysteryale.rollbar.RollbarInitializer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        importFileStateRepository.save(new ImportFileState(hashFile(pathFile)));
        logInfo("End importing file: '" + pathFile + "'");
    }

    private String hashFile(String pathFile) {
        Path filePath = Path.of(pathFile);
        try {
            byte[] data = Files.readAllBytes(Paths.get(filePath.toUri()));
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (Exception e) {
            logError(e.getMessage());
        }
        return null;
    }

    protected boolean isImported(String pathFile) {
        String hashCode = hashFile(pathFile);
        assert hashCode != null;
        Optional<ImportFileState> importFileStateOptional = importFileStateRepository.findById(hashCode);
        return importFileStateOptional.isPresent();
    }

    public Date extractDateFromFileName(String fileName) {
        String dateRegex = "\\d{2}_\\d{2}_\\d{4}";
        Matcher m = Pattern.compile(dateRegex).matcher(fileName);
        Date date = null;
        try {
            if (m.find()) {
                date = new SimpleDateFormat("MM_dd_yyyy").parse(m.group());
            } else {
                dateRegex = "\\d{4}";
                date = new Date();
                m = Pattern.compile(dateRegex).matcher(fileName);
                if (m.find()) {
                    String year = m.group();
                    date.setYear(Integer.parseInt(year));
                } else {
                    dateRegex = "\\d{2}";
                    m = Pattern.compile(dateRegex).matcher(fileName);
                    if (m.find()) {
                        String year = m.group();
                        date.setYear(2000 + Integer.parseInt(year));
                    }else{
                        logError("Can not extract Date from File name: " + fileName);
                    }
                }

                String[] monthArr = {"Apr", "Feb", "Jan", "May", "Aug", "Jul", "Jun", "Mar", "Sep", "Oct", "Nov", "Dec"};
                List<String> listMonth = Arrays.asList(monthArr);
                for(String month : listMonth){
                    if (fileName.toLowerCase().contains(month.toLowerCase())) {
                         date.setMonth(listMonth.indexOf(month));
                         break;
                    }
                }

                logError("Can not extract Date from File name: " + fileName);
            }

        } catch (java.text.ParseException e) {
            logError("Can not extract Date from File name: " + fileName);
        }
        return date;
    }




}
