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

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView verzijaAplikacije = (TextView) findViewById(R.id.txtVerzija);
        String verzija = this.getString(R.string.verzija);
        String verzijaNepoznata = this.getString(R.string.verzija_nepoznata);

        String verzijaString;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verzijaString = verzija + " " + String.valueOf(pInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            verzijaString = verzijaNepoznata;
        }

        verzijaAplikacije.setText(verzijaString);

        // Namjesti ActionBar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.meni_about);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Opcije menija.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
