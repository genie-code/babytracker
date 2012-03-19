package com.mhalka.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	
	private RadioButton PracenjeTrudnoce;
	private RadioButton PracenjeRazvoja;
	private TextView TerminPoroda;
	private TextView DatumRodjenja;
	private Button Spasi;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        
        PracenjeTrudnoce = (RadioButton) findViewById(R.id.rbPracenjeTrudnoce);
        PracenjeRazvoja = (RadioButton) findViewById(R.id.rbPracenjeRazvoja);
        TerminPoroda = (TextView) findViewById(R.id.txtTerminPoroda);
        DatumRodjenja = (TextView) findViewById(R.id.txtDatumRodjenja);
        Spasi = (Button) findViewById(R.id.btnSpasi);
        
        
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
				Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
				startActivityForResult(intent, 0);
				finish();
				
			}
		});
	}
}