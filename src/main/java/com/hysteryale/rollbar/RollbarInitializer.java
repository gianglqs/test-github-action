package com.hysteryale.rollbar;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;
import com.rollbar.notifier.Rollbar;

public class RollbarInitializer {

    // Within a method's body:
    protected static Rollbar rollbar = Rollbar.init(withAccessToken("628f3874f93944498ace20b499da165c")
                .environment("qa")
                .codeVersion("1.0.0")
                .build());

}
