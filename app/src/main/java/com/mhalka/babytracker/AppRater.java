package com.mhalka.babytracker;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

public class AppRater {
	public static final String PREFS_NAME = "BabyTrackerPrefs";
	public static final String DONT_SHOW_AGAIN = "DontShowAgain";
	public static final String LAUNCH_COUNT = "LaunchCount";
	public static final String FIRST_LAUNCH_DATE = "FirstLaunchDate";
	
	private final static int DAYS_UNTIL_PROMPT = 10;
	private final static int LAUNCHES_UNTIL_PROMPT = 5;
	
	public static void appLaunched(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);
		if (prefs.getBoolean(DONT_SHOW_AGAIN, false)) {
			return;
		}
		
		SharedPreferences.Editor editor = prefs.edit();
		
		// Povecaj brojac pokretanja aplikacije
		long launch_count = prefs.getLong(LAUNCH_COUNT, 0) + 1;
		editor.putLong(LAUNCH_COUNT, launch_count);
		
		// Dobavi datum prvog pokretanja
		Long date_firstLaunch = prefs.getLong(FIRST_LAUNCH_DATE, 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong(FIRST_LAUNCH_DATE, date_firstLaunch);
		}
		
		// Sacekaj definisani broj dana prije pokazivanja dijaloga
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch +
					(DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(mContext, editor);
			}
		}
		
		editor.apply();
	}
	
	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(mContext.getString(R.string.meni_rateapp));
		dialog.setMessage(mContext.getString(R.string.rateapp_dialog_msg));
		dialog.setCancelable(false);
		dialog.setPositiveButton(mContext.getString(R.string.rateapp_dialog_rate),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int w) {
				rateApp(mContext);
				if (editor != null) {
					editor.putBoolean(DONT_SHOW_AGAIN, true);
					editor.commit();
				}
			}
		});
		dialog.setNeutralButton(mContext.getString(R.string.rateapp_dialog_remindlater),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int w) {
				if (editor != null) {
					editor.putLong(LAUNCH_COUNT, 0);
					editor.commit();
				}
			}
		});
		dialog.setNegativeButton(mContext.getString(R.string.rateapp_dialog_dontshowagain),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int w) {
				if (editor != null) {
					editor.putBoolean(DONT_SHOW_AGAIN, true);
					editor.commit();
				}
			}
		});
		dialog.create();
		dialog.show();
	}
	
	private static boolean tryStartActivity(Intent aIntent, Context mContext) {
		try {
			mContext.startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}
	
	public static void rateApp(Context mContext) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// Probaj Google Play
		intent.setData(Uri.parse("market://details?id=com.mhalka.babytracker"));
		if (!tryStartActivity(intent, mContext)) {
			// Izgleda da Google Play nije instaliran, probaj web browser
			intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.mhalka.babytracker"));
			if (!tryStartActivity(intent, mContext)) {
				// Ako nista od prethodnog ne upali informisi korisnika o tome
				Toast.makeText(mContext, mContext.getString(R.string.no_google_play), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
