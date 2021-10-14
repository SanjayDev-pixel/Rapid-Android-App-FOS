package motobeans.architecture.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by abul on 26/10/17.
 */

public class AppUtils {

    private static final String PREF_DATA = "app";
    public static final String PREF_TIME_DIFF="pref_time_diff";

    public static Date getCurrentDate(Context context) {

        Date date = null;

        LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
            .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return date;
        }
        Location location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            long time = location.getTime();
            date = new Date(Long.valueOf(time));
        }
        Toast.makeText(context, date.toString() + "", Toast.LENGTH_LONG).show();
        return date;
    }

    /*****************
     * Message showing
     * ****************/
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void printLog(String msg) {
        Log.e("app log", msg);
    }


    /*******************
     * setting check for autotime
     * *****************/
    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME, 0) == 1;
        }
    }


    public static boolean isTimeAutomaticDialog(Context c, final DialogCallback callback) {

        if (isTimeAutomatic(c)) {
            return true;
        } else {
            showDialog(c,
                    "ALERT",
                    "Please set the date time auto update in setting." +
                            "And try again.", callback);
            return false;
        }

    }

    public static void showDialog(final Context context, String heading, String msg, final DialogCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(heading);

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (callback == null) {
                    context.startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                } else {
                    callback.onOk();
                }
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /***********************
     * Timing control
     * ********************/

    public static long getCurrentDifference() {
        long elapsedTime = SystemClock.elapsedRealtime();
        long currentTime = System.currentTimeMillis();
        return currentTime - elapsedTime;
    }

    public static long getCurrentTime(Context context) {
        String diff = readStringFromPref(context, PREF_TIME_DIFF);
        long lastDifference = 0;

        if (diff != null && !diff.trim().equals("")) {
            lastDifference = Long.parseLong(readStringFromPref(context, PREF_TIME_DIFF));
        } else {
            return lastDifference;
        }

//Calculate the new difference
        long currentDifference = getCurrentDifference();

//Calculate the difference of the differences
//this is the change the user made
        long userChangeInMillis = lastDifference - currentDifference;

//and here we get the old time:
        long previousTime = System.currentTimeMillis() + userChangeInMillis;

        return previousTime;
    }


    /****************
     * get prefrences
     * ***************/
    public static String readStringFromPref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        if (!sharedPref.contains(key)) {
            return null;
        }
        return sharedPref.getString(key, "");
    }

    public static int readIntFromPref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        if (!sharedPref.contains(key)) {
            return 0;
        }
        return sharedPref.getInt(key, 0);
    }

    public static boolean readBoolFromPref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        if (!sharedPref.contains(key)) {
            return false;
        }
        return sharedPref.getBoolean(key, false);
    }

    public static Set<String> readSetFromPref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        if (!sharedPref.contains(key)) {
            return null;
        }
        return sharedPref.getStringSet(key, null);
    }

    /****************
     * save prefrences
     * ***************/
    public static boolean writeStringToPref(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        return sharedPref.edit().putString(key, value).commit();
    }

    public static void writeIntToPref(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        sharedPref.edit().putInt(key, value).apply();
    }

    public static void writeBoolToPref(Context context, String key, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(key, value).apply();
    }

    public static void writeSetToPref(Context context, String key, Set<String> value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        sharedPref.edit().putStringSet(key, value).apply();
    }

    /*clear data*/
    public static boolean clearSharedPrefData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        return editor.commit();
    }

    /********************
     * Distance between lat
     * ********************/
    public static double distance(double lat1, double lon1, double lat2,
                                  double lon2/*, double el1, double el2*/) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

//        double height = el1 - el2;

        distance = Math.pow(distance, 2) /*+ Math.pow(height, 2)*/;

        return Math.sqrt(distance);
    }


    public static Calendar millisToCalender(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar;
    }

    public static String getStringDate(Calendar calendar, String dateFromat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFromat);
        String formatted = format.format(calendar.getTime());
        return formatted;
    }

    public static String getStringDateFromMillis(long millis, String dateFromat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFromat);
        String formatted = format.format(millisToCalender(millis).getTime());
        return formatted;
    }

    /*public static String getTime(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }*/

    public static String getTime(long durationInMillis) {


        long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    public static boolean compareDate(Calendar cal1, Calendar cal2) {
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    /*Check the time is current , past or future*/
    public static int checkDateStatus(Calendar calendar){
        long time = calendar.getTimeInMillis();
        long current=System.currentTimeMillis();
        long difference=time-current;

        long sec=1000;
        long min=sec*60;

        if(Math.abs(difference)<min){
            return 0;
        }else {
            if(difference>0){
                /*future date*/
                return 1;
            }else {
                /*past date*/
                return -1;
            }
        }
    }
    public static int checkDateStatus(Calendar large, Calendar small) {
        long time = large.getTimeInMillis();
        long current = small.getTimeInMillis();
        long difference = time - current;

        long sec = 1000;
        long min = sec * 60;

        if (Math.abs(difference) < min) {
            return 0;
        } else {
            if (difference > 0) {
                /*future date*/
                return 1;
            } else {
                /*past date*/
                return -1;
            }
        }
    }

    /*convert directly*/
    public static String convertCalToString(Calendar calendar, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        String strDate = "";
        if (sdf != null) {
            strDate = sdf.format(calendar.getTime());
        }
        return strDate;
    }

    public static boolean isDateInBetween(Calendar start, Calendar end, Calendar current) {

        Date a = start.getTime();
        Date b = end.getTime();
        Date d = current.getTime();

        return a.compareTo(d) * d.compareTo(b) >= 0;
    }
}
