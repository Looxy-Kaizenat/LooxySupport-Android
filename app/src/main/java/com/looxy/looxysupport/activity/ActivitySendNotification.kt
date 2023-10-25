package com.looxy.looxysupport.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataPostStatus
import com.looxy.looxysupport.utilities.APICall
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

class ActivitySendNotification : AppCompatActivity() {

    var context : Context = this@ActivitySendNotification
    private lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader
    lateinit var layoutLoader: LinearLayout

    private var registerToken: String = ""
    private var registerName: String = ""
    private var registerMobile: String = ""
    private var registerEmail: String = ""
    private var registerImage: String = ""

    private lateinit var layoutTitle: TextInputLayout
    private lateinit var editTitle: TextInputEditText
    private lateinit var layoutMessage: TextInputLayout
    private lateinit var editMessage: TextInputEditText
    private lateinit var layoutType: TextInputLayout
    private lateinit var autoType: AutoCompleteTextView
    private lateinit var textSubmit: TextView

    var title = ""
    var message = ""
    var userType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_notification)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.notifications)
        imgBack.setOnClickListener { onBackPressed() }

        layoutTitle = findViewById(R.id.layoutTitle)
        editTitle = findViewById(R.id.editTitle)
        layoutMessage = findViewById(R.id.layoutMessage)
        editMessage = findViewById(R.id.editMessage)
        layoutType = findViewById(R.id.layoutType)
        autoType = findViewById(R.id.autoType)
        textSubmit = findViewById(R.id.textSubmit)

        val arrayList = ArrayList<String>()
        arrayList.add("user")
        arrayList.add("shop")
        val adapter = ArrayAdapter(context, R.layout.item_dropdown_textview, R.id.textview, arrayList)
        autoType.setAdapter(adapter)

        textSubmit.setOnClickListener {
            layoutTitle.error = null
            layoutTitle.isErrorEnabled = false
            layoutMessage.error = null
            layoutMessage.isErrorEnabled = false
            layoutType.error = null
            layoutType.isErrorEnabled = false

            title = editTitle.text.toString().trim()
            message = editMessage.text.toString().trim()
            userType = autoType.text.toString().trim()

            if(title.isEmpty())
                layoutTitle.error = getString(R.string.required)
            else if(message.isEmpty())
                layoutMessage.error = getString(R.string.required)
            else if(userType.isEmpty())
                layoutType.error = getString(R.string.required)
            else
                UpdateData().execute()
        }
    }

    inner class UpdateData: CoroutineScope by MainScope()
    {
        lateinit var result: Response<DataPostStatus.StatusCheck>

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

                result = RetrofitHelper.getInstance().create(APICall.ApiSendNotification::class.java)
                    .getResult(registerToken, title = title, message = message, user_type = userType)

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
                            editTitle.setText("")
                            editMessage.setText("")
                            autoType.setText("")
                            title = ""
                            message = ""
                            userType = ""

                            Toast.makeText(context, result.body()?.message, Toast.LENGTH_SHORT).show()
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