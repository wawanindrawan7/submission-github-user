package com.dicoding.submission_3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submission_3.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private var adapter = ListUserAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = FavoriteViewModel(application)

        val actionbar = supportActionBar
        actionbar!!.title = "Favorite Users"

        setupAdapter()

        viewModel.getFavoriteUser()?.observe(this, {
            val data = it.map { favUser ->
                User(
                    id = favUser.id,
                    username = favUser.username,
                    location = "",
                    avatar = favUser.avatar,
                    company = "",
                    name = favUser.username,
                    repository = favUser.repository,
                    followers = favUser.followers,
                    following = favUser.following
                )
            }
            adapter.setFilterUser(data)
            binding.notFound.isVisible = data.isNullOrEmpty()
        })
    }

    private fun setupAdapter() {
        with(binding.rvFavorite) {
            this@FavoriteActivity.adapter.setOnItemClickCallback(object :
                ListUserAdapter.OnItemClickCallback {
                override fun onItemClicked(user: User) {
                    startActivity(
                        Intent(this@FavoriteActivity, DetailUserActivity::class.java).putExtra(
                            DetailUserActivity.EXTRA_USER,
                            user
                        )
                    )
                }

            })
            layoutManager =
                LinearLayoutManager(this@FavoriteActivity, LinearLayoutManager.VERTICAL, false)
            adapter = this@FavoriteActivity.adapter
        }
    }
}