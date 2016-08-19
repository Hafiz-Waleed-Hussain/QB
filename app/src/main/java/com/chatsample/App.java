package com.chatsample;

import android.app.Application;

import com.quickblox.core.QBSettings;

/**
 * Created by waleed on 17/08/2016.
 */
public class App extends Application {

    private static final String APP_ID = "45542";
    private static final String AUTH_KEY = "8-SEWyMGbFFgkZ4";
    private static final String AUTH_SECRET = "X79wBjmEnfZLhVM";

    @Override
    public void onCreate() {
        super.onCreate();

        QBSettings.getInstance().init(this,APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey("7LEXNaeWx2PVJswYeezy");
    }
}

