package com.example.capstonedesign;

import static com.example.capstonedesign.LoginFirstActivity.serverUrl;

import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 경로로 주어진 이미지 서버로 전송
public class UploadImage extends Thread{
    String path;

    public UploadImage(String path) {
        this.path = path;
    }

    public void run() {
        uploadImage();
    }

    private void uploadImage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        File file = new File(path);
        ImgUploadService service = retrofit.create(ImgUploadService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Upload", "success");
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Upload error:", t.getMessage());
            }
        });
    }
}
