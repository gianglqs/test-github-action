package com.hysteryale.rollbar;

import com.rollbar.notifier.Rollbar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@Configuration
public class RollbarInitializer {

//    @Autowired
//   private Dotenv env;
    protected static Rollbar rollbar;

    @Value("${rollbar.secret-key}")
    private String SECRET_KEY;

    @Value("${rollbar.environment}")
    private String ENV;

    @Value("${rollbar.code-version}")
    private String CODE_VERSION;


    @PostConstruct
    public void init() {

      //  rollbar = Rollbar.init(withAccessToken(env.get("HYSTERYALE_ROLLBAR_SECRET_KEY"))
        rollbar = Rollbar.init(withAccessToken(SECRET_KEY)
                .environment(ENV)
                .codeVersion(CODE_VERSION)
                .build());
    }
}
