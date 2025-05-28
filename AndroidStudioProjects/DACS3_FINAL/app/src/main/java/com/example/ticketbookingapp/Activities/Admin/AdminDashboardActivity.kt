package com.example.ticketbookingapp.Activities.Admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbookingapp.Activities.Auth.LoginActivity
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Domain.UserModel

class AdminDashboardActivity : AppCompatActivity() {
    private val TAG = "AdminDashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getSerializableExtra("user") as? UserModel
        if (user == null) {
            Log.e(TAG, "Dữ liệu người dùng không tìm thấy")
            Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            StatusTopBarColor()
            AdminDashboardScreen(user = user)
        }
    }

    companion object {
        fun newIntent(context: Context, user: UserModel): Intent {
            return Intent(context, AdminDashboardActivity::class.java).apply {
                putExtra("user", user)
            }
        }
    }
}