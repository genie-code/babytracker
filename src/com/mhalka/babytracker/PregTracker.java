package com.mhalka.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PregTracker extends Activity {
	
	private Button Postavke;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pregtracker);
        
        Postavke = (Button) findViewById(R.id.btnPostavke);
        
        Postavke.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(PregTracker.this, SettingsActivity.class);
 				startActivityForResult(intent, 0);
 				finish();
			}
		});
        
    }
}
