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
import com.looxy.looxysupport.viewholder.ViewHolderShopWallet

class AdapterShopWallet(
    private val context: Context,
    private val data: List<DataShopList.TransactionList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderShopWallet>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: DataShopList.TransactionList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShopWallet {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_wallet, parent, false)
        return ViewHolderShopWallet(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderShopWallet, position: Int) {
        val list: DataShopList.TransactionList = data[position]

        val strRs = context.resources.getString(R.string.Rs)

        holder.textDate.text = DateFormat.dateWithTimeToDateWithTimePipeFormat(list.date)
        holder.textReference.text = "Ref. no : ${list.ref_number}"
        holder.textTitle.text = list.particulars

        when (list.type) {
            "1" -> {
                holder.circleImageView.setImageResource(R.drawable.credit)
                holder.textAmount.text = "+" + strRs + AkConvertClass.decimalFormat(list.amount)
                holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                holder.textType.text = context.getString(R.string.credited)
            }
            "2" -> {
                holder.circleImageView.setImageResource(R.drawable.debit)
                holder.textAmount.text = "-" + strRs + AkConvertClass.decimalFormat(list.amount)
                holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.red_400))
                holder.textType.text = context.getString(R.string.debited)
            }
            "3" -> {
                holder.circleImageView.setImageResource(R.drawable.bank_icon)
                holder.textAmount.text = "-" + strRs + AkConvertClass.decimalFormat(list.amount)
                holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.blue))
                holder.textType.text = context.getString(R.string.transferred)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}