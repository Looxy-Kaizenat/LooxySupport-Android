package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.looxy.looxysupport.R
import com.looxy.looxysupport.adapter.AdapterIncentiveUserList
import com.looxy.looxysupport.data.DataIncentiveUserList
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

class ActivityIncentiveUserList : AppCompatActivity(), AdapterIncentiveUserList.OnItemClick {

    var context : Context = this@ActivityIncentiveUserList
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
    lateinit var layoutAdd: LinearLayout

    private val mCallBack: AdapterIncentiveUserList.OnItemClick = this

    lateinit var dialog: Dialog

    var userURL = ""
    var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incentive_user_list)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.incentive_user_list)
        imgBack.setOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerView)
        layoutNoData = findViewById(R.id.layoutNoData)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        layoutAdd = findViewById(R.id.layoutAdd)

        layoutNoData.visibility = View.GONE

        layoutAdd.setOnClickListener { startActivity(Intent(context, ActivityCreateIncentiveUser::class.java)) }

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
        lateinit var result: Response<DataIncentiveUserList.StatusCheck>

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

                result = RetrofitHelper.getInstance().create(APICall.ApiIncentiveUserList::class.java)
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
                            if(result.body()?.response != null)
                            {
                                val adapter = AdapterIncentiveUserList(context,
                                    result.body()?.response!!, mCallBack)
                                recyclerView.adapter = adapter
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

    override fun onClickedItem(position: Int, list: DataIncentiveUserList.DataList?, status: Int) {
        if(status == 1)
        {
            userURL = list?.url.toString()
            userName = list?.name.toString()
            dialogQRCode(userURL, userName)
        }
    }

    private fun dialogQRCode(url : String, name: String)
    {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.dialog_incentive_qr_code, null) as View

        dialog.setContentView(convertView)
        dialog.setCanceledOnTouchOutside(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgQRCode: ImageView = convertView.findViewById(R.id.imgQRCode)
        val textSubmit: TextView = convertView.findViewById(R.id.textSubmit)
        val textCancel: TextView = convertView.findViewById(R.id.textCancel)
        val textTitle: TextView = convertView.findViewById(R.id.textTitle)
        val layoutQRCodeShareContent: LinearLayout = convertView.findViewById(R.id.layoutQRCodeShareContent)

        val title = "$name's QR code"
        textTitle.text = title

        val qrCodeBitmap = CommonUtils.generateQRCode(url)
        imgQRCode.setImageBitmap(qrCodeBitmap)

        textSubmit.setOnClickListener {
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
            val qrCodeUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", qrCodeFile)
            val qrCodeOutputStream = FileOutputStream(qrCodeFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, qrCodeOutputStream)
            qrCodeOutputStream.close()

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri)
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
        }

        textCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()

        if (globalValues.getBoolean("fromCreateIncentiveUserPage"))
        {
            globalValues.put("fromCreateIncentiveUserPage", false)
            GetData().execute()
        }
    }
}