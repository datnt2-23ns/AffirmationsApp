package com.example.ticketbookingapp.Activities.TicketDetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel

class TicketDetailActivity : AppCompatActivity() {
    private var flight: FlightModel? = null
    private var user: UserModel? = null
    private var selectedSeats: String = ""
    private var totalPrice: Double = 0.0
    private var bookingTime: String = ""
    private var isHistoryView: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lấy dữ liệu từ Intent
        flight = intent.getSerializableExtra("flight") as? FlightModel
        user = intent.getSerializableExtra("user") as? UserModel
        selectedSeats = intent.getStringExtra("selectedSeats") ?: ""
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        bookingTime = intent.getStringExtra("bookingTime") ?: ""
        isHistoryView = intent.getBooleanExtra("isHistoryView", false)

        // Kiểm tra dữ liệu đầu vào
        if (flight == null || user == null || selectedSeats.isBlank()) {
            Toast.makeText(this, "Invalid data: Flight, User, or Seats missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContent {
            StatusTopBarColor()

            TicketDetailScreen(
                flight = flight!!,
                selectedSeats = selectedSeats,
                totalPrice = totalPrice,
                user = user!!,
                onBackClick = { finish() },
                onDownloadTicketClick = { /* Có thể xử lý tải vé nếu cần */ },
                isHistoryView = isHistoryView
            )
        }
    }
}