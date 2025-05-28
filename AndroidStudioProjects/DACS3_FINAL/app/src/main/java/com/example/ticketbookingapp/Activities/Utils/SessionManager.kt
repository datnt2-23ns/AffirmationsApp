package com.example.ticketbookingapp.Utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ticketbookingapp.Domain.UserModel
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER = "logged_in_user"
    }

    fun saveUser(user: UserModel) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): UserModel? {
        val userJson = prefs.getString(KEY_USER, null)
        return if (userJson != null) gson.fromJson(userJson, UserModel::class.java) else null
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains(KEY_USER)
    }

    fun logout() {
        prefs.edit().remove(KEY_USER).apply()
    }
}
