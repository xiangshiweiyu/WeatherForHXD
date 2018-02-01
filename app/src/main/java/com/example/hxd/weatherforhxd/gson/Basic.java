package com.example.hxd.weatherforhxd.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HXD on 2018/1/31.
 */

public class Basic {
    @SerializedName("location")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
}
