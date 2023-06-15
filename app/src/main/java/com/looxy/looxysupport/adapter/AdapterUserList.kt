package com.looxy.looxysupport.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.UserListResponse
import com.looxy.looxysupport.viewholder.ViewHolderUserList
import java.lang.Exception

class AdapterUserList(
    private val context: Context,
    private val data: List<UserListResponse.DataUserList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderUserList>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: UserListResponse.DataUserList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUserList {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_list, parent, false)
        return ViewHolderUserList(view)
    }

    override fun onBindViewHolder(holder: ViewHolderUserList, position: Int) {
        val list: UserListResponse.DataUserList = data[position]

        holder.textName.text = list.name
        holder.textPhone.text = list.phone
        holder.textTotalBookings.text = list.booking_count

        holder.layoutMain.setOnClickListener {
            mCallback.onClickedItem(position, list, 1)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}