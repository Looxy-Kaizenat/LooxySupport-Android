package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R

class ViewHolderIncentiveUserList(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textName: TextView = itemView.findViewById(R.id.textName)
    var textPhone: TextView = itemView.findViewById(R.id.textPhone)
    var textEmail: TextView = itemView.findViewById(R.id.textEmail)
    var textShopName: TextView = itemView.findViewById(R.id.textShopName)
    var layoutMail: LinearLayout = itemView.findViewById(R.id.layoutMail)
    var layoutShop: LinearLayout = itemView.findViewById(R.id.layoutShop)
    var textViewQR: TextView = itemView.findViewById(R.id.textViewQR)
}