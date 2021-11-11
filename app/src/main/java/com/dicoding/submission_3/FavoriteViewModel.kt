package com.dicoding.submission_3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dicoding.submission_3.local.FavoriteUser
import com.dicoding.submission_3.local.FavoriteUserDao
import com.dicoding.submission_3.local.UserDatabase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private var userDao: FavoriteUserDao? = null
    private var userDb: UserDatabase? = null

    init {
        userDb = UserDatabase.getDataBase(application)
        userDao = userDb?.favoriteUserDao()
    }

    fun getFavoriteUser(): LiveData<List<FavoriteUser>>? {
        return userDao?.getFavoriteUser()
    }
}