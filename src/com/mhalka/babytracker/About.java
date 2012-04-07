package com.mhalka.babytracker;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class About extends Activity {
	
	private TextView VerzijaAplikacije;
	private String Verzija;
	private String VerzijaNepoznata;
	private String VerzijaString;
	private Button OK;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// Setiraj Holo Light temu za Android 3 i vecu verziju.
    	if(android.os.Build.VERSION.SDK_INT >= 11) {
    		setTheme(android.R.style.Theme_Holo_Light);
    	}
    	
    	super.onCreate(savedInstanceState);
	    setContentView(R.layout.about);
	    
	    VerzijaAplikacije = (TextView) findViewById(R.id.txtVerzija);
	    Verzija = this.getString(R.string.verzija);
	    VerzijaNepoznata = this.getString(R.string.verzija_nepoznata);
	    OK = (Button) findViewById(R.id.btnOK);
	    
	    try {
	    	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	    	VerzijaString = Verzija + " "+ String.valueOf(pInfo.versionName);
	    } catch (NameNotFoundException e) {
	    		e.printStackTrace();
	    		VerzijaString = VerzijaNepoznata;
	    }
	    
	    VerzijaAplikacije.setText(VerzijaString);
	    
	    OK.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}
}
