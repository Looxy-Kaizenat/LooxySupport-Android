package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataGenerateIncentiveQR
import com.looxy.looxysupport.data.DataSendMoneyToOwner
import com.looxy.looxysupport.data.DataShopList
import com.looxy.looxysupport.utilities.APICall
import com.looxy.looxysupport.utilities.CommonUtils
import com.looxy.looxysupport.utilities.ConnectionDetector
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
import java.io.File
import java.io.FileOutputStream

class ActivityCreateIncentiveUser : AppCompatActivity() {

    var context : Context = this@ActivityCreateIncentiveUser
    private lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader
    lateinit var layoutLoader: LinearLayout

    private var registerToken: String = ""
    private var registerName: String = ""
    private var registerMobile: String = ""
    private var registerEmail: String = ""
    private var registerImage: String = ""

    private lateinit var layoutName: TextInputLayout
    private lateinit var editName: TextInputEditText
    private lateinit var layoutPhone: TextInputLayout
    private lateinit var editPhone: TextInputEditText
    private lateinit var layoutEmail: TextInputLayout
    private lateinit var editEmail: TextInputEditText
    private lateinit var layoutShop: TextInputLayout
    private lateinit var autoShop: AutoCompleteTextView
    private lateinit var textSubmit: TextView
    private lateinit var layoutQRCode: LinearLayout
    private lateinit var imgQRCode: ImageView
    private lateinit var textShare: TextView
    private lateinit var layoutQRCodeShareContent: LinearLayout

    var name = ""
    var email = ""
    var phone = ""
    var shopName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_incentive_user)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.create)
        imgBack.setOnClickListener { onBackPressed() }

        layoutName = findViewById(R.id.layoutName)
        editName = findViewById(R.id.editName)
        layoutPhone = findViewById(R.id.layoutPhone)
        editPhone = findViewById(R.id.editPhone)
        layoutEmail = findViewById(R.id.layoutEmail)
        editEmail = findViewById(R.id.editEmail)
        layoutShop = findViewById(R.id.layoutShop)
        autoShop = findViewById(R.id.autoShop)
        textSubmit = findViewById(R.id.textSubmit)
        layoutQRCode = findViewById(R.id.layoutQRCode)
        imgQRCode = findViewById(R.id.imgQRCode)
        textShare = findViewById(R.id.textShare)
        layoutQRCodeShareContent = findViewById(R.id.layoutQRCodeShareContent)

        layoutQRCode.visibility = View.GONE

        textSubmit.setOnClickListener {
            layoutName.error = null
            layoutName.isErrorEnabled = false
            layoutPhone.error = null
            layoutPhone.isErrorEnabled = false

            name = editName.text.toString().trim()
            phone = editPhone.text.toString().trim()
            email = editEmail.text.toString().trim()
            shopName = autoShop.text.toString().trim()

            if(name.isEmpty())
                layoutName.error = getString(R.string.required)
            else if(phone.isEmpty())
                layoutPhone.error = getString(R.string.required)
            else
                UpdateData().execute()
        }

        if(ConnectionDetector(context).checkForInternet())
            GetData().execute()
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
            loader.show()
        }

        private suspend fun doInBackground(): String = withContext(Dispatchers.IO) {

            var status = ""

            try {

                result = RetrofitHelper.getInstance().create(APICall.ApiShopList::class.java)
                    .getResult(registerToken, pagination = "false", page_limit = 0,
                        page_number = 0, search_parm = "", need_transaction = "no")

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
                            val arrayList = ArrayList<String>()
                            for (item in result.body()?.response!!)
                                arrayList.add(item.name)

                            val adapter = ArrayAdapter(context, R.layout.item_dropdown_textview, R.id.textview, arrayList)
                            autoShop.setAdapter(adapter)
                        }catch (e: Exception) {
                            Log.e("testing", "Response Exception is $e")
                        }
                    }
                    "invalidToken" -> {
                        TokenExpired(context)
                    }
                }

            }catch (e: JSONException)
            {
                Log.e("testing", "exception is $e")
            }
        }
    }

    inner class UpdateData: CoroutineScope by MainScope()
    {
        lateinit var result: Response<DataGenerateIncentiveQR.StatusCheck>

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

                result = RetrofitHelper.getInstance().create(APICall.ApiGenerateIncentiveQR::class.java)
                    .getResult(registerToken, name = name, mobile = phone, email = email, shop_name = shopName)

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

            loader.dismiss()

            try {

                when (status) {
                    "success" -> {
                        try {

                            globalValues.put("fromCreateIncentiveUserPage", true)

                            editName.setText("")
                            editPhone.setText("")
                            editEmail.setText("")
                            autoShop.setText("")
                            name = ""
                            phone = ""
                            email = ""
                            shopName = ""

                            Toast.makeText(context, result.body()?.message, Toast.LENGTH_SHORT).show()
                            layoutQRCode.visibility = View.VISIBLE

                            val qrCodeBitmap = CommonUtils.generateQRCode(result.body()?.response!!)
                            imgQRCode.setImageBitmap(qrCodeBitmap)

                            textShare.setOnClickListener {
                                layoutQRCodeShareContent.measure(
                                    View.MeasureSpec.makeMeasureSpec(layoutQRCodeShareContent.width, View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                )
                                layoutQRCodeShareContent.layout(
                                    layoutQRCodeShareContent.left,
                                    layoutQRCodeShareContent.top,
                                    layoutQRCodeShareContent.right,
                                    layoutQRCodeShareContent.top + layoutQRCodeShareContent.measuredHeight
                                )
                                val bitmap = Bitmap.createBitmap(
                                    layoutQRCodeShareContent.measuredWidth,
                                    layoutQRCodeShareContent.measuredHeight,
                                    Bitmap.Config.ARGB_8888
                                )
                                val canvas = Canvas(bitmap)
                                layoutQRCodeShareContent.draw(canvas)
                                val cachePath = File(applicationContext.cacheDir, "images")
                                cachePath.mkdirs()
                                val qrCodeFile = File(cachePath, "qrcode.png")
                                val qrCodeUri = FileProvider.getUriForFile(context, "$packageName.fileprovider", qrCodeFile)
                                val qrCodeOutputStream = FileOutputStream(qrCodeFile)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, qrCodeOutputStream)
                                qrCodeOutputStream.close()

                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.type = "image/*"
                                shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri)
                                startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
                            }
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