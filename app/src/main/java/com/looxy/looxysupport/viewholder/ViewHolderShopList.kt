package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R

class ViewHolderShopList(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var layoutMain: CardView = itemView.findViewById(R.id.layoutMain)
    var textName: TextView = itemView.findViewById(R.id.textName)
    var layoutSalonPhones: LinearLayout = itemView.findViewById(R.id.layoutSalonPhones)
    var textOwnerName: TextView = itemView.findViewById(R.id.textOwnerName)
    var textOwnerPhone: TextView = itemView.findViewById(R.id.textOwnerPhone)
    var textTotalBookings: TextView = itemView.findViewById(R.id.textTotalBookings)
    var textWalletAmount: TextView = itemView.findViewById(R.id.textWalletAmount)
    var textRedeemStatus: TextView = itemView.findViewById(R.id.textRedeemStatus)
}