package com.abdiel.classroom.data.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("school_id")
    val schoolId: Int?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
) : Parcelable