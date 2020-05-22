package com.prolabs.thestorywriter

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RequestInterface {
    @FormUrlEncoded
    @POST("TSW/GetTemplates.php")
    public fun getTemplate(@Field("filename") folder:String): Call<ArrayList<Template>>
}