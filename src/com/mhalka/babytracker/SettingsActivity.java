package com.mhalka.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String FIRSTRUN = "PrvoPokretanje";
	public static final String TRUDNOCA = "PracenjeTrudnoce";
	public static final String NOTIFIKACIJA = "Notifikacija";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	
	// Setiraj varijable za elemente forme.
	private RadioButton PracenjeTrudnoce;
	private RadioButton PracenjeRazvoja;
	private TextView TerminPoroda;
	private TextView DatumRodjenja;
	private CheckBox Notifikacija;
	private DatePicker DatumPocetkaPracenja;
	private Button Spasi;
	private Button Otkazi;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // Povezi prethodno setirane varijable za elemente forme sa ID-jevima u XML file-u.
        PracenjeTrudnoce = (RadioButton) findViewById(R.id.rbPracenjeTrudnoce);
        PracenjeRazvoja = (RadioButton) findViewById(R.id.rbPracenjeRazvoja);
        TerminPoroda = (TextView) findViewById(R.id.txtTerminPoroda);
        DatumRodjenja = (TextView) findViewById(R.id.txtDatumRodjenja);
        DatumPocetkaPracenja = (DatePicker) findViewById(R.id.dpDatumPocetkaPracenja);
        Notifikacija = (CheckBox) findViewById(R.id.cbNotifikacija);
        Spasi = (Button) findViewById(R.id.btnSpasi);
        Otkazi = (Button) findViewById(R.id.btnOtkazi);
        
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        // Provjeri da li se aplikacija pokrece prvi put i otvori formu shodno tome.
        Bundle podesavanjaExtras = getIntent().getExtras();
        if(podesavanjaExtras == null) {
        	Boolean prvoPokretanje = settings.getBoolean(FIRSTRUN, true);
        	if(prvoPokretanje) {
        		SharedPreferences.Editor editor = settings.edit();
        		editor.putBoolean(FIRSTRUN, false);
        		} else {
        			Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
        			if(pracenjeTrudnoce) {
        				Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
        				startActivityForResult(intent, 0);
        				finish();
        				} else {
        					Intent intent = new Intent(SettingsActivity.this, BabyTracker.class);
        					startActivityForResult(intent, 0);
        					finish();
        				}
        		}
        }
        
        // Provjeri da li postoje ranije unesene preference i populariziraj formu sa
        // postojecim vrijednostima.
        Boolean unesenePreference = settings.contains(TRUDNOCA);
                
        if(unesenePreference) {
          Boolean VrstaPracenja = settings.getBoolean(TRUDNOCA, true);
          Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
          Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
          
          if(VrstaPracenja) {
        	  PracenjeTrudnoce.setChecked(true);
        	  PracenjeRazvoja.setChecked(false);
          } else {
        	  PracenjeTrudnoce.setChecked(false);
        	  PracenjeRazvoja.setChecked(true);
          }

          DatumPocetkaPracenja.init(settings.getInt(GODINA,1920),settings.getInt(MJESEC,0),settings.getInt(DAN,1), null);
          
          if(NotifikacijaUkljucena) {
        	  Notifikacija.setChecked(true);
          } else {
        	  Notifikacija.setChecked(false);
          }
          
          if(pracenjeTrudnoce) {
				DatumRodjenja.setVisibility(View.GONE);
			    TerminPoroda.setVisibility(View.VISIBLE);
			} else {
				TerminPoroda.setVisibility(View.GONE);
			    DatumRodjenja.setVisibility(View.VISIBLE);
			}
        }
        
        // Prikazi adekvatan TextBox za odabranu vrstu pracenja.
        PracenjeTrudnoce.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(PracenjeTrudnoce.isChecked()) {
					DatumRodjenja.setVisibility(View.GONE);
				    TerminPoroda.setVisibility(View.VISIBLE);
				}
				
			}
		});
        
        PracenjeRazvoja.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(PracenjeRazvoja.isChecked()){
					TerminPoroda.setVisibility(View.GONE);
				    DatumRodjenja.setVisibility(View.VISIBLE);
				}
				
			}
		});
        
        // OnClick funkcija za dugme za spasavanje podesavanja.
        Spasi.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Dobavi ime file-a gdje su preference i unesi nove vrijednosti.
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putBoolean(FIRSTRUN, false);
			    editor.putBoolean(TRUDNOCA, PracenjeTrudnoce.isChecked());
                editor.putBoolean(NOTIFIKACIJA, Notifikacija.isChecked());
                editor.putInt(DAN, DatumPocetkaPracenja.getDayOfMonth());
                editor.putInt(MJESEC, DatumPocetkaPracenja.getMonth());
                editor.putInt(GODINA, DatumPocetkaPracenja.getYear());
                
                // Zapisi preference.
                editor.commit();
                
                // Otvori novi Activity shodno odabiru vrste pracenja.
                Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
    			if(pracenjeTrudnoce) {
                	Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
                	startActivityForResult(intent, 0);
                	finish();
                	} else {
                		Intent intent = new Intent(SettingsActivity.this, BabyTracker.class);
                		startActivityForResult(intent, 0);
                		finish();
                	}
                }
			});
        
        // OnClick funkcija za dugme za zatvaranje Activity-ja bez cuvanja preferenci.
        Otkazi.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
				
				// Otvori novi Activity shodno odabiru vrste pracenja, ako je odabir prethodno izvrsen.
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				Boolean unesenePreference = settings.contains(TRUDNOCA);
				
				if(unesenePreference) {
					Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
					
					if(pracenjeTrudnoce) {
						Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
						startActivityForResult(intent, 0);
						finish();
						} else {
							Intent intent = new Intent(SettingsActivity.this, BabyTracker.class);
							startActivityForResult(intent, 0);
							finish();
						}
				}
			}
		});
    }
}