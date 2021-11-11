package com.dicoding.submission_3

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Authorization: token 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")
    @GET("users")
    fun getUsers(): Call<List<UserResponseItem>>

    @GET("search/users")
    @Headers("Authorization: token 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")
    fun getSearchUser(
        @Query("q") query: String
    ): Call<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: token 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")
    fun getUserDetail(
        @Path("username") username: String
    ): Call<UserResponseItem>

    @GET("users/{username}/followers")
    @Headers("Authorization: token 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")
    fun getFollowers(
        @Path("username") username: String
    ): Call<List<UserResponseItem>>

    @GET("users/{username}/following")
    @Headers("Authorization: token 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")
    fun getFollowing(
        @Path("username") username: String
    ): Call<List<UserResponseItem>>


}