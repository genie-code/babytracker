package com.mhalka.babytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BabyTracker extends Activity {
	
	private Button Dugme;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babytracker);
        
        Dugme = (Button) findViewById(R.id.btnSettings);
        
        Dugme.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BabyTracker.this, SettingsActivity.class);
 				startActivity(intent);				
			}
		});
    }
}
