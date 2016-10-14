package webbtop.stormywebb.weather;

/**
 * Created by Webbtop on 10/12/2016.
 */

public class Forecast {
    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHourlyForecast() {
        return mHourlyForecast;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public Day[] getDailyForecast() {
        return mDailyForecast;
    }

    public void setDailyForecast(Day[] dailyForecast) {
        mDailyForecast = dailyForecast;
    }

    private Current mCurrent;
    private Hour[] mHourlyForecast;
    private Day[] mDailyForecast;
}
