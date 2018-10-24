package com.mhalka.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LoadActivity extends Activity {

    // Namjesti konstante za preference.
    private static final String PREFS_NAME = "BabyTrackerPrefs";
    private static final String FIRSTRUN = "PrvoPokretanje";
    private static final String TRUDNOCA = "PracenjeTrudnoce";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // Provjeri da li se aplikacija pokrece prvi put i otvori formu shodno tome.
        Boolean prvoPokretanje = settings.getBoolean(FIRSTRUN, true);
        if (prvoPokretanje) {
            finish();
            Intent podesavanja = new Intent(LoadActivity.this, SettingsActivity.class);
            startActivityForResult(podesavanja, 0);
            finish();
        } else {
            Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
            if (pracenjeTrudnoce) {
                Intent intent = new Intent(LoadActivity.this, PregTracker.class);
                startActivityForResult(intent, 0);
                finish();
            } else {
                Intent intent = new Intent(LoadActivity.this, BabyTracker.class);
                startActivityForResult(intent, 0);
                finish();
            }
        }
    }
}
