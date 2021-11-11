package com.dicoding.submission_3

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FollowPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var username: String? = null


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FollowerFragment.newInstance(0, username.toString())
            1 -> fragment = FollowingFragment.newInstance(1, username.toString())
        }
        return fragment as Fragment
    }
}