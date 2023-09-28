package com.hysteryale.rollbar;

// Add your package declaration here, eg. package com.mycompany;
import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;
import com.rollbar.notifier.Rollbar;

public class RollbarInitializer {

    // Within a method's body:
    Rollbar rollbar = Rollbar.init(withAccessToken("0a7156c4fb29499db9ea06fa459eca99")
            .environment("qa")
            .codeVersion("1.0.0")
            .build());

    rollbar.log("Hello, Rollbar");
}
