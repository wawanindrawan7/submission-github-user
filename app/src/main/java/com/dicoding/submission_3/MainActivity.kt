package com.dicoding.submission_3

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submission_3.DetailUserActivity.Companion.EXTRA_USER
import com.dicoding.submission_3.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<User>()
    private lateinit var adapter: ListUserAdapter


    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvUsers.setHasFixedSize(true)

        findUsers()

        val switchTheme = binding.toggleTheme

        val pref = SettingPreferences.getInstance(dataStore)

        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
            MainViewModel::class.java
        )

        var isCheck = false
        mainViewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            })

        switchTheme.setOnClickListener {
            isCheck = !isCheck
            mainViewModel.saveThemeSetting(isCheck)
            binding.toggleTheme.isChecked = isCheck

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String): Boolean {
                if (newText.isEmpty()) findUsers()
                else searchUser(newText)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) findUsers()
                else filterDataUser(newText)
                return false
            }
        })
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.favorite) {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filterDataUser(text: String) {
        val listFilterUser = ArrayList<User>()

        for (item in list) {
            if (item.username.contains(text)) {
                listFilterUser.add(item)
            }
        }
        adapter.setFilterUser(listFilterUser)

    }


    private fun findUsers() {
        showLoading(true)
        val client = ApiConfig.getApiService().getUsers()
        client.enqueue(object : Callback<List<UserResponseItem>> {
            override fun onResponse(
                call: Call<List<UserResponseItem>>,
                response: Response<List<UserResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (!responseBody.isNullOrEmpty()) {
                        setUserData(responseBody)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<UserResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setUserData(consumerReviews: List<UserResponseItem>) {
        val listuser = arrayListOf<User>()
        consumerReviews.forEach {
            val user = User(
                it.id,
                it.login,
                "",
                it.avatarUrl,
                "",
                "",
                it.publicRepos.toString(),
                it.followers.toString(),
                it.following.toString()
            )
            if (!list.contains(user)) {
                list.add(user)
            }
            listuser.add(user)
        }
        binding.notFound.isVisible = listuser.isEmpty()
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        adapter = ListUserAdapter(listuser)
        binding.rvUsers.adapter = adapter


        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                startActivity(
                    Intent(this@MainActivity, DetailUserActivity::class.java).putExtra(
                        EXTRA_USER,
                        data
                    )
                )
            }
        })

    }

    private fun searchUser(data: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().getSearchUser(data)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setUserData(responseBody.userResponse)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


}