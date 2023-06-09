package com.abdiel.classroom.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("device_token") device_token: String?,
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
    @POST("api/auth/editpassword")
    suspend fun changePassword(
        @Field("old_password") old_password : String,
        @Field("new_password") new_password : String
    ) : String

    //Profile Update
    @FormUrlEncoded
    @POST("api/auth/editprofile")
    suspend fun editProfile(
        @Field("name") name: String,
        @Field("school_id") school_id: Int,
    ) : String

    @Multipart
    @POST("api/auth/editprofile")
    suspend fun editProfileImg(
        @Part("name") name: String,
        @Part("school_id") school_id: Int,
        @Part photo : MultipartBody.Part?
    ) : String

    //Get Friend List
    @GET("api/auth/getfriend")
    suspend fun getFriend(
    ) : String

    //Get School
    @GET("api/auth/getschools")
    suspend fun getSchool(
    ) : String

    //sendNotification
    @FormUrlEncoded
    @POST("api/auth/sendNotification")
    suspend fun sendNotification(
        @Field("title") title: String?,
        @Field("body") body: String?,
        @Field("user_id") userId: Int?
    ) : String
}