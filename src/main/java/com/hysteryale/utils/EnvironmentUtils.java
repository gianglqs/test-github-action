package com.hysteryale.utils;

import io.github.cdimascio.dotenv.Dotenv;
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


    public static Dotenv dotenv() {
        return Dotenv.configure()
                .directory("/home/malis3-qa/Documents/")
                .filename(".env")
                .ignoreIfMalformed()
                .load();
    }

    static {
        System.setProperty("DATABASE_URL", dotenv().get("DATABASE_URL"));
        System.setProperty("DATABASE_USERNAME", dotenv().get("DATABASE_USERNAME"));
        System.setProperty("DATABASE_PASSWORD", dotenv().get("DATABASE_PASSWORD"));
        System.setProperty("ROLBAR_KEY", dotenv().get("ROLBAR_KEY"));
        System.setProperty("ROLBAR_ENVIROMENT", dotenv().get("ROLBAR_ENVIROMENT"));
        System.setProperty("ROLBAR_CODE_VERSION", dotenv().get("ROLBAR_CODE_VERSION"));

    }

}
