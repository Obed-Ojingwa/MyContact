package com.obedcodes.managemycontact

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GooglePeopleApiService {
    @POST("v1/people:createContact")
    fun createContact(
        @Header("Authorization") authHeader: String,
        @Body contact: GoogleContact
    ): Call<Void>
}


