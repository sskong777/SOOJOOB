package com.example.SOOJOOB

import com.google.gson.annotations.SerializedName

data class SignUpRequestBody(
    @SerializedName("email")
    val email: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("password")
    val password: String?
)
