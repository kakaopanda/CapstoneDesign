package com.example.capstonedesign;

import static com.example.capstonedesign.LoginFirstActivity.serverUrl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 이미지 정보 데이터베이스 저장
public class SaveImage extends Thread{
    String id;
    String path;

    public SaveImage(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public void run() {
        saveImage();
    }

    private void saveImage() {
        Gson gson = new GsonBuilder().setLenient().create();
        String filename = new File(path).getName();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ImgInfoService service = retrofit.create(ImgInfoService.class);

        Call<String> call = service.upload(id, filename);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String result = response.body();
                    Log.e("Image","onResponse: 성공, 결과: "+result.toString());
                }
                else {
                    Log.e("Image", "onResponse: 실패" );
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Image", "onFailure " + t.getMessage());
            }
        });
    }
}
