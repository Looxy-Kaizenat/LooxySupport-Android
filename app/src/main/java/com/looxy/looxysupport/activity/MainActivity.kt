package com.looxy.looxysupport.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues

class MainActivity : AppCompatActivity() {

    var context : Context = this@MainActivity
    lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader

    private var registerToken: String? = ""
    private var registerName: String? = ""
    private var registerMobile: String? = ""
    private var registerEmail: String? = ""
    private var registerImage: String? = ""

    lateinit var cardViewUserList: CardView
    lateinit var cardViewShopList: CardView
    lateinit var cardViewBookingList: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        cardViewUserList = findViewById(R.id.cardViewUserList)
        cardViewShopList = findViewById(R.id.cardViewShopList)
        cardViewBookingList = findViewById(R.id.cardViewBookingList)

        cardViewUserList.setOnClickListener { startActivity(Intent(context, ActivityUserList::class.java)) }

    }
}