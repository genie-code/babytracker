package com.mhalka.babytracker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PregTracker extends Activity {

    // Namjesti konstante za preference.
    private static final String PREFS_NAME = "BabyTrackerPrefs";
    private static final String NOTIFIKACIJA = "Notifikacija";
    private static final String DAN = "DanPocetkaPracenja";
    private static final String MJESEC = "MjesecPocetkaPracenja";
    private static final String GODINA = "GodinaPocetkaPracenja";
    private static final String SEDMICA = "TrenutnaSedmicaTrudnoce";
    private static final String BATTERY_OPTIMIZATIONS = "BatteryOptimizations";

    private String shareVrijeme;
    private String NovoPracenje;
    private String DugmeYes;
    private String DugmeNo;

    /**
     * Called when the activity is first created.
     */
    @SuppressWarnings("ConstantConditions")
    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pregtracker);

        // Zabiljezi broj otvaranja aplikacije i pokazi rate dijalog ako su uslovi ispunjeni
        AppRater.appLaunched(this);

        // Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
        LinearLayout pregLayout = findViewById(R.id.llPregTracker);
        TextView introPodaciPlod = findViewById(R.id.txtIntroPodaciPlod);
        TextView podaciPlod = findViewById(R.id.txtPodaciPlod);
        ImageView slikaPlod = findViewById(R.id.ivSlikaPlod);
        String vasaTrudnoca = this.getString(R.string.vasa_trudnoca);
        String sedmica = this.getString(R.string.sedmica);
        String punaSedmica = this.getString(R.string.pune_sedmice);
        String puniDan = this.getString(R.string.puni_dani);
        String nerealnaVrijednost = this.getString(R.string.nerealna_vrijednost);
        String prekoTermina = this.getString(R.string.preko_termina);
        NovoPracenje = this.getString(R.string.namjesti_novo_pracenje);
        DugmeYes = this.getString(R.string.dugme_yes);
        DugmeNo = this.getString(R.string.dugme_no);

        // Procitaj preference.
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);

        // Dobavi datum pocetka pracenja i danasnji datum.
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA, 1920), settings.getInt(MJESEC, 0), settings.getInt(DAN, 1));
        Calendar today = Calendar.getInstance();

        // Izracunaj starost ploda u sedmicama.
        Calendar datum = (Calendar) datumPocetkaPracenja.clone();
        long weeksBetween = 0;

        // Racunaj starost ploda u odnosu na optimalni broj sedmica trajanja trudnoce
        while (today.before(datum)) {
            today.add(Calendar.DAY_OF_MONTH, 7);
            weeksBetween++;
        }
        // Namjesti varijablu za optimalan broj sedmica trudnoce
        int weeksopt = 41 - ((int) weeksBetween);

        // Racunaj starost ploda u odnosu na maksimalni broj sedmica trajanja trudnoce
        while (today.after(datum)) {
            datum.add(Calendar.DAY_OF_MONTH, 7);
            weeksBetween++;
        }
        // Namjesti varijablu za produzeni broj sedmica trudnoce
        int weeksexp = 40 + ((int) weeksBetween);

        // Izracunaj starost ploda u danima.
        // Dobavi datum pocetka pracenja i danasnji datum.
        Calendar endDate = new GregorianCalendar(settings.getInt(GODINA, 1920), settings.getInt(MJESEC, 0), settings.getInt(DAN, 1));
        Calendar startDate = Calendar.getInstance();

        // Izracunaj starost ploda u danima za optimalni broj dana
        Calendar date = (Calendar) endDate.clone();
        long daysBetween = 0;
        while (startDate.before(date)) {
            startDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        // Namjesti varijablu za optimalan broj dana starosti ploda
        int daysopt;
        if (((int) daysBetween % 7) != 0) {
            daysopt = 7 - ((int) daysBetween % 7);
        } else {
            daysopt = ((int) daysBetween % 7);
        }

        // Namjesti varijablu koja odbrojava dane unazad
        int pregdaysopt = 281 - ((int) daysBetween);

        // Racunaj starost ploda u odnosu na maksimalni broj dana trajanja trudnoce
        while (startDate.after(date)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        // Namjesti varijablu za produzeni broj dana starosti ploda
        int daysexp;
        if ((int) daysBetween < 8) {
            daysexp = ((int) daysBetween - 1);
        } else if ((int) daysBetween == 8) {
            daysexp = 0;
        } else {
            daysexp = (((int) daysBetween - 1) % 7);
        }

        // Namjesti varijablu za globalni broj sedmica trudnoce
        int weeks;
        if (weeksopt > 40) {
            weeks = weeksexp;
        } else {
            weeks = weeksopt;
        }

        // Namjesti varijablu za tacan broj sedmica starosti ploda
        int exactweeks = weeks - 1;

        // Namjesti varijablu za globalni broj dana
        int days;
        if (pregdaysopt > 280) {
            days = daysexp;
        } else {
            days = daysopt;
        }

        // Provjeri da izracunata vrijednost nije negativna.
        if (weeks < 1) {

            pregLayout.setVisibility(View.INVISIBLE);

            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setMessage(nerealnaVrijednost);
            alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent podesavanja = new Intent(PregTracker.this, SettingsActivity.class);
                    startActivityForResult(podesavanja, 0);
                    finish();
                }
            });
            alertbox.show();
        }

        /* Ako izracunata vrijednost premasuje dozvoljenu granicu izbaci upozorenje, sa mogucnoscu
           odabira nove vrste pracenja ili zatvaranja aplikacije. */
        else if (weeks > 42) {

            pregLayout.setVisibility(View.INVISIBLE);

            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setMessage(prekoTermina);
            alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // Izbaci drugi dijalog sa mogucnoscu odabira nove vrste pracenja.
                    AlertDialog.Builder settracking = new AlertDialog.Builder(PregTracker.this);
                    settracking.setMessage(NovoPracenje);
                    settracking.setPositiveButton(DugmeYes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent podesavanja = new Intent(PregTracker.this, SettingsActivity.class);
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

        // Ako je izracunata vrijednost u dozvoljenim granicama nastavi dalje
        else {

            // Zapisi izracunatu vrijednost ako je ukljucena notifikacija i okini alarm.
            if (NotifikacijaUkljucena) {

                // Zapisi trenutnu vrijednost u preference radi koristenja kasnije
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(SEDMICA, weeks);
                editor.apply();

                // Namjesti vrijeme za alarm i okidanje notifikacije
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 10);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        24 * 60 * 60 * 1000, pendingIntent);
            }

            // Namjesti ActionBar
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowCustomEnabled(true);

                LayoutInflater inflater = LayoutInflater.from(this);
                View actionBarView = inflater.inflate(R.layout.actionbar, pregLayout, false);

                TextView actionBarTitle = actionBarView.findViewById(R.id.abTitle);
                TextView actionBarSubtitle = actionBarView.findViewById(R.id.abSubtitle);

                actionBarTitle.setText(vasaTrudnoca + " " + weeks + "." + " " + sedmica);
                actionBarSubtitle.setText("[ " + punaSedmica + " " + exactweeks
                        + " i " + puniDan + " " + days + " ]");

                actionBar.setCustomView(actionBarView);

                shareVrijeme = actionBarTitle.getText().toString();
            }

            // Setiraj array sa resource ID-jevima za slike.
            int slike[] = {R.drawable.sedmica01, R.drawable.sedmica02, R.drawable.sedmica03,
                    R.drawable.sedmica04, R.drawable.sedmica05, R.drawable.sedmica06,
                    R.drawable.sedmica07, R.drawable.sedmica08, R.drawable.sedmica09,
                    R.drawable.sedmica10, R.drawable.sedmica11, R.drawable.sedmica12,
                    R.drawable.sedmica13, R.drawable.sedmica14, R.drawable.sedmica15,
                    R.drawable.sedmica16, R.drawable.sedmica17, R.drawable.sedmica18,
                    R.drawable.sedmica19, R.drawable.sedmica20, R.drawable.sedmica21,
                    R.drawable.sedmica22, R.drawable.sedmica23, R.drawable.sedmica24,
                    R.drawable.sedmica25, R.drawable.sedmica26, R.drawable.sedmica27,
                    R.drawable.sedmica28, R.drawable.sedmica29, R.drawable.sedmica30,
                    R.drawable.sedmica31, R.drawable.sedmica32, R.drawable.sedmica33,
                    R.drawable.sedmica34, R.drawable.sedmica35, R.drawable.sedmica36,
                    R.drawable.sedmica37, R.drawable.sedmica38, R.drawable.sedmica39,
                    R.drawable.sedmica40, R.drawable.sedmica41, R.drawable.sedmica42};

            // Setiraj resource ID shodno izracunatom mjesecu u kojem se beba trenutno nalazi.
            int resIdSlike = slike[weeks - 1];

            // Populariziraj ImageView sa odgovarajucom slikom.
            slikaPlod.setImageResource(resIdSlike);

            // Setiraj array sa vrijednostima za podatke o trudnoci.
            int podaci[] = {R.raw.sedmica01, R.raw.sedmica02, R.raw.sedmica03, R.raw.sedmica04,
                    R.raw.sedmica05, R.raw.sedmica06, R.raw.sedmica07, R.raw.sedmica08,
                    R.raw.sedmica09, R.raw.sedmica10, R.raw.sedmica11, R.raw.sedmica12,
                    R.raw.sedmica13, R.raw.sedmica14, R.raw.sedmica15, R.raw.sedmica16,
                    R.raw.sedmica17, R.raw.sedmica18, R.raw.sedmica19, R.raw.sedmica20,
                    R.raw.sedmica21, R.raw.sedmica22, R.raw.sedmica23, R.raw.sedmica24,
                    R.raw.sedmica25, R.raw.sedmica26, R.raw.sedmica27, R.raw.sedmica28,
                    R.raw.sedmica29, R.raw.sedmica30, R.raw.sedmica31, R.raw.sedmica32,
                    R.raw.sedmica33, R.raw.sedmica34, R.raw.sedmica35, R.raw.sedmica36,
                    R.raw.sedmica37, R.raw.sedmica38, R.raw.sedmica39, R.raw.sedmica40,
                    R.raw.sedmica41, R.raw.sedmica42};

            // Setiraj resource ID shodno izracunatoj sedmici trudnoce.
            int resIdPodaci = podaci[weeks - 1];

            /* Dobavi odgovarajuci text file, parsiraj ga i sa njegovim sadrzajem populariziraj
               TextView u kojem treba da se nalaze podaci. */
            InputStream inputStream = this.getResources().openRawResource(resIdPodaci);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            int numLines = 2;
            int lineCtr = 0;
            StringBuilder introtext = new StringBuilder();
            try {
                while ((line = buffreader.readLine()) != null) {
                    if (lineCtr == numLines) {
                        break;
                    }
                    lineCtr++;
                    introtext.append(line);
                    introtext.append('\n');
                }
            } catch (IOException e) {
                return;
            }
            introPodaciPlod.setText(introtext.toString());

            StringBuilder text = new StringBuilder();
            try {
                while ((line = buffreader.readLine()) != null) {
                    if (lineCtr >= numLines) {
                        lineCtr++;
                        text.append(line);
                        text.append('\n');
                    }
                }
            } catch (IOException e) {
                return;
            }
            podaciPlod.setText(text.toString());
        }

        // Provjeri da li aplikacija podlijeze optimizaciji baterije
        Boolean batteryOptimizations = settings.getBoolean(BATTERY_OPTIMIZATIONS, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !batteryOptimizations) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            String packageName = getPackageName();

            if(!pm.isIgnoringBatteryOptimizations(packageName)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.optimizacija_baterije)
                        .setMessage(R.string.optimizacija_baterije_poruka)
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancel, null)
                        .setNegativeButton(R.string.ask_no_more, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                settings.edit().putBoolean(BATTERY_OPTIMIZATIONS, true).apply();
                            }
                        })
                        .setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }
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
        // Opcije menija.
        switch (item.getItemId()) {
            case R.id.share:
                podijeliStanje();
                return true;
            case R.id.podesavanja:
                Intent podesavanja = new Intent(this, SettingsActivity.class);
                startActivityForResult(podesavanja, 0);
                finish();
                return true;
            case R.id.rateapp:
                AppRater.rateApp(this);
                return true;
            case R.id.about:
                Intent about = new Intent(this, About.class);
                startActivityForResult(about, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void podijeliStanje() {
        TextView introPodaciPlod = findViewById(R.id.txtIntroPodaciPlod);
        TextView podaciPlod = findViewById(R.id.txtPodaciPlod);

        // Dobavi raspolozive share intente
        List<Intent> targets = new ArrayList<>();
        Intent template = new Intent(Intent.ACTION_SEND);
        template.setType("text/plain");
        List<ResolveInfo> candidates = this.getPackageManager().
                queryIntentActivities(template, 0);

        /* Od svih raspolozivih share opcija ostavi samo nekoliko:

          Ukloni Facebook zbog nemogucnosti dijeljenja obicnog teksta (problem opisan
          na: http://stackoverflow.com/questions/7545254/android-and-facebook-share-intent)

          Ostale rasplozive share intente filtiraj i ostavi samo mail klijente, G+
          i poznatije IM aplikacije.
         */
        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (((!packageName.equals("com.facebook.katana")) &&
                    (candidate.activityInfo.name.toLowerCase().contains("mail"))) ||
                    (packageName.equals("com.google.android.apps.plus")) ||
                    (packageName.equals("com.google.android.talk")) ||
                    (packageName.equals("com.facebook.orca")) ||
                    (packageName.equals("com.viber.voip")) ||
                    (packageName.equals("com.whatsapp")) ||
                    (packageName.equals("com.skype.raider"))) {
                Intent target = new Intent(Intent.ACTION_SEND);
                target.setType("text/plain");
                target.putExtra(Intent.EXTRA_SUBJECT, shareVrijeme);
                String shareBody = introPodaciPlod.getText().toString().trim() + "\n\n" + podaciPlod.getText().toString().trim();
                target.putExtra(Intent.EXTRA_TEXT, shareBody);
                target.setPackage(packageName);
                targets.add(target);
            }
        }
        Intent chooser = Intent.createChooser(targets.remove(0), getString(R.string.share));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[0]));
        startActivity(chooser);
    }
}
