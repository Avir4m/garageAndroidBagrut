package com.example.garage;

import com.example.garage.models.CarMakesResponse;
import com.example.garage.models.CarModelsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CarQueryApi {
    @GET("/api/1.0/")
    Call<CarMakesResponse> getMakes(@Query("cmd") String cmd);

    @GET("/api/1.0/")
    Call<CarModelsResponse> getModels(@Query("cmd") String cmd, @Query("make") String make);
}
