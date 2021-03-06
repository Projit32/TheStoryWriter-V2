package com.prolabs.thestorywriter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.NullPointerException

class RetrofitClient {

    lateinit var retrofit: Retrofit

    fun build():RetrofitClient{
        retrofit=Retrofit.Builder()
            .baseUrl("https://proapplication.000webhostapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return this
    }

    fun fetchTemplates(folderName:String):Call<ArrayList<Template>>{
        return retrofit.create(RequestInterface::class.java).getTemplate(folderName)
    }
}