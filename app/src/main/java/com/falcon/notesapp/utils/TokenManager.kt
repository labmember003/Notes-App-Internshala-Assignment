package com.falcon.notesapp.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs = context.getSharedPreferences(Constants.PREFS_TOKEN, Context.MODE_PRIVATE)

    fun saveUserExistance() {
        val editor = prefs.edit()
        editor.putBoolean(Constants.USER_TOKEN, true)
        editor.apply()
    }

    fun deleteUserExistance() {
        val editor = prefs.edit()
        editor.remove(Constants.USER_TOKEN)
        editor.apply()
    }

    fun doesUserExist(): Boolean {
        return prefs.getBoolean(Constants.USER_TOKEN, false)
    }
}