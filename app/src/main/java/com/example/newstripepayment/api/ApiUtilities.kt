package com.example.newstripepayment.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
    fun getApiInterface(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java) // Ensure this line is correct
    }
}