package webbtop.stormywebb.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import webbtop.stormywebb.R;
import webbtop.stormywebb.weather.Current;
import webbtop.stormywebb.weather.Day;
import webbtop.stormywebb.weather.Forecast;
import webbtop.stormywebb.weather.Hour;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Forecast mForecast;



    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.dateLabel) TextView mDateLabel;
    @BindView(R.id.tempLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.icon) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @OnClick(R.id.dailyButton)
    public void startDailyActivity(View v){
        Intent intent = new Intent(this, DailyForecastActivity.class);
        startActivity(intent);
    }

    //private TextView mTemperatureLabel;
    //private TextView mTimeLabel;
    //private TextView mDateLabel;
    //private TextView mHumidityValue;
    //private TextView mPrecipValue;
    //private TextView mSummaryLabel;
    //private ImageView mIconImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getForecast();
            }
        });

        //mTemperatureLabel = (TextView)findViewById(R.id.tempLabel);
        //mTimeLabel = (TextView)findViewById(R.id.timeLabel);
        //mDateLabel = (TextView)findViewById(R.id.dateLabel);
        //mHumidityValue = (TextView)findViewById(R.id.humidityValue);
        //mPrecipValue = (TextView)findViewById(R.id.precipValue);
        //mSummaryLabel = (TextView)findViewById(R.id.summaryLabel);
        //mIconImageView = (ImageView) findViewById(R.id.icon) ;


        getForecast();

    }

    private void getForecast() {
        String apiKey = "025f621019d777d4d8bd0e14e1e5b685";
        double latitude = 42.40;
        double longitude = -71.06;

        String forecastURL = "https://api.darksky.net/forecast/" + apiKey +"/" + latitude + "," + longitude;


        if (isNetworkAvailable()) {

            toggleRefresh();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(forecastURL).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleRefresh();
                    }
                });
                alertUserAboutError();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleRefresh();
                    }
                });

                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()){
                        mForecast = parseForecastDetails(jsonData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });


                    }
                    else{
                        alertUserAboutError();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Exception Catch", e);
                }
                catch (JSONException e){
                    Log.e(TAG, "Exception Catch", e);
                }

            }
        });

    }
        else {
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRefreshImageView.setVisibility(View.VISIBLE);
    }}

    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText(current.getFormattedTime() + "");
        mDateLabel.setText(current.getFormattedDate() + "");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() +"%");
        mSummaryLabel.setText(current.getSummary());
        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);

        //mTemperatureLabel.setText(String.valueOf(mCurrent.getTemperature()));
        //mTimeLabel.setText(String.format("At %s it will be", String.valueOf(mCurrent.getFormattedTime())));
        //mDateLabel.setText(String.format("The date is %s", String.valueOf(mCurrent.getFormattedDate())));
        //mPrecipValue.setText(String.valueOf(mCurrent.getPrecipChance()));
        //mHumidityValue.setText(String.valueOf(mCurrent.getHumidity()));
        //mSummaryLabel.setText(String.valueOf(mCurrent.getSummary()));
        //Drawable drawable = ResourcesCompat.getDrawable(getResources(), mCurrent.getIconId(), null);
        //mIconImageView.setImageDrawable(drawable);

    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException{
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");
        Day[] days = new Day[data.length()];
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Day day = new Day();
            day.setIcon(jsonHour.getString("icon"));
            day.setSummary(jsonHour.getString("summary"));
            day.setTime(jsonHour.getLong("time"));
            day.setTemperatureMax(jsonHour.getDouble("temperatureMax"));
            day.setTimezone(timezone);

            days[i] = day;
        }

        return days;

    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");
        Hour[] hours = new Hour[data.length()];
        for (int i = 0; i < data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTimezone(timezone);

            hours[i] = hour;

        }

        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getInt("temperature"));
        current.setTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());
        Log.d(TAG, current.getFormattedDate());

        return current;

    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }

        return isAvailable;

    };
}
