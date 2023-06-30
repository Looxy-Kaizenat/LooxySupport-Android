package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R

class ViewHolderMoneyRequested(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textName: TextView = itemView.findViewById(R.id.textName)
    var textId: TextView = itemView.findViewById(R.id.textId)
    var textAmount: TextView = itemView.findViewById(R.id.textAmount)
    var textSendMoney: TextView = itemView.findViewById(R.id.textSendMoney)
}