package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterMoneyRequested
import com.looxy.looxysupport.data.DataMoneyRequested
import com.looxy.looxysupport.data.DataSendMoneyToOwner
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.AkConvertClass
import com.looxy.looxysupport.utilities.ConnectionDetector
import com.looxy.looxysupport.utilities.GifLoader
import com.looxy.looxysupport.utilities.GlobalValues
import com.looxy.looxysupport.utilities.RetrofitHelper
import com.looxy.looxysupport.utilities.TokenExpired
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ActivityMoneyRequestedShops : AppCompatActivity(), AdapterMoneyRequested.OnItemClick {

    var context : Context = this@ActivityMoneyRequestedShops
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

    private val mCallBack: AdapterMoneyRequested.OnItemClick = this

    lateinit var dialog: Dialog

    var requestId = ""
    var paymentMode = ""
    var requestedAmt = ""
    var refNumber = ""
    var shopId = ""
    var ownerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money_requested_shops)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.money_requested)
        imgBack.setOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerView)
        layoutNoData = findViewById(R.id.layoutNoData)
        nestedScrollView = findViewById(R.id.nestedScrollView)

        layoutNoData.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        if(ConnectionDetector(context).checkForInternet()) {
            GetData().execute()
        }
        else
            Toast.makeText(context, context.resources.getString(R.string.check_internet), Toast.LENGTH_LONG).show()
    }

    inner class GetData: CoroutineScope by MainScope()
    {
        lateinit var result: Response<DataMoneyRequested.StatusCheck>

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

                result = RetrofitHelper.getInstance().create(APICall.ApiMoneyRequested::class.java)
                    .getResult(registerToken)

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

            try {

                when (status) {
                    "success" -> {
                        try {
                            if(result.body()?.moneyRequested != null)
                            {
                                val adapterMoneyRequested = AdapterMoneyRequested(context,
                                    result.body()?.moneyRequested!!, mCallBack)
                                recyclerView.adapter = adapterMoneyRequested
                            }
                            else
                                dataCompleted()
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

            }catch (e: JSONException)
            {
                Log.e("testing", "exception is $e")
            }
        }
    }

    fun dataCompleted()
    {
        layoutNoData.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    override fun onClickedItem(position: Int, list: DataMoneyRequested.DataList?, status: Int) {

        if(status == 1)
        {
            shopId = list?.shop_id.toString()
            requestId = list?.id.toString()
            ownerId = list?.owner_id.toString()
            requestedAmt = list?.amount.toString()
            dialogMoneyRequested()
        }
    }

    private fun dialogMoneyRequested()
    {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.dialog_send_money, null) as View

        dialog.setContentView(convertView)
        dialog.setCanceledOnTouchOutside(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutBookingMode: TextInputLayout = convertView.findViewById(R.id.layoutBookingMode)
        val editBookingMode: TextInputEditText = convertView.findViewById(R.id.editBookingMode)
        val layoutReferenceNumber: TextInputLayout = convertView.findViewById(R.id.layoutReferenceNumber)
        val editReferenceNumber: TextInputEditText = convertView.findViewById(R.id.editReferenceNumber)
        val textSubmit: TextView = convertView.findViewById(R.id.textSubmit)
        val textCancel: TextView = convertView.findViewById(R.id.textCancel)
        val textTitle: TextView = convertView.findViewById(R.id.textTitle)

        val strRs = context.resources.getString(R.string.Rs)
        val amount = "Enter transaction details of $strRs${AkConvertClass.decimalFormat1Digit2Decimal((requestedAmt ?: "0"))}"
        textTitle.text = amount

        textSubmit.setOnClickListener {
            layoutBookingMode.error = null
            layoutBookingMode.isErrorEnabled = false
            layoutReferenceNumber.error = null
            layoutReferenceNumber.isErrorEnabled = false

            paymentMode = editBookingMode.text.toString().trim()
            refNumber = editReferenceNumber.text.toString().trim()

            if(paymentMode.isEmpty())
                layoutBookingMode.error = getString(R.string.required)
            else if(refNumber.isEmpty())
                layoutReferenceNumber.error = getString(R.string.required)
            else
                UpdateSendMoney().execute()
        }

        textCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    inner class UpdateSendMoney: CoroutineScope by MainScope()
    {
        lateinit var result: Response<DataSendMoneyToOwner.StatusCheck>

        val coroutineScope = CoroutineScope(Dispatchers.Main)

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

                result = RetrofitHelper.getInstance().create(APICall.ApiSendMoneyToOwner::class.java)
                    .getResult(registerToken, request_id = requestId, payment_mode = paymentMode,
                        requested_amt = requestedAmt, ref_number = refNumber, shop_id = shopId, owner_id = ownerId)

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
        private fun onPostExecute(status: String) {

            layoutLoader.visibility = View.GONE

            try {

                when (status) {
                    "success" -> {
                        try {
                            Toast.makeText(context, result.body()?.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                            GetData().execute()
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                        }
                    }
                    "invalidToken" -> {
                        TokenExpired(context)
                    }
                    else -> Toast.makeText(context, result.body()?.message, Toast.LENGTH_SHORT).show()
                }

            }catch (e: JSONException)
            {
                Log.e("testing", "exception is $e")
            }
        }
    }
}