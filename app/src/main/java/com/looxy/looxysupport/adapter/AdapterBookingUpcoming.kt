package com.looxy.looxysupport.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataBookingHistory
import com.looxy.looxysupport.utilities.CommonUtils
import com.looxy.looxysupport.utilities.DateFormat
import com.looxy.looxysupport.utilities.NumberSuffixes
import com.looxy.looxysupport.viewholder.ViewHolderBookingUpcoming

class AdapterBookingUpcoming(
    private val context: Context,
    private val data: List<DataBookingHistory.DataList>,
    private val mCallback: OnItemClick
) : RecyclerView.Adapter<ViewHolderBookingUpcoming>() {

    interface OnItemClick {
        fun onClickedItem(position: Int, list: DataBookingHistory.DataList?, status: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBookingUpcoming {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_upcoming, parent, false)
        return ViewHolderBookingUpcoming(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderBookingUpcoming, position: Int) {
        val list: DataBookingHistory.DataList = data[position]

        val strRs = context.resources.getString(R.string.Rs)

        holder.textShopName.text = list.shop_name
        holder.textBookingNumber.text = "Booking # : ${list.booking_number}"
        holder.textPaymentType.text = "Payment mode : ${list.payment_mode}"
        holder.textShopPhone.text = "Phone : ${list.shop_phone}"

        if(!list.name.isNullOrEmpty()) {
            holder.textUserName.text = "User name : ${list.name}"
        }
        else
            holder.textUserName.text = context.getString(R.string.user_name_unknown)

        if(!list.phone.isNullOrEmpty()) {
            holder.textUserPhone.text = "User phone : ${list.phone}"
        }
        else
            holder.textUserPhone.text = context.getString(R.string.user_phone_unknown)

        val date = DateFormat.dateToOnlyDate(list.booking_date)
        holder.textSuperScript.text = NumberSuffixes[date]
        holder.textDate.text = date
        holder.textMonthDay.text = DateFormat.dateToMonthYearDay(list.booking_date)

        if (list.payment_mode.lowercase() == "cash")
            holder.textPaidAmountStatus.text = context.resources.getString(R.string.to_be_paid)

        holder.textAmount.text = strRs+list.net_price
        holder.textTotalAmount.text = strRs+list.final_price
        holder.textOffer.text = "- $strRs${list.offers_amount}"

        if(list.is_gst_applicable == "1")
        {
            holder.layoutTax.visibility = View.VISIBLE
            holder.textTaxAmount.text = strRs+list.tax
            holder.textTaxPercentage.text =
                "${context.resources.getString(R.string.tax_and_charges)} (${list.gst_per}%)"
        }
        else
            holder.layoutTax.visibility = View.GONE

        try {
            if(list.service_data.isNotEmpty())
            {
                holder.textServiceDeleted.visibility = View.GONE

                val layoutManager = LinearLayoutManager(context)
                holder.recyclerView.layoutManager = layoutManager
                val adapterBookingHistoryServices = AdapterBookingHistoryServices(context = context, data = list.service_data)
                holder.recyclerView.adapter = adapterBookingHistoryServices

                val starTimes = ArrayList<String>()
                val timeDurations = ArrayList<String>()
                for (item in list.service_data)
                {
                    starTimes.add(item.start_time)
                    timeDurations.add(item.time_duration)
                }
                val totalServiceTime: String = CommonUtils.sumOfServiceTimes(timeDurations)
                val firstServiceStartTime: String = CommonUtils.getMinimumTime(starTimes)
                val endTime: String = CommonUtils.addTimeFromGivenStartTime(totalServiceTime, firstServiceStartTime)
                holder.textTimeSlot.text = "${DateFormat.time24to12hoursFormat(firstServiceStartTime)}-${DateFormat.time24to12hoursFormat(endTime)}"
            }
            else {
                holder.textServiceDeleted.visibility = View.VISIBLE
                holder.textTimeSlot.text = "-"
            }
        }catch (e: Exception)
        {
            holder.textServiceDeleted.visibility = View.VISIBLE
            holder.textTimeSlot.text = "-"
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}