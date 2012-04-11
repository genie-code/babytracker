package com.mhalka.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String FIRSTRUN = "PrvoPokretanje";
	public static final String TRUDNOCA = "PracenjeTrudnoce";
	
	// Class variables set for SplashScreen
	protected boolean _active = true;
    protected int _splashTime = 3000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// Setiraj Holo Light temu za Android 3 i vecu verziju.
    	if(android.os.Build.VERSION.SDK_INT >= 11) {
    		setTheme(android.R.style.Theme_Holo_Light);
    	}
    	
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash);
	    
	    // Thread for displaying the SplashScreen
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                	// Procitaj preference
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    
                    // Provjeri da li se aplikacija pokrece prvi put i otvori formu shodno tome.
                    Boolean prvoPokretanje = settings.getBoolean(FIRSTRUN, true);
                    if(prvoPokretanje) {
                    	finish();
                    	Intent podesavanja = new Intent(SplashScreen.this, SettingsActivity.class);
                    	startActivityForResult(podesavanja, 0);
                    	finish();
                    	} else {
                    		Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
                    		if(pracenjeTrudnoce) {
                    			Intent intent = new Intent(SplashScreen.this, PregTracker.class);
                    			startActivityForResult(intent, 0);
                    			finish();
                    			} else {
                    				Intent intent = new Intent(SplashScreen.this, BabyTracker.class);
                    				startActivityForResult(intent, 0);
                    				finish();
                    			}
                    	}
                 }
            }
        };
        splashThread.start();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }
}
