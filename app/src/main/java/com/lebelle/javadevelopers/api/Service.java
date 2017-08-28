package com.lebelle.javadevelopers.api;


import com.lebelle.javadevelopers.model.DevelopersProfile;
import com.lebelle.javadevelopers.model.JavaDevelopersResponse;
import com.lebelle.javadevelopers.query.Url;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Omawumi Eyekpimi on 25-Jun-17.
 */

public interface Service {
    @GET(Url.SEARCH_URL)
    Call<JavaDevelopersResponse> getJavaDevelopers(@Query("page") int page);

    @GET(Url.USER + "{login}")
    Call<DevelopersProfile> getDevelopersProfile(@Path("login") String login);
}
