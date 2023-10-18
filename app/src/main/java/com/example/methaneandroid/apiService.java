package com.example.methaneandroid;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface apiService {
    @GET("/methane/api/login")
    //since only one account exists
    Call<String> login();
///<int:mon>/<int:day>/<int:hour>/<int:dur>/<dev>/<keys>/<filename>
    @GET("/methane/api/data/{year}/{mon}/{day}/{hour}/{dur}/{dev}/{keys}/{filename}")
    Call<ResponseBody> download(@Path("year") int year, @Path("mon") int mon, @Path("day") int day,
                                @Path("hour") int hour, @Path("dur") int dur, @Path("dev") String dev,
                                @Path("keys") String keys, @Path("filename") String filename);

}
