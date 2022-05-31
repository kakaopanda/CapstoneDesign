package com.example.capstonedesign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TextAnalyzeService {
    @GET("text/{color}/{shape}/{text}")
    Call<PillModel> text(@Path("color")String color, @Path("shape")String shape, @Path("text")String text);
}
