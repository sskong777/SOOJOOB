package com.example.SOOJOOB.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface BadgeService {

    @GET("badges")
    fun getUserBadge(): Call<BadgesResponseBody>

    @GET("badges/no")
    fun getNoBadges(): Call<BadgesResponseBody>

    @GET("record/rank")
    fun getUserRank(): Call<RankResponseBody>
}