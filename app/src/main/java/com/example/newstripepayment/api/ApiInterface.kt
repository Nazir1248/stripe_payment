//package com.example.newstripepayment.api
//
//
//import com.example.newstripepayment.Models.CustomerModel
//import com.example.newstripepayment.Models.PaymentIntentModel
//import com.example.newstripepayment.Utils.SECRET_KEY
//import retrofit2.Response
//import retrofit2.http.Headers
//import retrofit2.http.POST
//import retrofit2.http.Query
//
//interface ApiInterface {
//
//    @Headers("Authorization:Bearer $SECRET_KEY")
//    @POST("v1/customers")
//    suspend fun getCustomer() : Response<CustomerModel>
//
//
//
//    @Headers("Authorization:Bearer $SECRET_KEY",
//        "Stripe-Version: 2025-03-31.basil")
//    @POST("v1/ephemeral_keys")
//    suspend fun getEphemeralKey(
//        @Query("customer") customer : String
//    ) : Response<CustomerModel>
//
//
//
//    @Headers("Authorization:Bearer $SECRET_KEY")
//    @POST("v1/payment_intents")
//    suspend fun getPaymentIntent(
//        @Query("customer") customer : String,
//        @Query("amount") amount : String = "1099",
//        @Query("currency") currency : String = "eur",
//        @Query("automatic_payment_methods[enabled]") automatePay : Boolean =true,
//    ) : Response<PaymentIntentModel>
//}



// ApiInterface.kt
package com.example.newstripepayment.api

import PaymentIntentModel
import com.example.newstripepayment.Models.CustomerModel
import com.example.newstripepayment.Models.EphemeralKeyModel
import com.example.newstripepayment.Utils.SECRET_KEY
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomer(): Response<CustomerModel>

    @Headers(
        "Authorization: Bearer $SECRET_KEY",
        "Stripe-Version: 2022-11-15" // Use stable version
    )
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String
    ): Response<EphemeralKeyModel>

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customer: String,
        @Query("amount") amount: String = "1099",
        @Query("currency") currency: String = "eur",
        @Query("automatic_payment_methods[enabled]") automatePay: Boolean = true,
    ): Response<PaymentIntentModel>
}


