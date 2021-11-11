package com.dicoding.submission_3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submission_3.databinding.FragmentFollowerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowerFragment : Fragment() {

    private lateinit var binding: FragmentFollowerBinding
    private lateinit var adapter: ListUserAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvFollowers.setHasFixedSize(true)
            rvFollowers.layoutManager = LinearLayoutManager(activity)
        }

        val index = arguments?.getInt(ARG_SECTION_NUMBER)
        val username = arguments?.getString(EXTRA_USERNAME)

        findUsersFollowers(username.toString())
    }

    companion object {
        private const val EXTRA_USERNAME = "username"
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val TAG = "MainActivity"

        fun newInstance(index: Int, username: String): FollowerFragment {
            val fragment = FollowerFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_USERNAME, username)
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle

            Log.d("INDEX", index.toString())
            Log.d("USERNAME FOLLOWER", username.toString())
            return fragment
        }
    }

    private fun findUsersFollowers(username: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().getFollowers(username)
        client.enqueue(object : Callback<List<UserResponseItem>> {
            override fun onResponse(
                call: Call<List<UserResponseItem>>,
                response: Response<List<UserResponseItem>>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    setData(responseBody)
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<List<UserResponseItem>>, t: Throwable) {
                showLoading(false)
            }
        })
    }

    private fun setData(dataUser: List<UserResponseItem>) {
        val listuser = ArrayList<User>()
        for (user in dataUser) {
            var data = User(
                user.id,
                user.login,
                "",
                user.avatarUrl,
                "",
                "",
                user.publicRepos.toString(),
                user.followers.toString(),
                user.following.toString()
            )
            listuser.add(data)
        }

        adapter = ListUserAdapter(listuser)
        binding.rvFollowers.adapter = adapter

        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                startActivity(
                    Intent(requireContext(), DetailUserActivity::class.java).putExtra(
                        DetailUserActivity.EXTRA_USER,
                        data
                    )
                )
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