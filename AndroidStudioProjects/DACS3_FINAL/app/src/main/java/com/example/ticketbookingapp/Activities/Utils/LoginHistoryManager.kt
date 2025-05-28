package com.example.ticketbookingapp.Utils // Tạo một package phù hợp, ví dụ Utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LoginHistoryItem(
    val identifier: String, // Tên đăng nhập hoặc email
    val timestamp: Long,
    val formattedDateTime: String // Để hiển thị thân thiện với người dùng
)

class LoginHistoryManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LoginHistoryPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val MAX_HISTORY_ITEMS = 10 // Giới hạn số lượng mục lịch sử

    companion object {
        private const val KEY_LOGIN_HISTORY = "login_history"
    }

    fun addLoginAttempt(identifier: String, isSuccess: Boolean) {
        if (!isSuccess) {
            // Bạn có thể chọn không lưu nếu đăng nhập thất bại,
            // hoặc lưu với một trạng thái khác nếu cần.
            // Hiện tại, chúng ta chỉ lưu đăng nhập thành công.
            return
        }

        val currentTimestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDateTime = sdf.format(Date(currentTimestamp))

        val newItem = LoginHistoryItem(identifier, currentTimestamp, formattedDateTime)
        val history = getLoginHistory().toMutableList()

        // Thêm vào đầu danh sách (mới nhất lên trước)
        history.add(0, newItem)

        // Giữ cho danh sách không vượt quá MAX_HISTORY_ITEMS
        while (history.size > MAX_HISTORY_ITEMS) {
            history.removeAt(history.size - 1) // Xóa mục cũ nhất
        }

        saveLoginHistory(history)
    }

    fun getLoginHistory(): List<LoginHistoryItem> {
        val jsonHistory = prefs.getString(KEY_LOGIN_HISTORY, null)
        return if (jsonHistory != null) {
            val type = object : TypeToken<List<LoginHistoryItem>>() {}.type
            gson.fromJson(jsonHistory, type)
        } else {
            emptyList()
        }
    }

    private fun saveLoginHistory(history: List<LoginHistoryItem>) {
        val jsonHistory = gson.toJson(history)
        prefs.edit().putString(KEY_LOGIN_HISTORY, jsonHistory).apply()
    }

    fun clearLoginHistory() {
        prefs.edit().remove(KEY_LOGIN_HISTORY).apply()
    }
}