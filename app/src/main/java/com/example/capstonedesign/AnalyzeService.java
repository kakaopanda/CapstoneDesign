package com.example.capstonedesign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AnalyzeService {
    @GET("analyze/{id}/{filename}")
    Call<PillModel> analyze(@Path("id")String id, @Path("filename")String filename);
}
