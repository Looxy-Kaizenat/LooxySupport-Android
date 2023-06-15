package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterUserList
import com.looxy.looxysupport.data.UserListResponse
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.ConnectionDetector
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues
import com.looxy.looxysupport.utilities.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

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

    private val mCallBack: AdapterUserList.OnItemClick = this
    var listArray: MutableList<UserListResponse.DataUserList> = mutableListOf()
    lateinit var adapterUserList: AdapterUserList

    var searchParam: String = ""

    var pagenumber = 1
    var pageLimit = 10
    var loading = false
    var isCompleted: Boolean = false

    private var getDataClass: GetData = GetData()


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

        if(ConnectionDetector(context).checkForInternet()) {
            isCompleted = false
            loading = true
            pagenumber = 1
            getDataClass.execute()
        }
        else
            Toast.makeText(context, context.resources.getString(R.string.check_internet), Toast.LENGTH_LONG).show()
    }

    inner class GetData: CoroutineScope by MainScope()
    {
        lateinit var result: Response<UserListResponse.UserList>

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

                result = RetrofitHelper.getInstance().create(APICall.UserListApi::class.java)
                    .getResult(registerToken, pagination = "true", page_limit = pageLimit,
                        page_number = pagenumber, search_parm = searchParam)

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

    override fun onClickedItem(position: Int, list: UserListResponse.DataUserList?, status: Int) {
        if(status == 1)
            Toast.makeText(context, list?.name ?: "", Toast.LENGTH_SHORT).show()
    }

}