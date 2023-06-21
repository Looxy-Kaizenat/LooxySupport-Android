package com.looxy.looxysupport.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterBookingUpcoming
import com.looxy.looxysupport.data.DataBookingHistory
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.ConnectionDetector
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues
import com.looxy.looxysupport.utilities.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class FragmentBookingCancelled : Fragment(), AdapterBookingUpcoming.OnItemClick {

    @get:JvmName("getAdapterContext") lateinit var context : Context
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

    private val mCallBack: AdapterBookingUpcoming.OnItemClick = this
    var listArray: MutableList<DataBookingHistory.DataList> = mutableListOf()
    lateinit var adapterUserList: AdapterBookingUpcoming

    var searchParam: String = ""

    var pagenumber = 1
    var pageLimit = 10
    var loading = false
    var isCompleted: Boolean = false

    private var getDataClass: GetData = GetData()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking_upcoming, container, false)

        context = requireContext()
        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = view.findViewById(R.id.layoutLoader)

        layoutLoader.visibility = View.VISIBLE

        val sharedPreference =  context.getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        recyclerView = view.findViewById(R.id.recyclerView)
        layoutNoData = view.findViewById(R.id.layoutNoData)
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        layoutBottomToast = view.findViewById(R.id.layoutBottomToast)
        editSearch = view.findViewById(R.id.editSearch)

        layoutNoData.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        adapterUserList = AdapterBookingUpcoming(context = context, data = listArray, mCallback = mCallBack)
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

        if(ConnectionDetector(context).checkForInternet()) {
            isCompleted = false
            loading = true
            pagenumber = 1
            getDataClass.execute()
        }
        else
            Toast.makeText(context, context.resources.getString(R.string.check_internet), Toast.LENGTH_LONG).show()

        return view
    }

    inner class GetData: CoroutineScope by MainScope()
    {
        lateinit var result: Response<DataBookingHistory.StatusCheck>

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

                result = RetrofitHelper.getInstance().create(APICall.ApiBookingHistory::class.java)
                    .getResult(registerToken, pagination = "true", booking_type = "online",
                        status = "canceled", services_status = "", page_limit = pageLimit,
                        page_number = pagenumber, search_parm = searchParam, from_date = "")

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

            layoutLoader.visibility = View.GONE
            layoutBottomToast.visibility = View.GONE
            layoutNoData.visibility = View.GONE

            try {

                when (status) {
                    "success" -> {
                        try {
                            listArray.addAll(result.body()?.booking_data!!)
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                            dataCompleted()
                        }
                    }
                    "invalidToken" -> {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
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

    override fun onClickedItem(position: Int, list: DataBookingHistory.DataList?, status: Int) {
        TODO("Not yet implemented")
    }
}