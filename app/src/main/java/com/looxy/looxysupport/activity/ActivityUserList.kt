package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterUserList
import com.looxy.looxysupport.data.DataUserList
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.ConnectionDetector
import com.looxy.looxysupport.utilities.DateFormat
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues
import com.looxy.looxysupport.utilities.RetrofitHelper
import com.looxy.looxysupport.utilities.TokenExpired
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ActivityUserList : AppCompatActivity(), AdapterUserList.OnItemClick{

    var context : Context = this@ActivityUserList
    private lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader
    lateinit var layoutLoader: LinearLayout

    private var registerToken: String = ""
    private var registerName: String = ""
    private var registerMobile: String = ""
    private var registerEmail: String = ""
    private var registerImage: String = ""

    private lateinit var recyclerView: RecyclerView
    lateinit var layoutNoData: LinearLayout
    private lateinit var nestedScrollView: NestedScrollView
    lateinit var layoutBottomToast: LinearLayout
    private lateinit var editSearch: EditText
    lateinit var layoutFromDate: TextInputLayout
    lateinit var autoFromDate: AutoCompleteTextView
    lateinit var layoutToDate: TextInputLayout
    lateinit var autoToDate: AutoCompleteTextView
    lateinit var imgSubmit: ImageView

    private val mCallBack: AdapterUserList.OnItemClick = this
    var listArray: MutableList<DataUserList.DataList> = mutableListOf()
    lateinit var adapterUserList: AdapterUserList

    var searchParam: String = ""

    var pagenumber = 1
    var pageLimit = 10
    var loading = false
    var isCompleted: Boolean = false
    private var getDataClass: GetData = GetData()

    var fromDate: String = ""
    var toDate: String = ""
    private lateinit var startDateCalendar: Calendar
    private lateinit var endDateCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        loader.show()

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.user_list)
        imgBack.setOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerView)
        layoutNoData = findViewById(R.id.layoutNoData)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        layoutBottomToast = findViewById(R.id.layoutBottomToast)
        editSearch = findViewById(R.id.editSearch)
        layoutFromDate = findViewById(R.id.layoutFromDate)
        autoFromDate = findViewById(R.id.autoFromDate)
        layoutToDate = findViewById(R.id.layoutToDate)
        autoToDate = findViewById(R.id.autoToDate)
        imgSubmit = findViewById(R.id.imgSubmit)

        layoutNoData.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        adapterUserList = AdapterUserList(context = context, data = listArray, mCallback = mCallBack)
        recyclerView.adapter = adapterUserList

        editSearch.addTextChangedListener {
            searchParam = editSearch.text.toString().trim()

            getDataClass.coroutineScope.cancel()

            listArray.clear()
            isCompleted = false
            loading = true
            pagenumber = 1

            getDataClass = GetData()
            getDataClass.execute()
        }

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY + 500 >= v.getChildAt(0).measuredHeight - v.measuredHeight && !isCompleted && !loading) {
                pagenumber++
                loading = true
                getDataClass.execute()
            }
        })

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
            else {
                getDataClass.coroutineScope.cancel()

                listArray.clear()
                isCompleted = false
                loading = true
                pagenumber = 1

                getDataClass = GetData()
                getDataClass.execute()
            }
        }
//        from and to date ends here

        if(ConnectionDetector(context).checkForInternet()) {
            isCompleted = false
            loading = true
            pagenumber = 1
            getDataClass.execute()
        }
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
        lateinit var result: Response<DataUserList.StatusCheck>

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        fun execute() = coroutineScope.launch {
            onPreExecute()
            val status = doInBackground()
            onPostExecute(status)
        }

        private fun onPreExecute() {
            layoutBottomToast.visibility = View.VISIBLE
        }

        private suspend fun doInBackground(): String = withContext(Dispatchers.IO) {

            var status = ""

            try {

                result = RetrofitHelper.getInstance().create(APICall.ApiUserList::class.java)
                    .getResult(registerToken, pagination = "true", page_limit = pageLimit,
                        page_number = pagenumber, search_parm = searchParam, from_date = fromDate, to_date = toDate)

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
        @SuppressLint("NotifyDataSetChanged")
        private fun onPostExecute(status: String) {

            loader.dismiss()
            layoutBottomToast.visibility = View.GONE
            layoutNoData.visibility = View.GONE

            try {

                when (status) {
                    "success" -> {
                        try {
                            listArray.addAll(result.body()?.response!!)
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                            dataCompleted()
                        }
                    }
                    "invalidToken" -> {
                        TokenExpired(context)
                        dataCompleted()
                    }
                    else -> dataCompleted()
                }

                adapterUserList.notifyDataSetChanged()

            }catch (e: JSONException)
            {
                Log.e("testing", "exception is $e")
            }

            loading = false
        }
    }

    fun dataCompleted()
    {
        isCompleted = true
        if(pagenumber == 1)
            layoutNoData.visibility = View.VISIBLE
    }

    override fun onClickedItem(position: Int, list: DataUserList.DataList?, status: Int) {
        if(status == 1) {
//            Toast.makeText(context, list?.name ?: "", Toast.LENGTH_SHORT).show()
        }
    }

}