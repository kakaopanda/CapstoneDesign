package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.serverUrl;

import android.util.Log;

import com.bumptech.glide.util.ExceptionCatchingInputStream;

import java.io.File;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 이미지 파일 서버로 업로드
public class UploadImage implements Callable<String> {
    String path;

    public UploadImage(String path) {
        this.path = path;
    }

    public String call() throws Exception{
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
        Response<ResponseBody> response = call.execute();

        return response.body().toString();
    }
}
