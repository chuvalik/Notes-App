package com.example.common.settings

import android.content.Context
import android.util.Log

private const val SHARED_PREFS_NAME = "user_session"
private const val KEY_USER_ID = "user_id"
private const val DEFAULT_VALUE = ""


class UserSessionStorageImpl(
    context: Context
) : UserSessionStorage {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveUserSessionId(userId: String) {
        Log.d("TestError", "saving id $userId")
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    override fun getUserSessionId(): String {
        Log.d("TestError", "get id ${sharedPreferences.getString(KEY_USER_ID, DEFAULT_VALUE) ?: DEFAULT_VALUE}")

        return sharedPreferences.getString(KEY_USER_ID, DEFAULT_VALUE) ?: DEFAULT_VALUE
    }
}