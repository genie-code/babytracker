package com.mhalka.babytracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
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
	public static final String NOTIFIKACIJA = "Notifikacija";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	public static final String MJESECI = "TrenutnaStarostBebe";
	
	// Setiraj varijable za elemente forme.
	private TextView StarostBebe;
	private String VasaBeba;
	private String Mjesec;
	private String NerealnaVrijednost;
	private String PrekoGodine;
	private AlarmManager am;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babytracker);
        
        // Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
        StarostBebe = (TextView) findViewById(R.id.txtStarostBebe);
        VasaBeba = this.getString(R.string.vasa_beba);
        Mjesec = this.getString(R.string.mjesec);
        NerealnaVrijednost = this.getString(R.string.nerealna_vrijednost);
        PrekoGodine = this.getString(R.string.vise_od_godine);
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
        
        // Izracunaj starost bebe u mjesecima.
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar today = Calendar.getInstance();
        
        Calendar datum = (Calendar) datumPocetkaPracenja.clone();
        long monthsBetween = 0;
        while (datum.before(today)) {
        	datum.add(Calendar.MONTH, 1);
        	monthsBetween++;
        	}
        
        int months = (int) monthsBetween;
        
        // Populariziraj TextBox sa izracunatom vrijednoscu.
        StarostBebe.setText(VasaBeba + " " + months + "." + " " + Mjesec);
        
        // Provjeri da izracunata vrijednost nije negativna.
        if(months < 1) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(NerealnaVrijednost);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			Intent podesavanja = new Intent(BabyTracker.this, SettingsActivity.class);
                	podesavanja.putExtra("BezProvjere", "nema");
     				startActivityForResult(podesavanja, 0);
     				finish();
        		}
        	});
        	alertbox.show();
        }
        
        // Ako izracunata vrijednost premasuje dozvoljenu granicu izbaci upozorenje i
        // zatvori aplikaciju.
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
        
        // Zapisi izracunatu vrijednost ako je ukljucena notifikacija i okini alarm.
        if(NotifikacijaUkljucena == true) {
        	
        	// Zapisi trenutnu vrijednost u preference radi koristenja kasnije
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putInt(MJESECI, months);
        	editor.commit();
        	
        	am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        	Intent intent = new Intent(this, AlarmReceiver.class);
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
        			intent, PendingIntent.FLAG_CANCEL_CURRENT);
        	am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
        			(5 * 1000), pendingIntent);
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
        // Opcije menija.
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
