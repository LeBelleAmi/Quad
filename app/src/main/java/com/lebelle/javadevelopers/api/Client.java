package com.lebelle.javadevelopers.api;

import com.lebelle.javadevelopers.query.Url;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Omawumi Eyekpimi on 25-Jun-17.
 */

public class Client {
    public static Retrofit retrofit = null;
    public static Retrofit getClient(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Url.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
