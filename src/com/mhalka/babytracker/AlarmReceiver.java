package com.mhalka.babytracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AlarmReceiver extends BroadcastReceiver {
	
	// Namjesti konstante za preference.
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String TRUDNOCA = "PracenjeTrudnoce";
	public static final String NOTIFIKACIJA = "Notifikacija";
	public static final String DAN = "DanPocetkaPracenja";
	public static final String MJESEC = "MjesecPocetkaPracenja";
	public static final String GODINA = "GodinaPocetkaPracenja";
	public static final String SEDMICA = "TrenutnaSedmicaTrudnoce";
	public static final String MJESECI = "TrenutnaStarostBebe";
	
	// Konstanta za notifikaciju.
	public static final int NOTIFIKACIJA_ID = 0;
	
	// Setiraj varijable.
	private NotificationManager notifier;
	private String ScrollingText;
	private String NotificationText;
		
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	// Povezi prethodno setirane varijable za elemente forme sa njihovim vrijednostima.
    	ScrollingText = context.getString(R.string.nove_informacije_scrolling);
    	NotificationText = context.getString(R.string.nove_informacije_notifikacija);
		
    	// Procitaj preference.
    	SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
    	Boolean pracenjeTrudnoce = settings.getBoolean(TRUDNOCA, true);
    	Integer SedmicaTrudnoce = settings.getInt(SEDMICA, 1);
    	Integer StarostBebe = settings.getInt(MJESECI, 1);
        
        // Setiraj vrijednosti varijabli potrebnih za izracunavanje starosti.
        Calendar datumPocetkaPracenja = new GregorianCalendar(settings.getInt(GODINA,1920), settings.getInt(MJESEC,0), settings.getInt(DAN,1));
        Calendar today = Calendar.getInstance();
        
        if(pracenjeTrudnoce) {
        	// Izracunaj starost ploda u sedmicama.
        	long weeksBetween = 0;
        	while (today.before(datumPocetkaPracenja)) {
        		today.add(Calendar.DAY_OF_MONTH, 6);
        		weeksBetween++;
        		}
        	int weeks = 43 - ((int) weeksBetween);
        	
        	// Pokreni notifikaciju ako su ispunjeni svi uslovi.
        	if(weeks != SedmicaTrudnoce) {
        		startNotifikaciju(context);
        	}
        } else {
        	// Izracunaj starost bebe u mjesecima.
        	long monthsBetween = 0;
        	while (datumPocetkaPracenja.before(today)) {
        		datumPocetkaPracenja.add(Calendar.MONTH, 1);
        		monthsBetween++;
        		}
        	int months = (int) monthsBetween;
        	
        	// Pokreni notifikaciju ako su ispunjeni svi uslovi.
        	if(months != StarostBebe) {
        		startNotifikaciju(context);
        	}
        }
    }
    
    public void startNotifikaciju(Context context) {
    	notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
        Notification notification = new Notification(R.drawable.ic_launcher, ScrollingText, System.currentTimeMillis());
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, SettingsActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        notification.setLatestEventInfo(context, context.getText(R.string.app_name), NotificationText, contentIntent);
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        
        notifier.notify(NOTIFIKACIJA_ID, notification);
    }
}
