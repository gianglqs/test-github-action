package com.hysteryale.service;

import com.hysteryale.rollbar.RollbarInitializer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BasedService extends RollbarInitializer {

    /**
     *  To log info to console and rollbar
     * @param message
     * @param exceptions
     */
    protected void logInfo(String message, Exception... exceptions){
        log.info(message, exceptions);
        rollbar.info(message);
    }

    protected void logDebug(String message, Exception... exceptions){
        log.debug(message, exceptions);
        rollbar.debug(message);
    }

    protected void logError(String message, Exception... exceptions){
        log.error(message, exceptions);
        rollbar.error(message);

    }

    protected void logWarning(String message, Exception... exceptions){
        log.warn(message, exceptions);
        rollbar.warning(message);

    }

}
