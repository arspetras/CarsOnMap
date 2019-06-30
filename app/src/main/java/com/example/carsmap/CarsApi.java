package com.example.carsmap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CarsApi {

    @GET("api/mobile/public/availablecars")
    Call<List<Post>> getPosts();

}
