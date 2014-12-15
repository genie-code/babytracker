package com.mhalka.babytracker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    // Namjesti konstante za preference.
    private static final String PREFS_NAME = "BabyTrackerPrefs";
    private static final String FIRSTRUN = "PrvoPokretanje";
    private static final String TRUDNOCA = "PracenjeTrudnoce";
    private static final String NOTIFIKACIJA = "Notifikacija";
    private static final String DAN = "DanPocetkaPracenja";
    private static final String MJESEC = "MjesecPocetkaPracenja";
    private static final String GODINA = "GodinaPocetkaPracenja";

    // Setiraj varijable za elemente forme.
    private RadioButton PracenjeTrudnoce;
    private RadioButton PracenjeRazvoja;
    private TextView TerminPoroda;
    private TextView DatumRodjenja;
    private CheckBox Notifikacija;
    private DatePicker DatumPocetkaPracenja;

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("NewApi")
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


        // Procitaj preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // Provjeri da li postoje ranije unesene preference i populariziraj formu sa
        // postojecim vrijednostima.
        Boolean unesenePreference = settings.contains(TRUDNOCA);

        if (unesenePreference) {
            Boolean VrstaPracenja = settings.getBoolean(TRUDNOCA, true);
            Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
            Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);

            if (VrstaPracenja) {
                PracenjeTrudnoce.setChecked(true);
                PracenjeRazvoja.setChecked(false);
            } else {
                PracenjeTrudnoce.setChecked(false);
                PracenjeRazvoja.setChecked(true);
            }

            DatumPocetkaPracenja.init(settings.getInt(GODINA, 1920), settings.getInt(MJESEC, 0), settings.getInt(DAN, 1), null);

            if (NotifikacijaUkljucena) {
                Notifikacija.setChecked(true);
            } else {
                Notifikacija.setChecked(false);
            }

            if (pracenjeTrudnoce) {
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
                if (PracenjeTrudnoce.isChecked()) {
                    DatumRodjenja.setVisibility(View.GONE);
                    TerminPoroda.setVisibility(View.VISIBLE);
                }

            }
        });

        PracenjeRazvoja.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (PracenjeRazvoja.isChecked()) {
                    TerminPoroda.setVisibility(View.GONE);
                    DatumRodjenja.setVisibility(View.VISIBLE);
                }

            }
        });

        // Namjesti ActionBar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.meni_podesavanja);
        }
    }

    @Override
    public void onBackPressed() {
        cancelAndExit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelAndExit();
                return true;
            case R.id.spasi:
                saveAndExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Spasi podesavanja i zatvori activity
    private void saveAndExit() {
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
        editor.apply();

        // Otvori novi Activity shodno odabiru vrste pracenja.
        Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
        if (pracenjeTrudnoce) {
            Intent intent = new Intent(SettingsActivity.this, PregTracker.class);
            startActivityForResult(intent, 0);
            finish();
        } else {
            Intent intent = new Intent(SettingsActivity.this, BabyTracker.class);
            startActivityForResult(intent, 0);
            finish();
        }
    }

    // Zatvori activity bez spasavanja
    private void cancelAndExit() {
        finish();

        // Otvori novi Activity shodno odabiru vrste pracenja, ako je odabir prethodno izvrsen.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean unesenePreference = settings.contains(TRUDNOCA);

        if (unesenePreference) {
            Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);

            if (pracenjeTrudnoce) {
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
}
