package com.example.capstonedesign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImgInfoService {
    @GET("upload/{id}/{filename}")
    Call<String> upload(@Path("id") String id, @Path("filename")String filename);
}
