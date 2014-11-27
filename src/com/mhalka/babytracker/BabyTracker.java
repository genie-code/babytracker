package com.mhalka.babytracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private LinearLayout BebaLayout;
	private TextView IntroPodaciBeba;
	private TextView PodaciBeba;
	private TextView actionBarTitle;
	private TextView actionBarSubtitle;
	private ImageView SlikaBeba;
	private String VasaBeba;
	private String Mjesec;
	private String NerealnaVrijednost;
	private String NapunjenaGodina;
	private String PrekoGodine;
	private AlarmManager am;
	
    /** Called when the activity is first created. */
	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.babytracker);
        
        // Zabiljezi broj otvaranja aplikacije i pokazi rate dijalog ako su uslovi ispunjeni
        AppRater.appLaunched(this);
        
        // Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
        BebaLayout = (LinearLayout) findViewById(R.id.llBabyTracker);
        PodaciBeba = (TextView) findViewById(R.id.txtPodaciBeba);
        IntroPodaciBeba = (TextView) findViewById(R.id.txtIntroPodaciBeba);
        SlikaBeba = (ImageView) findViewById(R.id.ivSlikaBeba);
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
        
        // Izracunaj starost bebe u mjesecima.
        Calendar datum = (Calendar) datumPocetkaPracenja.clone();
        long monthsBetween = 0;
        while (datum.before(today)) {
        	today.add(Calendar.MONTH, -1);
        	monthsBetween++;
        }
        
        int months = (int) monthsBetween;
        
        /** Ponovo dobavi datum pocetka pracenja i danasnji datum, jer, iz nekog razloga, donji if
         *  statement ne moze da koristi prethodno dobavljene vrijednosti. */
        Calendar pocetak = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar danas = Calendar.getInstance();
        
        /** Provjeri da li je izracunata vrijednost 12 i da li datum rodjenja (mjesec i dan) odgovaraju
         * danasnjem datumu i shodno tome pokazi alert dijalog da je beba napunila godinu dana. */
        if((months == 12) && ((pocetak.get(Calendar.YEAR)) < (danas.get(Calendar.YEAR))) &&
        		((pocetak.get(Calendar.MONTH)) == (danas.get(Calendar.MONTH))) &&
        		((pocetak.get(Calendar.DAY_OF_MONTH)) == (danas.get(Calendar.DAY_OF_MONTH)))) {
        	
        	BebaLayout.setVisibility(View.INVISIBLE);
        	
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(NapunjenaGodina);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			finish();
        		}
        	});
        	alertbox.show();
        }
        
        // Provjeri da izracunata vrijednost nije negativna.
        else if(months < 1) {
        	
        	BebaLayout.setVisibility(View.INVISIBLE);
        	
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(NerealnaVrijednost);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			Intent podesavanja = new Intent(BabyTracker.this, SettingsActivity.class);
        			startActivityForResult(podesavanja, 0);
     				finish();
        		}
        	});
        	alertbox.show();
        }
        
        // Provjeri da li je izracunata vrijednost veca od 12 i shodno tome pokazi odgovarajuci alert.
        else if(months > 12) {
        	
        	BebaLayout.setVisibility(View.INVISIBLE);
        	
        	/** Provjeri da li datum rodjenja (mjesec i dan) odgovaraju danasnjem datumu i shodno tome
        	 *  pokazi alert dijalog da je beba napunila godinu dana. */
            if(((pocetak.get(Calendar.YEAR)) < (danas.get(Calendar.YEAR))) &&
            		((pocetak.get(Calendar.MONTH)) == (danas.get(Calendar.MONTH))) &&
            		((pocetak.get(Calendar.DAY_OF_MONTH)) == (danas.get(Calendar.DAY_OF_MONTH)))) {
            	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            	alertbox.setMessage(NapunjenaGodina);
            	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface arg0, int arg1) {
            			finish();
            		}
            	});
            	alertbox.show();
            } else {
            	
            	/** Ako izracunata vrijednost premasuje dozvoljenu granicu izbaci upozorenje i
            	 *  vrati korisnika na settings activity. */
            	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            	alertbox.setMessage(PrekoGodine);
            	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface arg0, int arg1) {
            			Intent podesavanja = new Intent(BabyTracker.this, SettingsActivity.class);
            			startActivityForResult(podesavanja, 0);
            			finish();
            		}
            	});
            	alertbox.show();
            }
        }
        
        // Ako je izracunata vrijednost u dozvoljenim granicama nastavi dalje
        else {
        	
        	// Zapisi izracunatu vrijednost ako je ukljucena notifikacija i okini alarm.
        	if(NotifikacijaUkljucena) {
        		
        		// Zapisi trenutnu vrijednost u preference radi koristenja kasnije
        		SharedPreferences.Editor editor = settings.edit();
        		editor.putInt(MJESECI, months);
        		editor.commit();
        		
        		// Namjesti vrijeme za alarm i okidanje notifikacije
        		Calendar calendar = Calendar.getInstance();
        		calendar.set(Calendar.HOUR_OF_DAY, 10);
        		calendar.set(Calendar.MINUTE, 00);
        		calendar.set(Calendar.SECOND, 00);
        		
        		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        		Intent intent = new Intent(this, AlarmReceiver.class);
        		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
        				intent, PendingIntent.FLAG_CANCEL_CURRENT);
        		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        				(24 * 60 * 60 * 1000), pendingIntent);
        	}
        	
        	// Namjesti ActionBar
        	ActionBar actionBar = getActionBar();
    		actionBar.setDisplayShowHomeEnabled(true);
    		actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            
            LayoutInflater inflater = LayoutInflater.from(this);
            View actionBarView = inflater.inflate(R.layout.actionbar, BebaLayout, false);
            
            actionBarTitle = (TextView) actionBarView.findViewById(R.id.abTitle);
            actionBarSubtitle = (TextView) actionBarView.findViewById(R.id.abSubtitle);
            
            actionBarTitle.setText(VasaBeba + " " + months + "." + " " + Mjesec);
            actionBarSubtitle.setVisibility(View.GONE);
    		
    		actionBar.setCustomView(actionBarView);
        	
        	// Setiraj array sa resource ID-jevima za slike.
        	int slike[] = { R.drawable.mjesec01, R.drawable.mjesec02, R.drawable.mjesec03,
        			R.drawable.mjesec04, R.drawable.mjesec05, R.drawable.mjesec06, R.drawable.mjesec07,
        			R.drawable.mjesec08, R.drawable.mjesec09, R.drawable.mjesec10, R.drawable.mjesec11,
        			R.drawable.mjesec12 };
        	
        	// Setiraj resource ID shodno izracunatom mjesecu u kojem se beba trenutno nalazi.
        	int resIdSlike = slike[months - 1];
        	
        	// Populariziraj ImageView sa odgovarajucom slikom.
        	SlikaBeba.setImageResource(resIdSlike);
        	
        	// Setiraj array sa vrijednostima za podatke o razvoju.
        	int podaci[] = { R.raw.mjesec01, R.raw.mjesec02, R.raw.mjesec03, R.raw.mjesec04, R.raw.mjesec05,
        			R.raw.mjesec06, R.raw.mjesec07, R.raw.mjesec08, R.raw.mjesec09, R.raw.mjesec10,
        			R.raw.mjesec11, R.raw.mjesec12 };
        	
        	// Setiraj resource ID shodno izracunatom mjesecu u kojem se beba trenutno nalazi.
        	int resIdPodaci = podaci[months - 1];
        	
        	/** Dobavi odgovarajuci text file, parsiraj ga i sa njegovim sadrzajem populariziraj
        	 *  TextView u kojem treba da se nalaze podaci. */
        	InputStream inputStream = this.getResources().openRawResource(resIdPodaci);
        	InputStreamReader inputreader = new InputStreamReader(inputStream);
        	BufferedReader buffreader = new BufferedReader(inputreader);
        	String line = null;
        	int numLines = 2;
        	int lineCtr = 0;
        	StringBuilder introtext = new StringBuilder();
        	try {
        		while ((line = buffreader.readLine()) != null) {
        			if (lineCtr == numLines) {
    				    break;
    				}
        			lineCtr++;
        			introtext.append(line);
        			introtext.append('\n');
        		}
        	} catch (IOException e) {
        			return;
        		}
        	IntroPodaciBeba.setText(introtext.toString());
        	
        	StringBuilder text = new StringBuilder();
        	try {
        		while ((line = buffreader.readLine()) != null) {
        			if (lineCtr >= numLines) {
        				lineCtr++;
            			text.append(line);
            			text.append('\n');
            		}
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
            	startActivityForResult(podesavanja, 0);
 				finish();
                return true;
            case R.id.rateapp:
                AppRater.rateApp(this);
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
