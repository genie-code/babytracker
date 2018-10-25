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
     * Fix for https://github.com/dmfs/opentasks/issues/383
     * with workaround suggested at https://issuetracker.google.com/issues/36972466#comment14
     */
    private void checkAppReplacingState() {
        if (getResources() == null) {
            Process.killProcess(Process.myPid());
        }
    }
}
