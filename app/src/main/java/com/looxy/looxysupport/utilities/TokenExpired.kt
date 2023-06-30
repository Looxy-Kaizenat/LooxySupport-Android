package com.looxy.looxysupport.utilities

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.looxy.looxysupport.R
import com.looxy.looxysupport.utilities.AkAlertDialog.OnPositiveClick

class TokenExpired(private val context: Context) {
    var dialog: AkAlertDialog = AkAlertDialog(context)

    init {
        if (!dialog.isShowing) {
            val prefUserData = context.getSharedPreferences("registerAdminDetails", 0)
            val prefEditor = prefUserData.edit()
            prefEditor.clear()
            prefEditor.apply()
            FirebaseAuth.getInstance().signOut()
            dialog.setTitle(context.resources.getString(R.string.alert))
            dialog.setMessage(context.resources.getString(R.string.token_expired))
            dialog.setCanceledOnTouchOutside(false)
            dialog.setPositiveButton(
                context.resources.getString(R.string.ok),
                object : OnPositiveClick {
                    override fun onClick(status: Int) {}
                })
            dialog.show()
        }
    }
}