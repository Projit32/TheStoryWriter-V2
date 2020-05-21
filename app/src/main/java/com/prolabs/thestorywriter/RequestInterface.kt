package com.prolabs.thestorywriter

import retrofit2.Call
import retrofit2.http.POST

interface RequestInterface {
    @POST("TSW/GetTemplates.php")
    public fun getTemplate(): Call<ArrayList<Template>>
}