package com.example.hxd.weatherforhxd.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by HXD on 2018/1/31.
 * 网络请求工具类
 */

public class HttpUtil {
    /**
     * 发送网络请求获取数据
     * @param url 数据地址
     * @param callback 返回数据
     */
    public static void sendOkHttpRequest(String url,okhttp3.Callback callback){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
