package com.looxy.looxysupport.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.utilities.AkConvertClass
import com.looxy.looxysupport.utilities.DateFormat
import com.looxy.looxysupport.viewholder.ViewHolderShopPendingWallet

class AdapterShopPendingWallet(
    private val context: Context,
    private val data: List<DataShopList.RedeemList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderShopPendingWallet>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: DataShopList.RedeemList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShopPendingWallet {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_pending_wallet, parent, false)
        return ViewHolderShopPendingWallet(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderShopPendingWallet, position: Int) {
        val list: DataShopList.RedeemList = data[position]

        val strRs = context.resources.getString(R.string.Rs)

        holder.textReference.text = "Ref. no : ${list.reference_number}"
        holder.textAmount.text = strRs + AkConvertClass.decimalFormat(list.amount)

        when (list.status) {
            "1" -> {
                holder.textTitle.text = context.getString(R.string.your_amount_will_reach_you_in_3_4_working_days)
                holder.textStatus.text = context.getString(R.string.pending)
                holder.textStatus.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                holder.textDate.text = context.getString(R.string.requested_on) +DateFormat.dateWithTimeToDateWithTimePipeFormat(list.requested_on)
            }
            "3" -> {
                holder.textTitle.text = list.reject_reason
                holder.textStatus.text = context.getString(R.string.rejected)
                holder.textStatus.setTextColor(ContextCompat.getColor(context, R.color.red_400))
                holder.textDate.text = context.getString(R.string.updated_on) +DateFormat.dateWithTimeToDateWithTimePipeFormat(list.updated_on)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}