package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.serverUrl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 주성분 정보를 서버로부터 받아온다.
public class GetComponent implements Callable<ComponentModel> {
    private String name;
    private ComponentModel componentModel;

    public GetComponent(String name) {
        this.name = name;
    }

    public ComponentModel call() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ComponentService service = retrofit.create(ComponentService.class);
        Call<ComponentModel> call = service.component(name);
        Response<ComponentModel> response = call.execute();
        componentModel = response.body();

        return componentModel;
    }
}
