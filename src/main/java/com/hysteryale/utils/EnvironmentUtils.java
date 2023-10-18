package com.hysteryale.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class EnvironmentUtils implements EnvironmentAware {
    private static Environment env;
    public static String getEnvironmentValue(String propertyKey) {
        return env.getProperty(propertyKey);
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

//    @Bean
//    public Dotenv dotenv() {
//        return Dotenv.configure()
//                .directory("/home/songiang/Phoenix-software/hyster-yale-backend/")
//                .filename("env")
//                .ignoreIfMalformed()
//                .load();
//    }

}
