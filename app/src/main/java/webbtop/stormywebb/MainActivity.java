package webbtop.stormywebb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;



    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.dateLabel) TextView mDateLabel;
    @BindView(R.id.tempLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.icon) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

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
                        mCurrentWeather = getCurrentDetails(jsonData);
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
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText(mCurrentWeather.getFormattedTime() + "");
        mDateLabel.setText(mCurrentWeather.getFormattedDate() + "");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() +"%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);

        //mTemperatureLabel.setText(String.valueOf(mCurrentWeather.getTemperature()));
        //mTimeLabel.setText(String.format("At %s it will be", String.valueOf(mCurrentWeather.getFormattedTime())));
        //mDateLabel.setText(String.format("The date is %s", String.valueOf(mCurrentWeather.getFormattedDate())));
        //mPrecipValue.setText(String.valueOf(mCurrentWeather.getPrecipChance()));
        //mHumidityValue.setText(String.valueOf(mCurrentWeather.getHumidity()));
        //mSummaryLabel.setText(String.valueOf(mCurrentWeather.getSummary()));
        //Drawable drawable = ResourcesCompat.getDrawable(getResources(), mCurrentWeather.getIconId(), null);
        //mIconImageView.setImageDrawable(drawable);

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getInt("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());
        Log.d(TAG, currentWeather.getFormattedDate());

        return currentWeather;

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
