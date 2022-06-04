package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.serverUrl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 이미지 정보(ID, 파일 경로) 데이터베이스에 저장
public class SaveImageInfo implements Callable<String> {
    String id;
    String path;

    public SaveImageInfo(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public String call() throws Exception {
        Gson gson = new GsonBuilder().setLenient().create();
        String filename = new File(path).getName();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ImgInfoService service = retrofit.create(ImgInfoService.class);

        Call<String> call = service.upload(id, filename);
        Response<String> response = call.execute();

        return response.body();
    }
}
