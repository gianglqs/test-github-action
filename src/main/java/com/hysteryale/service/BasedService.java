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
import java.util.Optional;


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
            String checksum = new BigInteger(1, hash).toString(16);
            return checksum;
        } catch (Exception e) {
            logError(e.getMessage());
        }
        return null;
    }

    protected boolean isImported(String pathFile) {
        String hashCode = hashFile(pathFile);
        Optional<ImportFileState> importFileStateOptional = importFileStateRepository.findById(hashCode);
        return importFileStateOptional.isPresent();
    }


}
