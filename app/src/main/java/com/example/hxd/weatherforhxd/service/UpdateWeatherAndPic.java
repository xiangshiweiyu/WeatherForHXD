package com.example.hxd.weatherforhxd.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.hxd.weatherforhxd.activity.WeatherActivity;
import com.example.hxd.weatherforhxd.gson.Update;
import com.example.hxd.weatherforhxd.gson.Weather;
import com.example.hxd.weatherforhxd.util.HttpUtil;
import com.example.hxd.weatherforhxd.util.JsonUtil;
import com.example.hxd.weatherforhxd.util.StaticUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateWeatherAndPic extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updatePic();
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+hour;
        Intent i=new Intent(this, UpdateWeatherAndPic.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,i,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 后台更新图片
     */
    private void updatePic() {
        HttpUtil.sendOkHttpRequest(StaticUtil.WEATHER_BACKGROUND_PIC, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picResponse = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(UpdateWeatherAndPic.this).edit();
                editor.putString("bing_pic", picResponse);
                editor.apply();
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    /**
     * 后台更新天气
     */
    private void updateWeather() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = spf.getString("weather", null);
        if (weatherString != null) {
            Weather weather = JsonUtil.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = StaticUtil.WEATHER_URL + weatherId + StaticUtil.WEATHER_KEY;
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherResponse = response.body().string();
                    Weather weather = JsonUtil.handleWeatherResponse(weatherResponse);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(UpdateWeatherAndPic.this).edit();
                        editor.putString("weather", weatherResponse);
                        editor.apply();
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {

                }


            });
        }
    }
}
