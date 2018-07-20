package com.example.localadmin.prayertime.cloud;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServiceInterface {

    @GET("calendar")
    Call<ResponseBody> getData(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("method") String method, @Query("month") String month, @Query("year") String year);

    @GET("timings/{date_or_timestamp}")
    Call<ResponseBody> getPrayByTimeStamp(@Path("date_or_timestamp") String address, @Query("latitude") String latitude, @Query("longitude") String longitude);

    @GET("mpt.json")
    Call<ResponseBody> getPrayerTimeMalay(@Query("code") String code, @Query("filter") String filter);
}
