package com.zemnuhov.stressapp.ServerAPI;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zemnuhov.stressapp.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;

public interface StressAppAPI {
    @GET("api/users/")
    Call<List<User>> getUsers();

    @POST("api/authorization/")
    Call<User> authorization(@Body AuthorizationRequest authorizationRequest);
}

