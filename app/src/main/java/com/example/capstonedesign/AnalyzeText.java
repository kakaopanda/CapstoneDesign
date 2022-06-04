package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.serverUrl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 유저가 입력한 알약 정보로 이미지 분석
public class AnalyzeText implements Callable<PillModel> {
    private String color, shape, text;
    private PillModel pillModel;

    public AnalyzeText(String color, String shape, String text) {
        this.color = color;
        this.shape = shape;
        this.text = text;
    }

    public PillModel call() throws Exception{
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        TextAnalyzeService service = retrofit.create(TextAnalyzeService.class);
        Call<PillModel> call = service.text(color, shape, text);
        Response<PillModel> response = call.execute();
        pillModel = response.body();

        return pillModel;
    }
}