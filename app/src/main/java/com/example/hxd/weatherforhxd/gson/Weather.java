package com.example.hxd.weatherforhxd.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * {
 * "HeWeather6": [
 * {
 * "basic": {
 * "cid": "CN101010100",
 * "location": "beijing",
 * "parent_city": "beijing",
 * "admin_area": "beijing",
 * "cnty": "China",
 * "lat": "39.90498734",
 * "lon": "116.4052887",
 * "tz": "+8.0"
 * },
 * "update": {
 * "loc": "2018-01-31 16:51",
 * "utc": "2018-01-31 08:51"
 * },
 * "status": "ok",
 * "now": {
 * "cloud": "0",
 * "cond_code": "100",
 * "cond_txt": "Sunny/Clear",
 * "fl": "-5",
 * "hum": "15",
 * "pcpn": "0.0",
 * "pres": "1027",
 * "tmp": "2",
 * "vis": "8",
 * "wind_deg": "237",
 * "wind_dir": "WSW",
 * "wind_sc": "1-2",
 * "wind_spd": "7"
 * }
 * }
 * ]
 * }
 */
public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Update update;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    @SerializedName("lifestyle")
    public List<Lifestyle> lifestyleList;
}
