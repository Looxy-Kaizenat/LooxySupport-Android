package com.looxy.looxysupport.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import de.hdodenhof.circleimageview.CircleImageView

class ViewHolderBookingUpcoming(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textShopName: TextView = itemView.findViewById(R.id.textShopName)
    var textBookingNumber: TextView = itemView.findViewById(R.id.textBookingNumber)
    var textPaymentType: TextView = itemView.findViewById(R.id.textPaymentType)
    var textTimeSlot: TextView = itemView.findViewById(R.id.textTimeSlot)
    var textShopPhone: TextView = itemView.findViewById(R.id.textShopPhone)
    var textDate: TextView = itemView.findViewById(R.id.textDate)
    var textSuperScript: TextView = itemView.findViewById(R.id.textSuperScript)
    var textMonthDay: TextView = itemView.findViewById(R.id.textMonthDay)
    var textUserName: TextView = itemView.findViewById(R.id.textUserName)
    var textUserPhone: TextView = itemView.findViewById(R.id.textUserPhone)
    var textServiceDeleted: TextView = itemView.findViewById(R.id.textServiceDeleted)
    var recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    var textAmount: TextView = itemView.findViewById(R.id.textAmount)
    var textOffer: TextView = itemView.findViewById(R.id.textOffer)
    var layoutTax: RelativeLayout = itemView.findViewById(R.id.layoutTax)
    var textTaxPercentage: TextView = itemView.findViewById(R.id.textTaxPercentage)
    var textTaxAmount: TextView = itemView.findViewById(R.id.textTaxAmount)
    var textPaidAmountStatus: TextView = itemView.findViewById(R.id.textPaidAmountStatus)
    var textTotalAmount: TextView = itemView.findViewById(R.id.textTotalAmount)
}