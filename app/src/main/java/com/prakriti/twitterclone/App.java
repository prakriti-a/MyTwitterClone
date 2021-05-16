package com.prakriti.twitterclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("lGAzo2C2k3QBY3h5VCUD768aiXUds0oLZQtd6xYY")
                .clientKey("EZd4uNrW75KlLmkIfu2t0Hrwy4b2Mngr2sGtNMWE")
                .server("https://parseapi.back4app.com/")
                .build());
    }
}
