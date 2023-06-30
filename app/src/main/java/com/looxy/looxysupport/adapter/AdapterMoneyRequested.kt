package com.looxy.looxysupport.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataMoneyRequested
import com.looxy.looxysupport.data.DataUserList
import com.looxy.looxysupport.utilities.AkConvertClass
import com.looxy.looxysupport.viewholder.ViewHolderMoneyRequested
import com.looxy.looxysupport.viewholder.ViewHolderUserList

class AdapterMoneyRequested(
    private val context: Context,
    private val data: List<DataMoneyRequested.DataList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderMoneyRequested>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: DataMoneyRequested.DataList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMoneyRequested {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_money_requested, parent, false)
        return ViewHolderMoneyRequested(view)
    }

    override fun onBindViewHolder(holder: ViewHolderMoneyRequested, position: Int) {
        val list: DataMoneyRequested.DataList = data[position]

        val strRs = context.resources.getString(R.string.Rs)

        holder.textName.text = list.shop_name
        holder.textId.text = list.id

        val amount = strRs+ AkConvertClass.decimalFormat1Digit2Decimal((list.amount ?: "0"))
        holder.textAmount.text = amount

        holder.textSendMoney.setOnClickListener {
            mCallback.onClickedItem(position, list, 1)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}