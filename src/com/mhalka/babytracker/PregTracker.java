package com.mhalka.babytracker;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class PregTracker extends Activity {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	
    // Namjesti varijable
    private TextView DobDjeteta;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pregtracker);
        
        DobDjeteta = (TextView) findViewById(R.id.txtAge);
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        
        int godine = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        int mjeseci = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
        int dani = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            godine--;
            mjeseci--;
            dani--;
        }
        
        DobDjeteta.setText("Imas " + String.valueOf(godine) + " godina " + String.valueOf(mjeseci) + " mjeseci i "+ String.valueOf(dani) + " dana.");
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.podesavanja:
            	Intent podesavanja = new Intent(this, SettingsActivity.class);
            	podesavanja.putExtra("BezProvjere", "nema");
            	startActivityForResult(podesavanja, 0);
            	finish();
                return true;
            case R.id.about:
            	Intent about = new Intent(this, About.class);
            	startActivityForResult(about, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
