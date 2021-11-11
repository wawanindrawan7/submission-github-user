package com.dicoding.submission_3.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite_user")
data class FavoriteUser(
    @PrimaryKey
    val id: Int,
    var username: String,
    var repository: String,
    var followers: String,
    var following: String,
    var avatar: String,
) : Serializable