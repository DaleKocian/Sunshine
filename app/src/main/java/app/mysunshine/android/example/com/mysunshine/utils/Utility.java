package app.mysunshine.android.example.com.mysunshine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

import app.mysunshine.android.example.com.mysunshine.R;
import app.mysunshine.android.example.com.mysunshine.data.WeatherContract;
import app.mysunshine.android.example.com.mysunshine.fragments.ForecastFragment;

/**
 * Created by dkocian on 9/9/2014.
 */
public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if (!isMetric) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    public static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

    public static String getForecastString(Cursor cursor, boolean isMetric) {
        return String.format("%s - %s - %s/%s",
                formatDate(cursor.getString(ForecastFragment.COL_WEATHER_DATE)),
                cursor.getString(ForecastFragment.COL_WEATHER_DESC),
                formatTemperature(cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric),
                formatTemperature(cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric));
    }
}
