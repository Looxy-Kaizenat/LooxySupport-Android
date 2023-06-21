package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import de.hdodenhof.circleimageview.CircleImageView

class ViewHolderBookingHistoryServices(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textService: TextView = itemView.findViewById(R.id.textService)
    var textAmount: TextView = itemView.findViewById(R.id.textAmount)
}