package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import de.hdodenhof.circleimageview.CircleImageView

class ViewHolderShopPendingWallet(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var circleImageView: CircleImageView = itemView.findViewById(R.id.circleImageView)
    var textTitle: TextView = itemView.findViewById(R.id.textTitle)
    var textDate: TextView = itemView.findViewById(R.id.textDate)
    var textReference: TextView = itemView.findViewById(R.id.textReference)
    var textAmount: TextView = itemView.findViewById(R.id.textAmount)
    var textStatus: TextView = itemView.findViewById(R.id.textStatus)
}