package com.example.ticketbookingapp.Activities.BookingHistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Domain.UserModel

class BookingHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lấy UserModel từ Intent
        val user = intent.getSerializableExtra("user") as? UserModel ?: return

        setContent {
            StatusTopBarColor()
            BookingHistoryScreen(user = user)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, BookingHistoryActivity::class.java)
        }
    }
}