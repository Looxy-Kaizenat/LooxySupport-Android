package com.looxy.looxysupport.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerTabLayout (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = ArrayList<Fragment>()
    val titleList = ArrayList<String>()

    // Add fragments to the adapter
    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
    }

    // Return the fragment at the specified position
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    // Return the number of fragments in the adapter
    override fun getItemCount(): Int {
        return fragmentList.size
    }
}