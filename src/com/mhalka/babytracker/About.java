package com.mhalka.babytracker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class About extends Activity {
	
	private TextView VerzijaAplikacije;
	private String Verzija;
	private String VerzijaNepoznata;
	private String VerzijaString;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
	    setContentView(R.layout.about);
	    
	    VerzijaAplikacije = (TextView) findViewById(R.id.txtVerzija);
	    Verzija = this.getString(R.string.verzija);
	    VerzijaNepoznata = this.getString(R.string.verzija_nepoznata);
	    
	    try {
	    	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	    	VerzijaString = Verzija + " "+ String.valueOf(pInfo.versionName);
	    } catch (NameNotFoundException e) {
	    		e.printStackTrace();
	    		VerzijaString = VerzijaNepoznata;
	    }
	    
	    VerzijaAplikacije.setText(VerzijaString);
	    
	    // Namjesti ActionBar
    	ActionBar actionBar = getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	actionBar.setTitle(R.string.meni_about);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Opcije menija.
        switch (item.getItemId()) {
            case android.R.id.home:
            	finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
