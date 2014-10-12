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
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PregTracker extends Activity {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String NOTIFIKACIJA = "Notifikacija";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	public static final String SEDMICA = "TrenutnaSedmicaTrudnoce";
	public static final String POZADINA = "Pozadina";
	
	// Setiraj varijable za elemente forme.
	private LinearLayout PregLayout;
	private TextView StarostPloda;
	private TextView PuneSedmice;
	private TextView IntroPodaciPlod;
	private TextView PodaciPlod;
	private ImageView SlikaPlod;
	private String VasaTrudnoca;
	private String Sedmica;
	private String PunaSedmica;
	private String PuniDan;
	private String NerealnaVrijednost;
	private String PrekoTermina;
	private String NovoPracenje;
	private String DugmeYes;
	private String DugmeNo;
	private AlarmManager am;
	
    /** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Setiraj Holo Light temu za Android 4 i vecu verziju.
    	if(android.os.Build.VERSION.SDK_INT >= 14) {
    		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
    	}
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.pregtracker);
        
        // Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
        PregLayout = (LinearLayout) findViewById(R.id.llPregTracker);
        StarostPloda = (TextView) findViewById(R.id.txtStarostPloda);
        PuneSedmice = (TextView) findViewById(R.id.txtPuneSedmice);
        IntroPodaciPlod = (TextView) findViewById(R.id.txtIntroPodaciPlod);
        PodaciPlod = (TextView) findViewById(R.id.txtPodaciPlod);
        SlikaPlod = (ImageView) findViewById(R.id.ivSlikaPlod);
        VasaTrudnoca = this.getString(R.string.vasa_trudnoca);
        Sedmica = this.getString(R.string.sedmica);
        PunaSedmica = this.getString(R.string.pune_sedmice);
        PuniDan = this.getString(R.string.puni_dani);
        NerealnaVrijednost = this.getString(R.string.nerealna_vrijednost);
        PrekoTermina = this.getString(R.string.preko_termina);
        NovoPracenje = this.getString(R.string.namjesti_novo_pracenje);
        DugmeYes = this.getString(R.string.dugme_yes);
        DugmeNo = this.getString(R.string.dugme_no);
        
        // Procitaj preference.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
        Integer mPosition = settings.getInt(POZADINA, 2);

        // Namjesti pozadinu prema sacuvanoj vrijednosti
        switch (mPosition) {
        case 0:
        	if (Build.VERSION.SDK_INT >= 16) {
        		PregLayout.setBackground(getResources().getDrawable(R.drawable.bg_blue));
        	} else {
        		PregLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_blue));
        	}
        	break;
        case 1:
        	if (Build.VERSION.SDK_INT >= 16) {
        		PregLayout.setBackground(getResources().getDrawable(R.drawable.bg_pink));
        	} else {
        		PregLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pink));
        	}
        	break;
        case 2:
        	break;
        }
        
        // Dobavi datum pocetka pracenja i danasnji datum.
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar today = Calendar.getInstance();
        
        // Izracunaj starost ploda u sedmicama.
        Calendar datum = (Calendar) datumPocetkaPracenja.clone();
        long weeksBetween = 0;
        
        // Racunaj starost ploda u odnosu na optimalni broj sedmica trajanja trudnoce
        while (today.before(datum)) {
        	today.add(Calendar.DAY_OF_MONTH, 7);
        	weeksBetween++;
        }
        // Namjesti varijablu za optimalan broj sedmica trudnoce
        int weeksopt = 41 - ((int) weeksBetween);
        
        // Racunaj starost ploda u odnosu na maksimalni broj sedmica trajanja trudnoce
        while (today.after(datum)) {
        	datum.add(Calendar.DAY_OF_MONTH, 7);
        	weeksBetween++;
        }
        // Namjesti varijablu za produzeni broj sedmica trudnoce
        int weeksexp = 40 + ((int) weeksBetween);
        
        // Izracunaj starost ploda u danima.
        // Dobavi datum pocetka pracenja i danasnji datum.
        Calendar endDate = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar startDate = Calendar.getInstance();
        
        // Izracunaj starost ploda u danima za optimalni broj dana
        Calendar date = (Calendar) endDate.clone();
        long daysBetween = 0;
        while (startDate.before(date)) {
        	startDate.add(Calendar.DAY_OF_MONTH, 1);
        	daysBetween++;
        }
        // Namjesti varijablu za optimalan broj dana starosti ploda
        int daysopt = 0;
        if (((int) daysBetween % 7) != 0) {
        	daysopt = 7 - ((int) daysBetween % 7);
        } else {
        	daysopt = ((int) daysBetween % 7);
        }
        
        // Namjesti varijablu koja odbrojava dane unazad
        int pregdaysopt = 281 - ((int) daysBetween);
        
        // Racunaj starost ploda u odnosu na maksimalni broj dana trajanja trudnoce
        while (startDate.after(date)) {
        	date.add(Calendar.DAY_OF_MONTH, 1);
        	daysBetween++;
        }
        // Namjesti varijablu za produzeni broj dana starosti ploda
        int daysexp = 0;
        if ((int) daysBetween < 8) {
        	daysexp = ((int) daysBetween - 1);
        } else if ((int) daysBetween == 8) {
        	daysexp = 0;
        } else {
        	daysexp = (((int) daysBetween - 1) % 7);
        }
        
        // Namjesti varijablu za globalni broj sedmica trudnoce
        int weeks = 0;
        if(weeksopt > 40) {
    		weeks = weeksexp;
    	} else {
    		weeks = weeksopt;
    	}
        
        // Namjesti varijablu za tacan broj sedmica starosti ploda
        int exactweeks = weeks - 1;
        
        // Namjesti varijablu za globalni broj dana
        int days = 0;
        if(pregdaysopt > 280) {
        	days = daysexp;
        } else {
        	days = daysopt;
        }
        
        // Provjeri da izracunata vrijednost nije negativna.
        if(weeks < 1) {
        	
        	PregLayout.setVisibility(View.INVISIBLE);
        	
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(NerealnaVrijednost);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			Intent podesavanja = new Intent(PregTracker.this, SettingsActivity.class);
        			startActivityForResult(podesavanja, 0);
     				finish();
        		}
        	});
        	alertbox.show();
        }
        
        /** Ako izracunata vrijednost premasuje dozvoljenu granicu izbaci upozorenje, sa mogucnoscu
         *  odabira nove vrste pracenja ili zatvaranja aplikacije. */
        else if(weeks > 42) {
        	
        	PregLayout.setVisibility(View.INVISIBLE);
        	
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(PrekoTermina);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			// Izbaci drugi dijalog sa mogucnoscu odabira nove vrste pracenja.
        			AlertDialog.Builder settracking = new AlertDialog.Builder(PregTracker.this);
                    settracking.setMessage(NovoPracenje);
                    settracking.setPositiveButton(DugmeYes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        	Intent podesavanja = new Intent(PregTracker.this, SettingsActivity.class);
                        	startActivityForResult(podesavanja, 0);
                        	finish();
                        }
                    });
                    settracking.setNegativeButton(DugmeNo, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
                    settracking.show();
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
        		editor.putInt(SEDMICA, weeks);
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
        	
        	// Namjesti ActionBar za Android 4 i vece verzije.
        	if(android.os.Build.VERSION.SDK_INT >= 14) {
        		ActionBar actionBar = getActionBar();
        		actionBar.setDisplayShowHomeEnabled(true);
        		actionBar.setTitle(R.string.pregtracking);
        	} else {
        		// Namjesti Title prema odabranom nacinu pracenja.
        		setTitle(R.string.pregtracking);
        	} 
        	
        	// Populariziraj TextView sa izracunatom vrijednoscu.
        	StarostPloda.setText(VasaTrudnoca + " " + weeks + "." + " " + Sedmica);
        	// Popuni TextView sa napunjenim brojem sedmica i dana
        	PuneSedmice.setText("[ " + PunaSedmica + " " + exactweeks
        			+ " i " + PuniDan + " " + days + " ]");
        	
        	// Setiraj array sa resource ID-jevima za slike.
        	int slike[] = { R.drawable.sedmica01, R.drawable.sedmica02, R.drawable.sedmica03,
        			R.drawable.sedmica04, R.drawable.sedmica05, R.drawable.sedmica06,
        			R.drawable.sedmica07, R.drawable.sedmica08, R.drawable.sedmica09,
        			R.drawable.sedmica10, R.drawable.sedmica11, R.drawable.sedmica12,
        			R.drawable.sedmica13, R.drawable.sedmica14, R.drawable.sedmica15,
        			R.drawable.sedmica16, R.drawable.sedmica17, R.drawable.sedmica18,
        			R.drawable.sedmica19, R.drawable.sedmica20, R.drawable.sedmica21,
        			R.drawable.sedmica22, R.drawable.sedmica23, R.drawable.sedmica24,
        			R.drawable.sedmica25, R.drawable.sedmica26, R.drawable.sedmica27,
        			R.drawable.sedmica28, R.drawable.sedmica29, R.drawable.sedmica30,
        			R.drawable.sedmica31, R.drawable.sedmica32, R.drawable.sedmica33,
        			R.drawable.sedmica34, R.drawable.sedmica35, R.drawable.sedmica36,
        			R.drawable.sedmica37, R.drawable.sedmica38, R.drawable.sedmica39,
        			R.drawable.sedmica40, R.drawable.sedmica41, R.drawable.sedmica42 };
        	
        	// Setiraj resource ID shodno izracunatom mjesecu u kojem se beba trenutno nalazi.
        	int resIdSlike = slike[weeks - 1];
        	
        	// Populariziraj ImageView sa odgovarajucom slikom.
        	SlikaPlod.setImageResource(resIdSlike);
        	
        	// Setiraj array sa vrijednostima za podatke o trudnoci.
        	int podaci[] = { R.raw.sedmica01, R.raw.sedmica02, R.raw.sedmica03, R.raw.sedmica04,
        			R.raw.sedmica05, R.raw.sedmica06, R.raw.sedmica07, R.raw.sedmica08,
        			R.raw.sedmica09, R.raw.sedmica10, R.raw.sedmica11, R.raw.sedmica12,
        			R.raw.sedmica13, R.raw.sedmica14, R.raw.sedmica15, R.raw.sedmica16,
        			R.raw.sedmica17, R.raw.sedmica18, R.raw.sedmica19, R.raw.sedmica20,
        			R.raw.sedmica21, R.raw.sedmica22, R.raw.sedmica23, R.raw.sedmica24,
        			R.raw.sedmica25, R.raw.sedmica26, R.raw.sedmica27, R.raw.sedmica28,
        			R.raw.sedmica29, R.raw.sedmica30, R.raw.sedmica31, R.raw.sedmica32,
        			R.raw.sedmica33, R.raw.sedmica34, R.raw.sedmica35, R.raw.sedmica36,
        			R.raw.sedmica37, R.raw.sedmica38, R.raw.sedmica39, R.raw.sedmica40,
        			R.raw.sedmica41, R.raw.sedmica42 };
        	
        	// Setiraj resource ID shodno izracunatoj sedmici trudnoce.
        	int resIdPodaci = podaci[weeks - 1];
        	
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
        	IntroPodaciPlod.setText(introtext.toString());
        	
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
        	PodaciPlod.setText(text.toString());
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
            case R.id.background:
                bgChangeDialog();
                return true;
            case R.id.rateapp:
                rateApp();
                return true;
            case R.id.about:
            	Intent about = new Intent(this, About.class);
            	startActivityForResult(about, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void bgChangeDialog() {
    	// Namjesti varijable za preference
    	final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	final SharedPreferences.Editor editor = settings.edit();
    	Integer mPosition = settings.getInt(POZADINA, 2);
    	
    	// Procitaj array vrijednosti za pozadine
    	final CharSequence[] items = getResources().getStringArray(R.array.backgrounds);
    	
    	new AlertDialog.Builder(this)
    	.setTitle(getString(R.string.bg_change_title))
    	.setSingleChoiceItems(items, mPosition, new DialogInterface.OnClickListener() {
    		@SuppressWarnings("deprecation")
    		@SuppressLint("NewApi")
    		public void onClick(DialogInterface dialog, int which) {
    			switch (which) {
    			case 0:
    				if (Build.VERSION.SDK_INT >= 16) {
    					PregLayout.setBackground(getResources().getDrawable(R.drawable.bg_blue));
    				} else {
    					PregLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_blue));
    				}
    				
    				// Zapisi preference
    				editor.putInt(POZADINA, which);
    				editor.commit();
    				
    				break;
    			case 1:
    				if (Build.VERSION.SDK_INT >= 16) {
    					PregLayout.setBackground(getResources().getDrawable(R.drawable.bg_pink));
    				} else {
    					PregLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pink));
    				}
    				
    				// Zapisi preference
    				editor.putInt(POZADINA, which);
    				editor.commit();
    				
    				break;
    			case 2:
    				if (Build.VERSION.SDK_INT >= 16) {
    					PregLayout.setBackground(null);
    				} else {
    					PregLayout.setBackgroundDrawable(null);
    				}
    				
    				// Zapisi preference
    				editor.putInt(POZADINA, which);
    				editor.commit();
    				
    				break;
    			}
    			dialog.dismiss();
    		}
    	})
    	.setCancelable(true)
    	.show();
    }
    
    private boolean tryStartActivity(Intent aIntent) {
    	try
    	{
    		startActivity(aIntent);
    		return true;
    	}
    	catch (ActivityNotFoundException e)
    	{
    		return false;
    	}
    }
    
    public void rateApp() {
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	// Probaj Google Play
    	intent.setData(Uri.parse("market://details?id=com.mhalka.babytracker"));
    	if (!tryStartActivity(intent)) {
    		// Izgleda da Google Play nije instaliran, probaj web browser
    		intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.mhalka.babytracker"));
    		if (!tryStartActivity(intent)) {
    			// Ako nista od prethodnog ne upali informisi korisnika o tome
    			Toast.makeText(this, getString(R.string.no_google_play), Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}
