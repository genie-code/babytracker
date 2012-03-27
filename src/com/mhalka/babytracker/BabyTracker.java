package com.mhalka.babytracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class BabyTracker extends Activity {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	
	// Namjesti varijable
	private TextView StarostBebe;
	private String VasaBeba;
	private String Mjesec;
	private String PrekoGodine;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babytracker);
        
        StarostBebe = (TextView) findViewById(R.id.txtStarostBebe);
        VasaBeba = this.getString(R.string.vasa_beba);
        Mjesec = this.getString(R.string.mjesec);
        PrekoGodine = this.getString(R.string.vise_od_godine);
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar today = Calendar.getInstance();
        
        Calendar datum = (Calendar) datumPocetkaPracenja.clone();
        long monthsBetween = 0;
        while (datum.before(today)) {
        	datum.add(Calendar.MONTH, 1);
        	monthsBetween++;
        	}
        
        int months = (int) monthsBetween;
        
        StarostBebe.setText(VasaBeba + " " + months + "." + " " + Mjesec);
        
        if(months > 12) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(PrekoGodine);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			finish();
        		}
        	});
        	alertbox.show();
        }
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
