package com.example.hxd.weatherforhxd.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    @SerializedName("date")
    public String date;
    @SerializedName("code_txt_d")
    public String codeTxtD;
    @SerializedName("tmp_max")
    public String tmpMax;
    @SerializedName("tmp_min")
    public String tmpMin;
}
