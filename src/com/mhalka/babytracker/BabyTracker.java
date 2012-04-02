package com.mhalka.babytracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private TextView PodaciBeba;
	private String VasaBeba;
	private String Mjesec;
	private String NerealnaVrijednost;
	private String NapunjenaGodina;
	private String PrekoGodine;
	private AlarmManager am;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babytracker);
        
        // Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
        StarostBebe = (TextView) findViewById(R.id.txtStarostBebe);
        PodaciBeba = (TextView) findViewById(R.id.txtPodaciBeba);
        VasaBeba = this.getString(R.string.vasa_beba);
        Mjesec = this.getString(R.string.mjesec);
        NerealnaVrijednost = this.getString(R.string.nerealna_vrijednost);
        NapunjenaGodina = this.getString(R.string.napunjena_godina);
        PrekoGodine = this.getString(R.string.vise_od_godine);
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
        
        // Dobavi datum pocetka pracenja i danasnji datum.
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar today = Calendar.getInstance();
        
        // Provjeri da li datum rodjenja (mjesec i dan) odgovaraju danasnjem datumu i shodno tome
        // pokazi alert dijalog da je beba napunila godinu dana.
        if(((datumPocetkaPracenja.get(Calendar.YEAR)) < (today.get(Calendar.YEAR))) &&
        		((datumPocetkaPracenja.get(Calendar.MONTH)) == (today.get(Calendar.MONTH))) &&
        		((datumPocetkaPracenja.get(Calendar.DAY_OF_MONTH)) == (today.get(Calendar.DAY_OF_MONTH)))) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(NapunjenaGodina);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			finish();
        		}
        	});
        	alertbox.show();
        }
        
        // Izracunaj starost bebe u mjesecima.
        Calendar datum = (Calendar) datumPocetkaPracenja.clone();
        long monthsBetween = 0;
        while (datum.before(today)) {
        	today.add(Calendar.MONTH, -1);
        	monthsBetween++;
        	}
        
        int months = (int) monthsBetween;
        
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
        else if(months > 12) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(PrekoGodine);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			finish();
        		}
        	});
        	alertbox.show();
        }
        
        // Ako je izracunata vrijednost u dozvoljenim granicama nastavi dalje
        else {
        	// Zapisi izracunatu vrijednost ako je ukljucena notifikacija i okini alarm.
        	if(NotifikacijaUkljucena) {
        		
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
        	
        	// Populariziraj TextView sa izracunatom vrijednoscu.
        	StarostBebe.setText(VasaBeba + " " + months + "." + " " + Mjesec);
        	
        	// Setiraj array sa vrijednostima za podatke o razvoju.
        	int num[] = { R.raw.mjesec01, R.raw.mjesec02, R.raw.mjesec03, R.raw.mjesec04, R.raw.mjesec05,
        			R.raw.mjesec06, R.raw.mjesec07, R.raw.mjesec08, R.raw.mjesec09, R.raw.mjesec10,
        			R.raw.mjesec11, R.raw.mjesec12 };
        	
        	// Setiraj resource ID shodno izracunatom mjesecu u kojem se beba trenutno nalazi.
        	int resId = num[months - 1];
        	
        	// Dobavi odgovarajuci text file, parsiraj ga i sa njegovim sadrzajem populariziraj
        	// TextView u kojem treba da se nalaze podaci.
        	InputStream inputStream = this.getResources().openRawResource(resId);
        	InputStreamReader inputreader = new InputStreamReader(inputStream);
        	BufferedReader buffreader = new BufferedReader(inputreader);
        	String line;
        	StringBuilder text = new StringBuilder();
        	try {
        		while ((line = buffreader.readLine()) != null) {
        			text.append(line);
        			text.append('\n');
        			}
        		} catch (IOException e) {
        			return;
        			}
        	PodaciBeba.setText(text.toString());
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
