package com.mhalka.babytracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class PregTracker extends Activity {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	
    // Namjesti varijable
    private TextView StarostPloda;
    private String VasaBeba;
    private String Sedmica;
    private String Sedmice234;
    private String Sedmice;
    private String PrekoTermina;
    private String NovoPracenje;
    private String DugmeYes;
    private String DugmeNo;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pregtracker);
        
        StarostPloda = (TextView) findViewById(R.id.txtStarostPloda);
        VasaBeba = this.getString(R.string.vasa_beba);
        Sedmica = this.getString(R.string.sedmica_jednina);
        Sedmice234 = this.getString(R.string.sedmica_mnozina234);
        Sedmice = this.getString(R.string.sedmica_mnozina);
        PrekoTermina = this.getString(R.string.preko_termina);
        NovoPracenje = this.getString(R.string.namjesti_novo_pracenje);
        DugmeYes = this.getString(R.string.dugme_yes);
        DugmeNo = this.getString(R.string.dugme_no);
        
        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar today = Calendar.getInstance();
        
        int weeks  = (today.get(Calendar.YEAR) - datumPocetkaPracenja.get(Calendar.YEAR)) * 12 +
        		(today.get(Calendar.MONTH)- datumPocetkaPracenja.get(Calendar.MONTH)) +
        		(today.get(Calendar.DAY_OF_MONTH) >= datumPocetkaPracenja.get(Calendar.DAY_OF_MONTH)? 0: -1);
        
        if(weeks == 1) {
        	StarostPloda.setText(VasaBeba + " " + weeks + " " + Sedmica);
        } else if((weeks > 1) && (weeks < 5)) {
        	StarostPloda.setText(VasaBeba + " " + weeks + " " + Sedmice234);
        } else {
        	StarostPloda.setText(VasaBeba + " " + weeks + " " + Sedmice);
        }
        
        if(weeks > 42) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        	alertbox.setMessage(PrekoTermina);
        	alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			AlertDialog.Builder settracking = new AlertDialog.Builder(PregTracker.this);
                    settracking.setMessage(NovoPracenje);
                    settracking.setPositiveButton(DugmeYes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        	Intent podesavanja = new Intent(PregTracker.this, SettingsActivity.class);
                        	podesavanja.putExtra("BezProvjere", "nema");
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
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.podesavanja:
            	Intent podesavanja = new Intent(this, SettingsActivity.class);
            	podesavanja.putExtra("BezProvjere", "nema");
            	startActivityForResult(podesavanja, 0);
            	finish();
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
