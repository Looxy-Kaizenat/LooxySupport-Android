package com.looxy.looxysupport.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataBookingHistory
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.viewholder.ViewHolderBookingHistoryServices
import com.looxy.looxysupport.viewholder.ViewHolderShopList


class AdapterBookingHistoryServices(
    private val context: Context,
    private val data: List<DataBookingHistory.ServiceList>,
) : RecyclerView.Adapter<ViewHolderBookingHistoryServices>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBookingHistoryServices {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_history_services, parent, false)
        return ViewHolderBookingHistoryServices(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolderBookingHistoryServices, position: Int) {
        val list: DataBookingHistory.ServiceList = data[position]

        val strRs = context.resources.getString(R.string.Rs);

        holder.textService.text = list.service_name
        holder.textAmount.text = strRs+list.service_final_price
    }

    override fun getItemCount(): Int {
        return data.size
    }
}