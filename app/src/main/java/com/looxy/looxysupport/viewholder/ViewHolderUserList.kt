package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R

class ViewHolderUserList(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var layoutMain: CardView = itemView.findViewById(R.id.layoutMain)
    var textName: TextView = itemView.findViewById(R.id.textName)
    var textPhone: TextView = itemView.findViewById(R.id.textPhone)
    var textEmail: TextView = itemView.findViewById(R.id.textEmail)
    var textGender: TextView = itemView.findViewById(R.id.textGender)
    var textTotalBookings: TextView = itemView.findViewById(R.id.textTotalBookings)
}