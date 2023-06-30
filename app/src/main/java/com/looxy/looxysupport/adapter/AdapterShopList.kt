package com.looxy.looxysupport.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.utilities.AkConvertClass
import com.looxy.looxysupport.viewholder.ViewHolderShopList


class AdapterShopList(
    private val context: Context,
    private val data: List<DataShopList.DataList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderShopList>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: DataShopList.DataList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShopList {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_list, parent, false)
        return ViewHolderShopList(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolderShopList, position: Int) {
        val list: DataShopList.DataList = data[position]

        val strRs = context.resources.getString(R.string.Rs)

        holder.textName.text = list.name
        holder.textTotalBookings.text = list.booking_count

        if (!list.onwer_name.isNullOrEmpty())
            holder.textOwnerName.text = list.onwer_name
        else
            holder.textOwnerName.text = "-"

        if(!list.owner_phone.isNullOrEmpty()) {
            holder.textOwnerPhone.text = list.owner_phone
            val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.call_icon)
            holder.textOwnerPhone.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            holder.textOwnerPhone.compoundDrawablePadding = 10

            holder.textOwnerPhone.setOnClickListener {
                val strCall: String = list.owner_phone
                val number = Uri.parse("tel:$strCall")
                val callIntent = Intent(Intent.ACTION_DIAL, number)
                context.startActivity(callIntent)
            }
        }
        else
            holder.textOwnerPhone.text = "-"

        holder.layoutSalonPhones.removeAllViews()
        if(!list.shop_phone.isNullOrEmpty())
        {
            val phones = list.shop_phone.split(",")
            for (item in phones)
            {
                val textView = TextView(context)
                textView.text = item
                textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                textView.textSize = 13f

                val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.call_icon)
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                textView.compoundDrawablePadding = 10

                textView.setTextAppearance(R.style.bold)

                if(holder.layoutSalonPhones.childCount > 0)
                {
                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 20, 0, 0)
                    textView.layoutParams = params
                }

                textView.setOnClickListener {
                    val strCall: String = item
                    val number = Uri.parse("tel:$strCall")
                    val callIntent = Intent(Intent.ACTION_DIAL, number)
                    context.startActivity(callIntent)
                }

                holder.layoutSalonPhones.addView(textView)
            }
        }
        else
        {
            val textView = TextView(context)
            textView.text = "-"
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
            textView.textSize = 13f

            textView.setTextAppearance(R.style.bold)

            holder.layoutSalonPhones.addView(textView)
        }

        val amount = strRs+ AkConvertClass.decimalFormat1Digit2Decimal((list.amount ?: "0"))
        holder.textWalletAmount.text = amount

        var checkPending = false
        try {
            for (item in list.redeem_details)
            {
                if(item.status == "1")
                {
                    checkPending = true
                    break
                }
            }
        }catch (e: Exception)
        {
            Log.e("testing", "Redeem Exception is $e")
        }

        if (checkPending)
        {
            holder.textRedeemStatus.text = context.getString(R.string.pending)
            holder.textRedeemStatus.setTextColor(ContextCompat.getColor(context, R.color.red_500))
        }
        else
            holder.textRedeemStatus.text = "-"

        holder.layoutMain.setOnClickListener {
            mCallback.onClickedItem(position, list, 1)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}