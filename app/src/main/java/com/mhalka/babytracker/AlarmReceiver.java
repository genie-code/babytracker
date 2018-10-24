package com.mhalka.babytracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    // Namjesti konstante za preference.
    private static final String PREFS_NAME = "BabyTrackerPrefs";
    private static final String TRUDNOCA = "PracenjeTrudnoce";
    private static final String NOTIFIKACIJA = "Notifikacija";
    private static final String DAN = "DanPocetkaPracenja";
    private static final String MJESEC = "MjesecPocetkaPracenja";
    private static final String GODINA = "GodinaPocetkaPracenja";
    private static final String SEDMICA = "TrenutnaSedmicaTrudnoce";
    private static final String MJESECI = "TrenutnaStarostBebe";
    private static final String RODJENDAN = "BebinPrviRodjendan";

    // Konstanta za notifikaciju.
    private static final int NOTIFIKACIJA_ID = 0;

    private String ScrollingText;
    private String NotificationText;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
        ScrollingText = context.getString(R.string.nove_informacije_scrolling);
        NotificationText = context.getString(R.string.nove_informacije_notifikacija);

        // Procitaj preference.
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        Boolean NotifikacijaUkljucena = settings.getBoolean(NOTIFIKACIJA, true);
        Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
        Boolean BebinRodjendan = settings.getBoolean(RODJENDAN, false);
        Integer SedmicaTrudnoce = settings.getInt(SEDMICA, 1);
        Integer StarostBebe = settings.getInt(MJESECI, 1);

        if (NotifikacijaUkljucena) {

            // Setiraj vrijednosti varijabli potrebnih za izracunavanje starosti.
            Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA, 1920), settings.getInt(MJESEC, 0), settings.getInt(DAN, 1));
            Calendar today = Calendar.getInstance();

            if (pracenjeTrudnoce) {
                // Izracunaj starost ploda u sedmicama.
                long weeksBetween = 0;
                // Racunaj starost ploda u odnosu na optimalni broj sedmica trajanja trudnoce
                while (today.before(datumPocetkaPracenja)) {
                    today.add(Calendar.DAY_OF_MONTH, 7);
                    weeksBetween++;
                }
                // Namjesti varijablu za optimalan broj sedmica trudnoce
                int weeksopt = 41 - ((int) weeksBetween);

                // Racunaj starost ploda u odnosu na maksimalni broj sedmica trajanja trudnoce
                while (today.after(datumPocetkaPracenja)) {
                    datumPocetkaPracenja.add(Calendar.DAY_OF_MONTH, 7);
                    weeksBetween++;
                }
                // Namjesti varijablu za produzeni broj sedmica trudnoce
                int weeksexp = 40 + ((int) weeksBetween);

                // Namjesti varijablu za globalni broj sedmica trudnoce
                int weeks;
                if (weeksopt > 40) {
                    weeks = weeksexp;
                } else {
                    weeks = weeksopt;
                }

                // Pokreni notifikaciju ako su ispunjeni svi uslovi.
                if ((weeks != SedmicaTrudnoce) && (weeks > 0) && (weeks < 43)) {
                    startNotifikaciju(context);
                }
            } else {
                // Provjeri da li datum rodjenja (mjesec i dan) odgovaraju danasnjem datumu i shodno
                // tome pokreni notifikaciju.
                if (!BebinRodjendan) {
                    if (((datumPocetkaPracenja.get(Calendar.YEAR)) < (today.get(Calendar.YEAR))) &&
                            ((datumPocetkaPracenja.get(Calendar.MONTH)) == (today.get(Calendar.MONTH))) &&
                            ((datumPocetkaPracenja.get(Calendar.DAY_OF_MONTH)) == (today.get(Calendar.DAY_OF_MONTH)))) {
                        // Zapisi u preference da je pokazana notifikacija za ovaj event
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(RODJENDAN, true);
                        editor.apply();
                        // Pokreni notifikaciju
                        startNotifikaciju(context);
                    }
                }

                // Izracunaj starost bebe u mjesecima.
                long monthsBetween = 0;
                while (datumPocetkaPracenja.before(today)) {
                    today.add(Calendar.MONTH, -1);
                    monthsBetween++;
                }
                int months = (int) monthsBetween;

                // Pokreni notifikaciju ako su ispunjeni svi uslovi.
                if ((months != StarostBebe) && (months > 0) && (months < 13)) {
                    startNotifikaciju(context);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    void startNotifikaciju(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashScreen.class), PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification;
        NotificationCompat.Builder builder;
        String id = context.getPackageName();
        String title = context.getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            assert mNotificationManager != null;
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_launcher_bw)
                    .setTicker(ScrollingText)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle(context.getText(R.string.app_name))
                    .setContentText(NotificationText);
        } else {
            builder = new NotificationCompat.Builder(context, id);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher_bw)
                        .setTicker(ScrollingText)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentTitle(context.getText(R.string.app_name))
                        .setContentText(NotificationText)
                        .setPriority(Notification.PRIORITY_HIGH);
            } else {
                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher_bw)
                        .setTicker(ScrollingText)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentTitle(context.getText(R.string.app_name))
                        .setContentText(NotificationText);
            }
        }

        notification = builder.build();
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFIKACIJA_ID, notification);
    }
}
