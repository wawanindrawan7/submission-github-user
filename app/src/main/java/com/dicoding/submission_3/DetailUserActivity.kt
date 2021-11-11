package com.dicoding.submission_3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.submission_3.databinding.ActivityDetailUserBinding
import com.dicoding.submission_3.local.FavoriteUser
import com.dicoding.submission_3.local.FavoriteUserDao
import com.dicoding.submission_3.local.UserDatabase
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private var userDao: FavoriteUserDao? = null
    private var userDb: UserDatabase? = null

    companion object {
        const val EXTRA_USER = "extra_user"
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
        //actionbar
        val actionbar = supportActionBar
        actionbar!!.title = "Detail User"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayShowHomeEnabled(true)

        userDb = UserDatabase.getDataBase(applicationContext)
        userDao = userDb?.favoriteUserDao()

        val username = user.username
        findUsers(username)

        val sectionsPagerAdapter = FollowPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        sectionsPagerAdapter.username = user.username

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        var isChecked = false

        CoroutineScope(Dispatchers.IO).launch {
            val count = checkUser(user.id)
            withContext(Dispatchers.Main) {
                if (count != null) {
                    if (count > 0) {
                        binding.toggleFavorite.isChecked = true
                        isChecked = true
                    } else {
                        binding.toggleFavorite.isChecked = false
                        isChecked = false
                    }
                }
            }
        }

        binding.toggleFavorite.setOnClickListener {
            isChecked = !isChecked
            if (isChecked) {
                addToFavorite(user)
            } else {
                removeFromFavorite(user.id)
            }
            binding.toggleFavorite.isChecked = isChecked
        }

        supportActionBar?.elevation = 0f

        val bundle = Bundle()
        bundle.getString(EXTRA_USER, username)

    }

    fun checkUser(id: Int) = userDao?.checkUser(id)

    private fun addToFavorite(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            var user = FavoriteUser(
                user.id,
                user.username,
                user.repository,
                user.followers,
                user.following,
                user.avatar
            )
            userDao?.addToFavorite(user)
        }
    }

    fun removeFromFavorite(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.removeFromFavorite(id)
        }
    }

    private fun findUsers(username: String) {
        val client = ApiConfig.getApiService().getUserDetail(username)
        client.enqueue(object : Callback<UserResponseItem> {
            override fun onResponse(
                call: Call<UserResponseItem>,
                response: Response<UserResponseItem>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    setData(responseBody)
                }
            }

            override fun onFailure(call: Call<UserResponseItem>, t: Throwable) {
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setData(dataUser: UserResponseItem) {
        binding.tvUsername.text = dataUser.login

        Glide.with(this)
            .load(dataUser.avatarUrl)
            .apply(RequestOptions())
            .into(binding.imageView2)

        binding.tvNama.text = dataUser.name
        binding.tvRepository.text = dataUser.publicRepos.toString()
        binding.tvFollowers.text = dataUser.followers.toString()
        binding.tvFollowing.text = dataUser.following.toString()
        binding.tvLokasi.text = dataUser.location
        binding.tvCompany.text = dataUser.company


    }


}

