package com.example.hxd.weatherforhxd.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hxd.weatherforhxd.R;
import com.example.hxd.weatherforhxd.gson.AQI;
import com.example.hxd.weatherforhxd.gson.Forecast;
import com.example.hxd.weatherforhxd.gson.Lifestyle;
import com.example.hxd.weatherforhxd.gson.Weather;
import com.example.hxd.weatherforhxd.util.HttpUtil;
import com.example.hxd.weatherforhxd.util.JsonUtil;
import com.example.hxd.weatherforhxd.util.StaticUtil;

import java.io.IOException;
import java.util.logging.StreamHandler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView svWeather;
    private TextView tvTitle;
    private TextView tvTitleTime;
    private TextView tvDegreeText;
    private TextView tvWeatherText;
    private LinearLayout llForrcast;
    private TextView tvAqiText;
    private TextView tvPm25;
    private TextView tvComfor;
    private TextView tvWashText;
    private TextView tvSportText;
    private ImageView ivWeatherBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将通知了和主界面设置为同一个背景
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        initView();

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = spf.getString("weather", null);

        String weatherPic = spf.getString("bing_pic", null);
        if (weatherPic != null) {
            Glide.with(this).load(weatherPic).into(ivWeatherBackground);
        } else {
            loadWeatherPic();
        }
        if (weatherString != null) {
            //有缓存时直接加载缓存数据
            Weather weather = JsonUtil.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //没有缓存时进行网络请求
            String weatherId = getIntent().getStringExtra("weather_id");
            svWeather.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
        svWeather = findViewById(R.id.sv_weather);
        tvAqiText = findViewById(R.id.tv_pm25_text);
        tvComfor = findViewById(R.id.tv_comfort_text);
        tvDegreeText = findViewById(R.id.tv_degree);
        tvWeatherText = findViewById(R.id.tv_weather);
        tvSportText = findViewById(R.id.tv_sport_wash);
        tvTitle = findViewById(R.id.tv_city_title);
        tvTitleTime = findViewById(R.id.tv_title_update_time);
        tvWashText = findViewById(R.id.tv_car_wash);
        llForrcast = findViewById(R.id.ll_forecast);
        ivWeatherBackground = findViewById(R.id.iv_weather_background);

    }

    /**
     * 加载天气背景图片
     */
    private void loadWeatherPic() {
        HttpUtil.sendOkHttpRequest(StaticUtil.WEATHER_BACKGROUND_PIC, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picResponse = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", picResponse);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(picResponse).
                                into(ivWeatherBackground);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    /**
     * 根据id请求天气信息
     *
     * @param weatherId 城市id
     */
    private void requestWeather(String weatherId) {
        String weatherUrl = StaticUtil.WEATHER_URL + weatherId + StaticUtil.WEATHER_KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = JsonUtil.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.
                                    LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.
                                LENGTH_LONG).show();
                    }
                });
            }
        });
        loadWeatherPic();
    }

    /**
     * 处理展示weather实体类数据
     *
     * @param weather 实体类数据
     */
    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.cond_txt;

        tvTitle.setText(cityName);
        tvTitleTime.setText(updateTime);
        tvDegreeText.setText(degree);
        tvWeatherText.setText(weatherInfo);
        llForrcast.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, llForrcast,
                    false);
            TextView tvData = view.findViewById(R.id.tv_data_text);
            TextView tvInfo = view.findViewById(R.id.tv_info_text);
            TextView tvMax = view.findViewById(R.id.tv_max_text);
            TextView tvMin = view.findViewById(R.id.tv_min_text);

            tvData.setText(forecast.date);
            tvInfo.setText(forecast.codeTxtD);
            tvMax.setText(forecast.tmpMax);
            tvMin.setText(forecast.tmpMin);
            llForrcast.addView(view);
        }
        if (weather.aqi != null) {
            tvAqiText.setText(weather.aqi.city.aqi);
            tvPm25.setText(weather.aqi.city.pm25);
        }
        for (Lifestyle lifestyle : weather.lifestyleList) {
            String type = lifestyle.type;
            switch (type) {
                case "trav":
                    String comfort = "舒适度：" + lifestyle.txt;
                    tvComfor.setText(comfort);
                    break;
                case "cw":
                    String carWash = "洗车指数:" + lifestyle.txt;
                    tvWashText.setText(carWash);
                    break;
                case "sport":
                    String sport = "运动建议:" + lifestyle.txt;
                    tvSportText.setText(sport);
                    break;
                default:
            }
        }
        svWeather.setVisibility(View.VISIBLE);
    }
}
