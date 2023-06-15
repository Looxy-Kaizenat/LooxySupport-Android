package com.looxy.looxysupport.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.looxy.looxysupport.R

class GifLoader(context: Context) : Dialog(context){

    init {
        setContentView(R.layout.layout_loader_dialog)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}