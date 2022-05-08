package com.example.capstonedesign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LoginService {
    @GET("login/{id}/{pw}")
    Call<String> login(@Path("id") String id, @Path("pw")String pw);
}
