package com.mhalka.babytracker;

import android.app.Application;
import android.os.Process;

public final class BabyTrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        checkAppReplacingState();
    }

    /*
     * Workaround za framework bug na androidu 5.1: kada se app azurira dodje do padanja.
     * Pronadjeno na: https://issuetracker.google.com/issues/36972466#comment14
     */
    private void checkAppReplacingState() {
        if (getResources() == null) {
            Process.killProcess(Process.myPid());
        }
    }
}
