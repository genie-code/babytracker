package com.mhalka.babytracker;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity {
	
	private TextView VerzijaAplikacije;
	private String VerzijaString;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.about);
	    
	    VerzijaAplikacije = (TextView) findViewById(R.id.txtVerzija);
	    
	    try {
	    	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	    	VerzijaString = "Verzija "+ String.valueOf(pInfo.versionName);
	    } catch (NameNotFoundException e) {
	    		e.printStackTrace();
	    		VerzijaString = "Nepoznata verzija!";
	    }
	    
	    VerzijaAplikacije.setText(VerzijaString);
	}
}
