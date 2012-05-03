package com.mhalka.babytracker;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartAtBootReceiver extends BroadcastReceiver {

	private AlarmManager am;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Namjesti vrijeme za alarm i okidanje notifikacije
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent receiver = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				receiver, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				(24 * 60 * 60 * 1000), pendingIntent);
	}

}
