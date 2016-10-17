package webbtop.stormywebb.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import webbtop.stormywebb.R;

/**
 * Created by Webbtop on 10/9/2016.
 */

public class Current {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter1 = new SimpleDateFormat("h:mm a");

        formatter1.setTimeZone(TimeZone.getTimeZone(getTimeZone()));

        Date dateTime = new Date(getTime() * 1000);
        String timeString1 = formatter1.format(dateTime);

        return timeString1;
    }
    public String getFormattedDate(){
        SimpleDateFormat formatter2 = new SimpleDateFormat("EEE, MMM d, ''yy");
        formatter2.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime() * 1000);
        String timeString2 = formatter2.format(dateTime);
        return timeString2;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public double getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(mPrecipChance);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }
}
