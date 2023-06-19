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
import com.looxy.looxysupport.data.DataUserList
import com.looxy.looxysupport.viewholder.ViewHolderUserList

class AdapterUserList(
    private val context: Context,
    private val data: List<DataUserList.DataList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderUserList>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: DataUserList.DataList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUserList {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_list, parent, false)
        return ViewHolderUserList(view)
    }

    override fun onBindViewHolder(holder: ViewHolderUserList, position: Int) {
        val list: DataUserList.DataList = data[position]

        holder.textName.text = list.name
        holder.textTotalBookings.text = list.booking_count
        holder.textGender.text = list.gender

        if(!list.phone.isNullOrEmpty()) {
            holder.textPhone.text = list.phone
            val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.call_icon)
            holder.textPhone.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            holder.textPhone.compoundDrawablePadding = 10

            holder.textPhone.setOnClickListener {
                val strCall: String = list.phone
                val number = Uri.parse("tel:$strCall")
                val callIntent = Intent(Intent.ACTION_DIAL, number)
                context.startActivity(callIntent)
            }
        }
        else
            holder.textPhone.text = "-"

        if(!list.email.isNullOrEmpty()) {
            holder.textEmail.text = list.email
            val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.mail_icon)
            holder.textEmail.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            holder.textEmail.compoundDrawablePadding = 10

            holder.textEmail.setOnClickListener {

                val subject = context.resources.getString(R.string.app_name)
                val preText = "Hello ${list.name},"

                val strMail: String = list.email
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "text/plain"
                emailIntent.data = Uri.parse("mailto:")
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(strMail))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                emailIntent.putExtra(Intent.EXTRA_TEXT, preText)
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }
        }
        else
            holder.textEmail.text = "-"

        holder.layoutMain.setOnClickListener {
            mCallback.onClickedItem(position, list, 1)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}