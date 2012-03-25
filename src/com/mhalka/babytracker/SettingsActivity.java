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
	
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String FIRSTRUN = "PrvoPokretanje";
	public static final String TRUDNOCA = "PracenjeTrudnoce";
	public static final String NOTIFIKACIJA = "Notifikacija";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	
	private RadioButton PracenjeTrudnoce;
	private RadioButton PracenjeRazvoja;
	private TextView TerminPoroda;
	private TextView DatumRodjenja;
	private CheckBox Notifikacija;
	private DatePicker DatumPocetkaPracenja;
	private Button Spasi;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        PracenjeTrudnoce = (RadioButton) findViewById(R.id.rbPracenjeTrudnoce);
        PracenjeRazvoja = (RadioButton) findViewById(R.id.rbPracenjeRazvoja);
        TerminPoroda = (TextView) findViewById(R.id.txtTerminPoroda);
        DatumRodjenja = (TextView) findViewById(R.id.txtDatumRodjenja);
        DatumPocetkaPracenja = (DatePicker) findViewById(R.id.dpDatumPocetkaPracenja);
        Notifikacija = (CheckBox) findViewById(R.id.cbNotifikacija);
        Spasi = (Button) findViewById(R.id.btnSpasi);
        
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        // Provjeri da li se aplikacija pokrece prvi put i otvori formu shodno tome.
        Boolean prvoPokretanje = settings.getBoolean(FIRSTRUN, true);
        
        if(prvoPokretanje != true) {
        	if(PracenjeRazvoja.isChecked()) {
				Intent intent = new Intent(SettingsActivity.this, BabyTracker.class);
				startActivityForResult(intent, 0);
				finish();
             } else {
            	Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
 				startActivityForResult(intent, 0);
 				finish();
             }
        } else {
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean(FIRSTRUN, false);
        	editor.commit();
        }
        
        // Provjeri da li postoje ranije unesene preference i populariziraj formu sa
        // postojecim vrijednostima.
        Boolean unesenePreference = settings.contains(TRUDNOCA);
                
        if(unesenePreference) {
          Boolean VrstaPracenja = settings.getBoolean(TRUDNOCA, true);
          Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
          
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
          
          if(PracenjeTrudnoce.isChecked()) {
				DatumRodjenja.setVisibility(View.GONE);
			    TerminPoroda.setVisibility(View.VISIBLE);
			} else {
				TerminPoroda.setVisibility(View.GONE);
			    DatumRodjenja.setVisibility(View.VISIBLE);
			}
        }
        
        PracenjeTrudnoce.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(PracenjeTrudnoce.isChecked()) {
					DatumRodjenja.setVisibility(View.GONE);
				    TerminPoroda.setVisibility(View.VISIBLE);
				}
				
			}
		});
        
        PracenjeRazvoja.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(PracenjeRazvoja.isChecked()){
					TerminPoroda.setVisibility(View.GONE);
				    DatumRodjenja.setVisibility(View.VISIBLE);
				}
				
			}
		});
        
        Spasi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putBoolean(FIRSTRUN, false);
			    editor.putBoolean(TRUDNOCA, PracenjeTrudnoce.isChecked());
                editor.putBoolean(NOTIFIKACIJA, Notifikacija.isChecked());
                editor.putInt(DAN, DatumPocetkaPracenja.getDayOfMonth());
                editor.putInt(MJESEC, DatumPocetkaPracenja.getMonth());
                editor.putInt(GODINA, DatumPocetkaPracenja.getYear());
                  
             if(PracenjeTrudnoce.isChecked()) {
				Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
				startActivityForResult(intent, 0);
				finish();
             } else {
            	Intent intent = new Intent(SettingsActivity.this, BabyTracker.class);
 				startActivityForResult(intent, 0);
 				finish();
             }
                editor.commit();
				
			}
		});
	}
}