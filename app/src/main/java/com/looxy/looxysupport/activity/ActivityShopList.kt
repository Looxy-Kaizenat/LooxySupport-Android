package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterShopList
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.ConnectionDetector
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

class ActivityShopList : AppCompatActivity(), AdapterShopList.OnItemClick {

    var context : Context = this@ActivityShopList
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

    private val mCallBack: AdapterShopList.OnItemClick = this
    var listArray: MutableList<DataShopList.DataList> = mutableListOf()
    lateinit var adapterUserList: AdapterShopList

    var searchParam: String = ""

    var pagenumber = 1
    var pageLimit = 10
    var loading = false
    var isCompleted: Boolean = false

    private var getDataClass: GetData = GetData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_list)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        loader.show()

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.shop_list)
        imgBack.setOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerView)
        layoutNoData = findViewById(R.id.layoutNoData)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        layoutBottomToast = findViewById(R.id.layoutBottomToast)
        editSearch = findViewById(R.id.editSearch)

        layoutNoData.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        adapterUserList = AdapterShopList(context = context, data = listArray, mCallback = mCallBack)
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
        lateinit var result: Response<DataShopList.StatusCheck>

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

                result = RetrofitHelper.getInstance().create(APICall.ApiShopList::class.java)
                    .getResult(registerToken, pagination = "true", page_limit = pageLimit,
                        page_number = pagenumber, search_parm = searchParam, need_transaction = "no")

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

    override fun onClickedItem(position: Int, list: DataShopList.DataList?, status: Int) {
        if(status == 1)
        {
            startActivity(Intent(context, ActivityShopWallet::class.java)
                .putExtra("shopId", list?.shop_id)
                .putExtra("shopName", list?.name))
        }
    }
}