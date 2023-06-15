package com.looxy.looxysupport.utilities

import android.content.Context

class GlobalValues(private var activity: Context?) {

    var preference = "looxySupportApp"

    fun getString(s: String?): String? {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        return preferences.getString(s, "")
    }

    fun getInt(value: String?): Int {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        return preferences.getInt(value, 0)
    }

    fun getFloat(value: String?): Float? {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        return preferences.getFloat(value, 0f)
    }

    fun getBoolean(value: String?): Boolean {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        return preferences.getBoolean(value, false)
    }

    fun put(name: String?, value: String?) {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        val editor = preferences.edit()
        editor.putString(name, value)
        editor.commit()
    }

    fun put(name: String?, value: Boolean) {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        val editor = preferences.edit()
        editor.putBoolean(name, value)
        editor.commit()
    }

    fun put(name: String?, value: Int) {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        val editor = preferences.edit()
        editor.putInt(name, value)
        editor.commit()
    }

    fun put(name: String?, value: Float?) {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        val editor = preferences.edit()
        editor.putFloat(name, value!!)
        editor.commit()
    }

    fun remove(name: String?): Boolean {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        val editor = preferences.edit()
        return if (has(name)) {
            editor.remove(name)
            editor.commit()
            true
        } else {
            false
        }
    }

    fun clear() {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun LogOut() {
        this.remove("user_id")
        this.remove("auth_token")
    }

    fun has(name: String?): Boolean {
        val preferences = activity!!.getSharedPreferences(preference, 0)
        return preferences.contains(name)
    }
}