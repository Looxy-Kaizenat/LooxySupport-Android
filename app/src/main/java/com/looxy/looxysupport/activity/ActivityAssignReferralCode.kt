package com.looxy.looxysupport.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.looxy.looxysupport.R
import com.looxy.looxysupport.data.DataPostStatus
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
import java.util.Locale

class ActivityAssignReferralCode : AppCompatActivity() {

    var context : Context = this@ActivityAssignReferralCode
    private lateinit var globalValues: GlobalValues
    lateinit var loader: GifLoader
    lateinit var layoutLoader: LinearLayout

    private var registerToken: String = ""
    private var registerName: String = ""
    private var registerMobile: String = ""
    private var registerEmail: String = ""
    private var registerImage: String = ""

    private lateinit var layoutReferralCode: TextInputLayout
    private lateinit var editReferralCode: TextInputEditText
    private lateinit var layoutShop: TextInputLayout
    private lateinit var autoShop: AutoCompleteTextView
    private lateinit var textSubmit: TextView

    var referralCode = ""
    var shopName = ""
    var shopId = ""

    data class Item(val id: String, val name: String)
    val items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_referral_code)

        globalValues = GlobalValues(context)
        loader = GifLoader(context)
        layoutLoader = findViewById(R.id.layoutLoader)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        registerToken = sharedPreference.getString("registerToken","").toString()

        val imgBack: ImageView = findViewById(R.id.imgBack)
        val textActionTitle: TextView = findViewById(R.id.textActionTitle)
        textActionTitle.text = context.resources.getString(R.string.assign_referral_code)
        imgBack.setOnClickListener { onBackPressed() }

        layoutReferralCode = findViewById(R.id.layoutReferralCode)
        editReferralCode = findViewById(R.id.editReferralCode)
        layoutShop = findViewById(R.id.layoutShop)
        autoShop = findViewById(R.id.autoShop)
        textSubmit = findViewById(R.id.textSubmit)

        textSubmit.setOnClickListener {
            layoutReferralCode.error = null
            layoutReferralCode.isErrorEnabled = false
            layoutShop.error = null
            layoutShop.isErrorEnabled = false

            referralCode = editReferralCode.text.toString().trim()
            shopName = autoShop.text.toString().trim()

            if(shopName.isEmpty())
                layoutShop.error = getString(R.string.required)
            else if(referralCode.isEmpty())
                layoutReferralCode.error = getString(R.string.required)
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

                            for (item in result.body()?.response!!) {
                                items.add(Item(item.shop_id, item.name))
                            }

                            val adapter = CustomAdapter(context, items)
                            autoShop.setAdapter(adapter)

                            autoShop.setOnItemClickListener { parent, view, position, id ->
                                val selectedItem = parent.getItemAtPosition(position) as Item
                                shopId = selectedItem.id
                                autoShop.setText(selectedItem.name)
                                Log.e("testing", "Selected id $shopId")
                            }
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

    inner class CustomAdapter(context: Context, private val originalItems: List<Item>) : ArrayAdapter<Item>(context, R.layout.item_dropdown_textview, originalItems) {

        private var filteredItems: List<Item> = originalItems.toList()

        override fun getCount(): Int = filteredItems.size
        override fun getItem(position: Int): Item = filteredItems[position]

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_dropdown_textview, parent, false)
            val textView = view.findViewById<TextView>(R.id.textview)
            textView.text = getItem(position).name
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_dropdown_textview, parent, false)
            val textView = view.findViewById<TextView>(R.id.textview)
            textView.text = getItem(position).name
            return view
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val results = FilterResults()
                    if (constraint == null || constraint.isEmpty()) {
                        results.values = originalItems
                        results.count = originalItems.size
                    } else {
                        val searchText = constraint.toString().lowercase(Locale.ROOT)
                        val filteredList = originalItems.filter {
                            it.name.lowercase(Locale.ROOT).contains(searchText)
                        }
                        results.values = filteredList
                        results.count = filteredList.size
                    }
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredItems = results?.values as List<Item>
                    notifyDataSetChanged()
                }
            }
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

                result = RetrofitHelper.getInstance().create(APICall.ApiAssignReferralCode::class.java)
                    .getResult(registerToken, shop_id = shopId, ref_code = referralCode)

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

                            Toast.makeText(context, result.body()?.message, Toast.LENGTH_SHORT).show()
                            finish()
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