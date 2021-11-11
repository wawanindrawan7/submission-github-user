package com.dicoding.submission_3

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    var username: String,
    var location: String,
    var avatar: String,
    var company: String,
    var name: String,
    var repository: String,
    var followers: String,
    var following: String,

    ) : Parcelable
