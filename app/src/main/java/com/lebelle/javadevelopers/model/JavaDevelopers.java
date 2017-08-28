package com.lebelle.javadevelopers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This represents the java developers
 */

public class JavaDevelopers {

    @SerializedName("login")
    @Expose
    /**parameter for username*/
    private String mUsername;


    @SerializedName("html_url")
    @Expose
    /**parameter for profile url*/
    private String mProfileUrl;



    @SerializedName("avatar_url")
    @Expose
    /**parameter for avatar*/
    private String mAvatar;


    @SerializedName("url")
    @Expose
    /** parameter of the user's github page link*/
    private String mUrl;


        /** create a new word object.
         * @param username is the username of the java developer
         *                 @param profileUrl is the fullname of the java developer
         *                           @param avatar is the image of the java developer
         *
         * @param url is the website URL to find the page of the java developer
         *
         */


        public JavaDevelopers(String username, String profileUrl, String avatar,  String url){
            this.mUsername = username;
            this.mProfileUrl = profileUrl;
            this.mAvatar = avatar;
            this.mUrl = url;
        }

        /**get the username of the java developer*/
        public String getUsername (){
            return mUsername;
        }

    /**get the fullname of the java developer*/
    public String getProfileUrl (){
        return mProfileUrl;
    }


        /**get the image of the java developer*/
        public String getAvatar (){
            return mAvatar;
        }

        /** Returns the website URL to find the page of the java developer*/
        public String getUrl() {return mUrl;}

    }


