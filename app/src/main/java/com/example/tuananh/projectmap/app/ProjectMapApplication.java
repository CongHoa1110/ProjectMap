package com.example.tuananh.projectmap.app;

import android.app.Application;

import com.example.tuananh.projectmap.network.APIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TuanAnh on 09-Sep-17.
 */

public class ProjectMapApplication extends Application {
    public static Retrofit mRetrofit;
    public static APIService apiService;
    public static final String BASE_API = "https://maps.googleapis.com/maps/";

    @Override
    public void onCreate() {
        super.onCreate();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = mRetrofit.create(APIService.class);
    }
}
