package com.example.localadmin.prayertime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.localadmin.prayertime.classes.PrayerTime;
import com.example.localadmin.prayertime.cloud.ApiServiceInterface;
import com.example.localadmin.prayertime.util.Utility;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView txtClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtClock = findViewById(R.id.txt_clock);
//
//        Intent i = new Intent(this, CompassActivity.class);
//        startActivity(i);


//        getPrayTime("1398332113", "3.950791", "101.992306");
        getPrayTimeMalay("wlp-0", "2");
    }

    private void getPrayTimeMalay(String code, String filter) {
        ApiServiceInterface apiEndPoint = Utility.getRetrofitInstanceMalay().create(ApiServiceInterface.class);
        Call<ResponseBody> call = apiEndPoint.getPrayerTimeMalay(code, filter);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String tmp = response.body().string();
                    JSONObject jsonData = new JSONObject(tmp);
                    Log.d("getPrayer", "onResponse: " + jsonData);


                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getPrayTime(String dateStamp, String lal, String longi) {
        ApiServiceInterface apiEndPoint = Utility.getRetrofitInstance().create(ApiServiceInterface.class);
        retrofit2.Call<ResponseBody> call = apiEndPoint.getPrayByTimeStamp(dateStamp, lal, longi);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String tmp = response.body().string();
                    JSONObject jsonData = new JSONObject(tmp);
                    Log.d("getDate", "onResponse: " + jsonData);
                    if (jsonData.getInt("code") == 200) {

                        JSONObject jsonInfo = jsonData.getJSONObject("data");
                        JSONObject jsonTimings = jsonInfo.getJSONObject("timings");
                        JSONObject jsonDate = jsonInfo.getJSONObject("date");

                        PrayerTime prayerTime = new PrayerTime();

                        prayerTime.Fajr = jsonTimings.getString("Fajr");
                        prayerTime.Sunrise = jsonTimings.getString("Sunrise");
                        prayerTime.Dhuhr = jsonTimings.getString("Dhuhr");
                        prayerTime.Asr = jsonTimings.getString("Asr");
                        prayerTime.Sunset = jsonTimings.getString("Sunset");
                        prayerTime.Maghrib = jsonTimings.getString("Maghrib");
                        prayerTime.Isha = jsonTimings.getString("Isha");
                        prayerTime.Imsak = jsonTimings.getString("Imsak");


                        JSONObject jsonHij = jsonDate.getJSONObject("hijri");
                        JSONObject jsonHWeek = jsonHij.getJSONObject("weekday");
                        JSONObject jsonHMonth = jsonHij.getJSONObject("month");

                        prayerTime.dateHijri = jsonHij.getString("date");
                        prayerTime.dayHijri = jsonHWeek.getString("en");
                        prayerTime.monthHijri = jsonHMonth.getString("en");
                        prayerTime.yearHijri = jsonHij.getString("year");

                        JSONObject jsonGeo = jsonDate.getJSONObject("gregorian");
                        JSONObject jsonGWeek = jsonGeo.getJSONObject("weekday");
                        JSONObject jsonGMonth = jsonGeo.getJSONObject("month");

                        prayerTime.dategrego = jsonGeo.getString("date");
                        prayerTime.daygrego = jsonGWeek.getString("en");
                        prayerTime.monthgrego = jsonGMonth.getString("en");
                        prayerTime.yeargrego = jsonGeo.getString("year");


                    }


                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
