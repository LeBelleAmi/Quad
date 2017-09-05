package com.lebelle.javadevelopers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Omawumi Eyekpimi on 09-Aug-17.
 */

public class JavaDevelopersResponse {
    @SerializedName("items")
    @Expose
    private List<JavaDevelopers> javaDevelopers = null;

    public List<JavaDevelopers> getJavaDevelopers(){
        return javaDevelopers;
    }

    //public void setJavaDevelopers (List<JavaDevelopers>javaDevelopers){
      //  this.javaDevelopers = javaDevelopers;
    //}

}
