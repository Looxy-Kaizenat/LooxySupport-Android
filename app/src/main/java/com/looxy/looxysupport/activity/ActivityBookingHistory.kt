package com.looxy.looxysupport.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.looxy.looxysupport.R
import com.looxy.looxysupport.fragment.FragmentBookingCancelled
import com.looxy.looxysupport.fragment.FragmentBookingCompleted
import com.looxy.looxysupport.fragment.FragmentBookingMissed
import com.looxy.looxysupport.fragment.FragmentBookingUpcoming
import com.looxy.looxysupport.viewpager.ViewPagerTabLayout

class ActivityBookingHistory : AppCompatActivity() {

    var context : Context = this@ActivityBookingHistory

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.booking_data)
        imgBack.setOnClickListener { onBackPressed() }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = ViewPagerTabLayout(this)
        adapter.addFragment(FragmentBookingUpcoming(), getString(R.string.upcoming))
        adapter.addFragment(FragmentBookingCompleted(), getString(R.string.completed))
        adapter.addFragment(FragmentBookingCancelled(), getString(R.string.cancelled))
        adapter.addFragment(FragmentBookingMissed(), getString(R.string.missed))

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.titleList[position]
        }.attach()
    }
}