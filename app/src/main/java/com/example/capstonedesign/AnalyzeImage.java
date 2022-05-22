package com.example.capstonedesign;

import static com.example.capstonedesign.LoginFirstActivity.serverUrl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnalyzeImage implements Callable<PillModel> {
    private String id;
    private String path;
    private PillModel pillModel;

    public AnalyzeImage(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public PillModel call() throws Exception{
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();
        String filename = new File(path).getName();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        AnalyzeService service = retrofit.create(AnalyzeService.class);
        Call<PillModel> call = service.analyze(id, filename);
        Response<PillModel> response = call.execute();
        pillModel = response.body();

        return pillModel;

//        --아래 코드는 비동기식--
//        Callback<PillModel> cb = new Callback<PillModel>() {
//            @Override
//            public void onResponse(Call<PillModel> call, Response<PillModel> response) {
//                if (response.isSuccessful()) {
//                    pillModel = response.body();
//                    Log.e("Analyze", "onResponse: 성공, 결과: " + pillModel);
//
//                } else {
//                    Log.e("Analyze", "onResponse: 실패");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PillModel> call, Throwable t) {
//                Log.e("Analyze", "onFailure " + t.getMessage());
//            }
//        };
//        call.enqueue(cb);
    }
}