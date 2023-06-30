package com.looxy.looxysupport.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.looxy.looxysupport.R

class AkAlertDialog(var context: Context) {
    var dialog: Dialog = Dialog(context)
    var title: TextView
    var subtitle: TextView
    var no: TextView
    var yes: TextView
    var positiveClick: OnPositiveClick? = null
    var negativeClick: OnNegativeClick? = null

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.dialog_yes_no, null) as View
        dialog.setContentView(convertView)
        dialog.setCanceledOnTouchOutside(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        //        languagedialog.getWindow().setAttributes(lp);
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        title = convertView.findViewById(R.id.title)
        subtitle = convertView.findViewById(R.id.subtitle)
        no = convertView.findViewById(R.id.no)
        yes = convertView.findViewById(R.id.yes)
        title.visibility = View.GONE
        subtitle.visibility = View.GONE
        no.visibility = View.GONE
        yes.visibility = View.GONE
        yes.setOnClickListener {
            dialog.dismiss()
            positiveClick!!.onClick(1)
        }
        no.setOnClickListener {
            dialog.dismiss()
            negativeClick!!.onClick(2)
        }
    }

    fun setCanceledOnTouchOutside(cancelable: Boolean) {
        dialog.setCanceledOnTouchOutside(cancelable)
    }

    interface OnPositiveClick {
        fun onClick(status: Int)
    }

    interface OnNegativeClick {
        fun onClick(status: Int)
    }

    fun setTitle(name: String?) {
        title.visibility = View.VISIBLE
        title.text = name
    }

    fun setMessage(name: String?) {
        subtitle.visibility = View.VISIBLE
        subtitle.text = name
    }

    fun setPositiveButton(name: String?, positiveClick: OnPositiveClick?) {
        this.positiveClick = positiveClick
        yes.visibility = View.VISIBLE
        yes.text = name
    }

    fun setNegativeButton(name: String?, negativeClick: OnNegativeClick?) {
        this.negativeClick = negativeClick
        no.visibility = View.VISIBLE
        no.text = name
    }

    fun show() {
        dialog.show()
    }

    val isShowing: Boolean
        get() = dialog.isShowing

    fun dismiss() {
        dialog.dismiss()
    }
}