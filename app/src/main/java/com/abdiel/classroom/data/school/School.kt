package com.abdiel.classroom.data.school


import com.google.gson.annotations.SerializedName

data class School(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("school_name")
    val schoolName: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
) {
    override fun toString(): String {
        return schoolName ?: ""
    }
}