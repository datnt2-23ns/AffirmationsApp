package com.example.ticketbookingapp.Activities.SeatSelect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Activities.TicketDetail.TicketDetailActivity
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel

class SeatSelectActivity : AppCompatActivity() {
    private var flight: FlightModel? = null
    private var user: UserModel? = null
    private var numPassenger: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lấy dữ liệu từ Intent
        flight = intent.getSerializableExtra("flight") as? FlightModel
        user = intent.getSerializableExtra("user") as? UserModel
        numPassenger = intent.getStringExtra("numPassenger")?.toIntOrNull() ?: 0

        // Kiểm tra dữ liệu đầu vào
        if (flight == null || user == null || numPassenger <= 0) {
            Toast.makeText(this, "Invalid data: Flight or User missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContent {
            StatusTopBarColor()

            SeatListScreen(
                flight = flight!!,
                numPassenger = numPassenger,
                onBackClick = {
                    finish()
                },
                onConfirm = { updatedFlight, selectedSeats, totalPrice ->
                    val intent = Intent(this, TicketDetailActivity::class.java).apply {
                        putExtra("flight", updatedFlight)
                        putExtra("user", user)
                        putExtra("selectedSeats", selectedSeats)
                        putExtra("totalPrice", totalPrice)
                    }
                    startActivity(intent)
                }
            )
        }
    }
}