package com.example.capstonedesign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComponentService {
    @GET("component/{name}")
    Call<ComponentModel> component(@Path("name") String name);
}
