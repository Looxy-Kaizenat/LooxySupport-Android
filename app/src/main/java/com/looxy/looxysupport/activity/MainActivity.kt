package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputLayout
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataDashboard
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.AkConvertClass
import com.looxy.looxysupport.utilities.ConnectionDetector
import com.looxy.looxysupport.utilities.DateFormat
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues
import com.looxy.looxysupport.utilities.RetrofitHelper
import com.looxy.looxysupport.utilities.TokenExpired
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    var context : Context = this@MainActivity
    private lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader

    private var registerToken: String = ""
    private var registerName: String = ""
    private var registerMobile: String = ""
    private var registerEmail: String = ""
    private var registerImage: String = ""

    lateinit var cardViewUserList: CardView
    private lateinit var cardViewShopList: CardView
    private lateinit var cardViewBookingList: CardView
    lateinit var layoutFromDate: TextInputLayout
    lateinit var autoFromDate: AutoCompleteTextView
    lateinit var layoutToDate: TextInputLayout
    lateinit var autoToDate: AutoCompleteTextView
    lateinit var imgSubmit: ImageView
    lateinit var textLastWeekUser: TextView
    lateinit var textLastWeekUserCount: TextView
    lateinit var textTotalUserCount: TextView
    lateinit var textLastWeekShop: TextView
    lateinit var textLastWeekShopCount: TextView
    lateinit var textTotalShopsCount: TextView
    lateinit var textLastWeekBooking: TextView
    lateinit var textLastWeekBookingCount: TextView
    lateinit var textTotalBookingCount: TextView
    lateinit var textLastWeekOnlineBooking: TextView
    lateinit var textLastWeekOnlineBookingCount: TextView
    lateinit var textTotalOnlineBookingCount: TextView
    lateinit var textLastWeekAmount: TextView
    lateinit var textLastWeekAmountValue: TextView
    lateinit var textTotalAmountValue: TextView
    lateinit var textLastWeekOfferAmount: TextView
    lateinit var textLastWeekOfferAmountValue: TextView
    lateinit var textTotalOfferAmountValue: TextView
    lateinit var cardViewPendingRequest: CardView
    lateinit var textPendingRequest: TextView
    lateinit var textLastWeekOnlineBookingAmount: TextView
    lateinit var textLastWeekOnlineBookingCountAmount: TextView
    lateinit var textTotalOnlineBookingCountAmount: TextView

    var fromDate: String = ""
    var toDate: String = ""
    private lateinit var startDateCalendar: Calendar
    private lateinit var endDateCalendar: Calendar
    var finalFromDate = ""
    var finalToDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        cardViewUserList = findViewById(R.id.cardViewUserList)
        cardViewShopList = findViewById(R.id.cardViewShopList)
        cardViewBookingList = findViewById(R.id.cardViewBookingList)
        layoutFromDate = findViewById(R.id.layoutFromDate)
        autoFromDate = findViewById(R.id.autoFromDate)
        layoutToDate = findViewById(R.id.layoutToDate)
        autoToDate = findViewById(R.id.autoToDate)
        imgSubmit = findViewById(R.id.imgSubmit)
        textLastWeekUser = findViewById(R.id.textLastWeekUser)
        textLastWeekUserCount = findViewById(R.id.textLastWeekUserCount)
        textTotalUserCount = findViewById(R.id.textTotalUserCount)
        textLastWeekShop = findViewById(R.id.textLastWeekShop)
        textLastWeekShopCount = findViewById(R.id.textLastWeekShopCount)
        textTotalShopsCount = findViewById(R.id.textTotalShopsCount)
        textLastWeekBooking = findViewById(R.id.textLastWeekBooking)
        textLastWeekBookingCount = findViewById(R.id.textLastWeekBookingCount)
        textTotalBookingCount = findViewById(R.id.textTotalBookingCount)
        textLastWeekOnlineBooking = findViewById(R.id.textLastWeekOnlineBooking)
        textLastWeekOnlineBookingCount = findViewById(R.id.textLastWeekOnlineBookingCount)
        textTotalOnlineBookingCount = findViewById(R.id.textTotalOnlineBookingCount)
        textLastWeekAmount = findViewById(R.id.textLastWeekAmount)
        textLastWeekAmountValue = findViewById(R.id.textLastWeekAmountValue)
        textTotalAmountValue = findViewById(R.id.textTotalAmountValue)
        textLastWeekOfferAmount = findViewById(R.id.textLastWeekOfferAmount)
        textLastWeekOfferAmountValue = findViewById(R.id.textLastWeekOfferAmountValue)
        textTotalOfferAmountValue = findViewById(R.id.textTotalOfferAmountValue)
        cardViewPendingRequest = findViewById(R.id.cardViewPendingRequest)
        textPendingRequest = findViewById(R.id.textPendingRequest)
        textLastWeekOnlineBookingAmount = findViewById(R.id.textLastWeekOnlineBookingAmount)
        textLastWeekOnlineBookingCountAmount = findViewById(R.id.textLastWeekOnlineBookingCountAmount)
        textTotalOnlineBookingCountAmount = findViewById(R.id.textTotalOnlineBookingCountAmount)

        cardViewUserList.setOnClickListener { startActivity(Intent(context, ActivityUserList::class.java)) }
        cardViewShopList.setOnClickListener { startActivity(Intent(context, ActivityShopList::class.java)) }
        cardViewBookingList.setOnClickListener { startActivity(Intent(context, ActivityBookingHistory::class.java)) }

        cardViewPendingRequest.setOnClickListener { startActivity(Intent(context, ActivityMoneyRequestedShops::class.java)) }

//        from and to date starts here
        startDateCalendar = Calendar.getInstance()
        endDateCalendar = Calendar.getInstance()
        val currentDate = Calendar.getInstance()
        startDateCalendar.set(
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        endDateCalendar.set(
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        autoFromDate.setOnClickListener {
            showDatePickerDialog(startDateCalendar) { selectedDate ->
                if (selectedDate <= endDateCalendar) {
                    startDateCalendar = selectedDate
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fromDate = dateFormat.format(startDateCalendar.time)
                    autoFromDate.setText(DateFormat.dateToDate(fromDate))
                } else {
                    Toast.makeText(context, getString(R.string.from_date_should_be_less_than_to_date), Toast.LENGTH_SHORT).show()
                }
            }
        }
        autoToDate.setOnClickListener {
            showDatePickerDialog(endDateCalendar) { selectedDate ->
                if (selectedDate >= startDateCalendar) {
                    endDateCalendar = selectedDate
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    toDate = dateFormat.format(endDateCalendar.time)
                    autoToDate.setText(DateFormat.dateToDate(toDate))
                } else {
                    Toast.makeText(context, getString(R.string.to_date_should_be_greater_than_start_date), Toast.LENGTH_SHORT).show()
                }
            }
        }
        imgSubmit.setOnClickListener {
            layoutFromDate.error = null
            layoutFromDate.isErrorEnabled = false
            layoutToDate.error = null
            layoutToDate.isErrorEnabled = false
            if(fromDate.isEmpty())
                layoutFromDate.error = getString(R.string.required)
            else if(toDate.isEmpty())
                layoutToDate.error = getString(R.string.required)
            else
                GetData().execute()
        }
//        from and to date ends here

        if(ConnectionDetector(context).checkForInternet())
            GetData().execute()
        else
            Toast.makeText(context, context.resources.getString(R.string.check_internet), Toast.LENGTH_LONG).show()
    }

    private fun showDatePickerDialog(calendar: Calendar, onDateSelected: (Calendar) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                onDateSelected(calendar)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    inner class GetData: CoroutineScope by MainScope()
    {
        lateinit var result: Response<DataDashboard.StatusCheck>

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        fun execute() = coroutineScope.launch {
            onPreExecute()
            val status = doInBackground()
            onPostExecute(status)
        }

        private fun onPreExecute() {
            loader.show()
        }

        private suspend fun doInBackground(): String = withContext(Dispatchers.IO) {

            var status = ""

            try {

                if(fromDate.isEmpty() && toDate.isEmpty())
                    getLastWeekDate()
                else
                {
                    finalFromDate = fromDate
                    finalToDate = toDate
                }

                result = RetrofitHelper.getInstance().create(APICall.ApiDashBoard::class.java)
                    .getResult(registerToken, from_date = finalFromDate, to_date = finalToDate)

                if(result.isSuccessful) {
                    status = result.body()?.status ?: ""
                    Log.e("testing", "result is ${result.body().toString()}")
                }
                else
                {
                    val jsonObject = result.errorBody()?.string()?.let { JSONObject(it) }
                    status = jsonObject?.optString("status") ?: ""
                    Log.e("testing", "result is ${jsonObject.toString()}")
                }

                return@withContext status
            }catch (e: Exception)
            {
                Log.e("testing", "Exception is $e")
                return@withContext status
            }

        }
        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        private fun onPostExecute(status: String) {

            loader.dismiss()

            try {

                when (status) {
                    "success" -> {
                        try {

                            val strRs = context.resources.getString(R.string.Rs)

                            if(fromDate.isEmpty() && toDate.isEmpty())
                            {
                                textLastWeekUser.text = context.resources.getString(R.string.last_week_users_colon)
                                textLastWeekShop.text = context.resources.getString(R.string.last_week_shops_colon)
                                textLastWeekBooking.text = context.resources.getString(R.string.last_week_bookings_colon)
                                textLastWeekOnlineBooking.text = context.resources.getString(R.string.last_week_online_booking_colon)
                                textLastWeekAmount.text = context.resources.getString(R.string.last_week_amount_colon)
                                textLastWeekOfferAmount.text = context.resources.getString(R.string.last_week_offer_amount_colon)
                                textLastWeekOnlineBookingAmount.text = context.resources.getString(R.string.last_week_online_amount_colon)
                            }
                            else
                            {
                                textLastWeekUser.text = context.resources.getString(R.string.users_colon)
                                textLastWeekShop.text = context.resources.getString(R.string.shops_colon)
                                textLastWeekBooking.text = context.resources.getString(R.string.bookings_colon)
                                textLastWeekOnlineBooking.text = context.resources.getString(R.string.online_booking_colon)
                                textLastWeekAmount.text = context.resources.getString(R.string.amount_colon)
                                textLastWeekOfferAmount.text = context.resources.getString(R.string.offer_amount_colon)
                                textLastWeekOnlineBookingAmount.text = context.resources.getString(R.string.online_amount_colon)
                            }

                            textLastWeekUserCount.text = result.body()?.weekUser
                            textTotalUserCount.text = result.body()?.allUser

                            textLastWeekShopCount.text = result.body()?.weekSalons
                            textTotalShopsCount.text = result.body()?.allSalons

                            textLastWeekBookingCount.text = result.body()?.weekBookings
                            textTotalBookingCount.text = result.body()?.allBookings

                            textLastWeekOnlineBookingCount.text = result.body()?.weekOnlineBookings
                            textTotalOnlineBookingCount.text = result.body()?.allOnlineBookings

                            textLastWeekOnlineBookingCountAmount.text = strRs+ AkConvertClass.decimalFormat1Digit2Decimal(result.body()?.weekOnlineValue ?:"0")
                            textTotalOnlineBookingCountAmount.text = strRs+ AkConvertClass.decimalFormat1Digit2Decimal(result.body()?.allOnlineValue ?:"0")

                            textLastWeekAmountValue.text = strRs+ AkConvertClass.decimalFormat1Digit2Decimal(result.body()?.weekValue ?:"0")
                            textTotalAmountValue.text = strRs+ AkConvertClass.decimalFormat1Digit2Decimal(result.body()?.allValue ?:"0")

                            textLastWeekOfferAmountValue.text = strRs+ AkConvertClass.decimalFormat1Digit2Decimal(result.body()?.weekOffer ?:"0")
                            textTotalOfferAmountValue.text = strRs+ AkConvertClass.decimalFormat1Digit2Decimal(result.body()?.allOffer ?:"0")

                            textPendingRequest.text = result.body()?.moneyRequested
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                        }
                    }
                    "invalidToken" -> {
                        TokenExpired(context)
                    }
                    else -> Toast.makeText(context, getString(R.string.server_error_please_try_again_after_sometime), Toast.LENGTH_SHORT).show()
                }

            }catch (e: JSONException)
            {
                Log.e("testing", "exception is $e")
            }

        }
    }

    fun getLastWeekDate() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.firstDayOfWeek = Calendar.SUNDAY

        calendar.add(Calendar.WEEK_OF_YEAR, -1)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedStartDate = dateFormat.format(startDate)
        val formattedEndDate = dateFormat.format(endDate)

        finalFromDate = formattedStartDate
        finalToDate = formattedEndDate
    }
}