package com.lebelle.javadevelopers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HP on 25-Aug-17.
 */

public class DevelopersProfile {
    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("blog")
    @Expose
    private String mBlog;

    @SerializedName("location")
    @Expose
    private String mLocation;

    @SerializedName("public_repos")
    @Expose
    private String mRepos;


    @SerializedName("bio")
    @Expose
    private String mBio;


    @SerializedName("followers")
    @Expose
    private String mFollowers;


    @SerializedName("following")
    @Expose
    private String mFollowing;

    public DevelopersProfile(String name, String bio, String location,  String repos, String following, String followers,
                             String blog){
        this.mName = name;
        this.mBio = bio;
        this.mLocation = location;
        this.mRepos = repos;
        this.mFollowing = following;
        this.mFollowers = followers;
        this.mBlog = blog;
    }

    public String getRealName() {
        return mName;
    }

    public String getBio() {
        return
        (mBio==null) ? mBio="Not Available": mBio;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getRepos() {
        return mRepos;
    }

    public String getFollowing() {
        return mFollowing;
    }

    public String getFollowers() {
        return mFollowers;
    }

    public String getBlog() {
        return mBlog;
    }

}
