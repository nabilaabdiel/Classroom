package com.abdiel.classroom.api

import retrofit2.http.*

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
//        @Header("device_token") device_token: String?,
        @Field("phone") phone: String?,
        @Field("password") password: String?
    ): String

    //Logout
    @POST("api/auth/logout")
    suspend fun logout() : String

    //getProfile
    @GET("api/auth/getprofile")
    suspend fun getProfile() : String

    //Register
    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("school_id") school_id: Int,
        @Field("password") password: String,
        @Field("confirm_password") confirm_password: String
    ) : String

    //Change Password
    @FormUrlEncoded
    @POST("auth/change-password")
    suspend fun changePassword(
        @Field("old_password") old_password : String,
        @Field("new_password") new_password : String,
        @Field("password_confirmation") password_confirmation : String
    ) : String

    //Profile Update
    @FormUrlEncoded
    @POST("user/profile")
    suspend fun userUpdate(
        @Field("name") name: String,
        @Field("school_id") school_id : Int,
        @Field("phone") phone : String,
    ) : String

    //Get Friend List
    @GET("api/auth/getfriend")
    suspend fun getFriend(
    ) : String

    //Detail Friend
    @GET("friends/2282bdc1")
    suspend fun detailFriend(
    ) : String

    //Get School
    @GET("api/auth/getschools")
    suspend fun getSchool(
    ) : String
}