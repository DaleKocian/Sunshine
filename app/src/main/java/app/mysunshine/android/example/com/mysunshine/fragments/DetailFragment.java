package app.mysunshine.android.example.com.mysunshine.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.mysunshine.android.example.com.mysunshine.R;
import app.mysunshine.android.example.com.mysunshine.activities.DetailActivity;
import app.mysunshine.android.example.com.mysunshine.data.WeatherContract;
import app.mysunshine.android.example.com.mysunshine.data.WeatherContract.WeatherEntry;
import app.mysunshine.android.example.com.mysunshine.utils.Utility;
import butterknife.ButterKnife;

/**
 * Created by k557782 on 9/15/14.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private static final int DETAIL_LOADER = 0;
    private static final String LOCATION_KEY = "location";
    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String mLocation;
    private String mForecast;
    private ShareActionProvider mShareActionProvider;
    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = ButterKnife.findById(rootView, R.id.detail_icon);
        mDateView = ButterKnife.findById(rootView, R.id.detail_date_textview);
        mFriendlyDateView = ButterKnife.findById(rootView, R.id.detail_day_textview);
        mDescriptionView = ButterKnife.findById(rootView, R.id.detail_forecast_textview);
        mHighTempView = ButterKnife.findById(rootView, R.id.detail_high_textview);
        mLowTempView = ButterKnife.findById(rootView, R.id.detail_low_textview);
        mHumidityView = ButterKnife.findById(rootView, R.id.detail_humidity_textview);
        mWindView = ButterKnife.findById(rootView, R.id.detail_wind_textview);
        mPressureView = ButterKnife.findById(rootView, R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DetailActivity.DATE_KEY)) {
            return null;
        }
        String forecastDate = intent.getStringExtra(DetailActivity.DATE_KEY);
        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri =
                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, forecastDate);
        Log.v(LOG_TAG, weatherForLocationUri.toString());
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            boolean isMetric = Utility.isMetric(getActivity());
            int weatherId = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            String dateString = Utility.formatDate(
                    cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));
            String weatherDescription =
                    cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            String high = Utility.formatTemperature(getActivity(),
                    cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
            String low = Utility.formatTemperature(getActivity(),
                    cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
            float humidity = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
            float windSpeedStr = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
            float windDirStr = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
            float pressure = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
            mDateView.setText(dateString);
            mDescriptionView.setText(weatherDescription);
            mHighTempView.setText(high);
            mLowTempView.setText(low);
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));
            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
            Log.v(LOG_TAG, "Forecast String: " + mForecast);
            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}