package com.example.tuananh.projectmap.network;

import com.example.tuananh.projectmap.model.MapResponse;
import com.example.tuananh.projectmap.model.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by TuanAnh on 09-Sep-17.
 */

public interface APIService {
    @GET("api/directions/json")
    Call<MapResponse> getDirection(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);

    // @FormUrlEncoded khi so tham so nhieu hon 1
//    @FormUrlEncoded
//    @POST("api/directions/json")
//    Call<MapResponse> getDirections(@Field("origin") String origin,
// @Field("destination") String destination, @Field("key") String key);

    @GET("api/place/nearbysearch/json")
    Call<PlaceResponse> getPlace(@Query("location") String location, @Query("radius") String radius,
                                 @Query("type") String type, @Query("key") String key);
}
