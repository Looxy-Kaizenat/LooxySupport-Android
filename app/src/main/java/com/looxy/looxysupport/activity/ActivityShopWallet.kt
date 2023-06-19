package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterShopList
import com.looxy.looxysupport.adapter.AdapterShopPendingWallet
import com.looxy.looxysupport.adapter.AdapterShopWallet
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.ConnectionDetector
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues
import com.looxy.looxysupport.utilities.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ActivityShopWallet : AppCompatActivity(), AdapterShopWallet.OnItemClick, AdapterShopPendingWallet.OnItemClick {

    var context : Context = this@ActivityShopWallet
    private lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader
    lateinit var layoutLoader: LinearLayout

    private var registerToken: String = ""
    private var registerName: String = ""
    private var registerMobile: String = ""
    private var registerEmail: String = ""
    private var registerImage: String = ""

    lateinit var textWallet: TextView
    lateinit var layoutRedeem: LinearLayout
    lateinit var recyclerViewPending: RecyclerView
    lateinit var layoutTransaction: LinearLayout
    lateinit var recyclerView: RecyclerView

    var shopId = ""
    var shopName = ""

    private val mCallBack: AdapterShopWallet.OnItemClick = this
    private val mCallBack1: AdapterShopPendingWallet.OnItemClick = this

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_wallet)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        try {
            shopId = intent.extras!!.getString("shopId").toString()
            shopName = intent.extras!!.getString("shopName").toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = shopName
        imgBack.setOnClickListener { onBackPressed() }

        textWallet = findViewById(R.id.textWallet)
        layoutRedeem = findViewById(R.id.layoutRedeem)
        recyclerViewPending = findViewById(R.id.recyclerViewPending)
        layoutTransaction = findViewById(R.id.layoutTransaction)
        recyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val layoutManager1 = LinearLayoutManager(context)
        recyclerViewPending.layoutManager = layoutManager1

        if(ConnectionDetector(context).checkForInternet())
            GetData().execute()
        else
            Toast.makeText(context, context.resources.getString(R.string.check_internet), Toast.LENGTH_LONG).show()
    }

    inner class GetData: CoroutineScope by MainScope()
    {
        private lateinit var result: Response<DataShopList.StatusCheck>

        private val coroutineScope = CoroutineScope(Dispatchers.Main)

        fun execute() = coroutineScope.launch {
            onPreExecute()
            val status = doInBackground()
            onPostExecute(status)
        }

        private fun onPreExecute() {
            layoutLoader.visibility = View.VISIBLE
        }

        private suspend fun doInBackground(): String = withContext(Dispatchers.IO) {

            var status = ""

            try {

                result = RetrofitHelper.getInstance().create(APICall.ApiShopDetails::class.java)
                    .getResult(registerToken, shop_id = shopId, need_transaction = "yes")

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

            try {

                when (status) {
                    "success" -> {

                        val strRs = context.resources.getString(R.string.Rs)

                        val list: DataShopList.DataList = result.body()?.response!![0]

                        val amount = strRs+ (list.amount ?: "0")
                        textWallet.text = amount

                        try {
                            val arrayList = list.transaction_details
                            val adapterShopWallet = AdapterShopWallet(context = context, data = arrayList, mCallback = mCallBack)
                            recyclerView.adapter = adapterShopWallet
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                            layoutTransaction.visibility = View.GONE
                        }

                        try {
                            val arrayList = list.redeem_details
                            val adapterShopPendingWallet = AdapterShopPendingWallet(context = context, data = arrayList, mCallback = mCallBack1)
                            recyclerViewPending.adapter = adapterShopPendingWallet
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                            layoutRedeem.visibility = View.GONE
                        }
                    }
                    "invalidToken" -> {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        dataCompleted()
                    }
                    else -> dataCompleted()
                }

            }catch (e: JSONException)
            {
                Log.e("testing", "exception is $e")
            }

        }
    }

    fun dataCompleted()
    {
        layoutTransaction.visibility = View.GONE
        layoutRedeem.visibility = View.GONE
    }

    override fun onClickedItem(position: Int, list: DataShopList.RedeemList?, status: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickedItem(position: Int, list: DataShopList.TransactionList?, status: Int) {
        TODO("Not yet implemented")
    }
}