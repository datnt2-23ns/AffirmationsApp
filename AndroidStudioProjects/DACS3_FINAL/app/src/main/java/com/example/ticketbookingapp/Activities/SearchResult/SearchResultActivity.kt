package com.example.ticketbookingapp.Activities.SearchResult

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.ViewModel.MainViewModel
import com.example.ticketbookingapp.R
import java.text.SimpleDateFormat
import java.util.*

class SearchResultActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private var from: String = ""
    private var to: String = ""
    private var departureDate: String = ""
    private var returnDate: String = ""
    private var typeClass: String = ""
    private var numPassenger: Int = 0
    private var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo ViewModel
        viewModel = MainViewModel()

        // Lấy dữ liệu từ Intent
        user = intent.getSerializableExtra("user") as? UserModel
        from = intent.getStringExtra("from")?.trim()?.replace("\\s+".toRegex(), " ") ?: ""
        to = intent.getStringExtra("to")?.trim()?.replace("\\s+".toRegex(), " ") ?: ""
        departureDate = intent.getStringExtra("departureDate")?.trim() ?: ""
        returnDate = intent.getStringExtra("returnDate")?.trim() ?: ""
        typeClass = intent.getStringExtra("typeClass")?.trim()?.replace("\\s+".toRegex(), " ") ?: ""
        numPassenger = intent.getStringExtra("numPassenger")?.toIntOrNull() ?: 0

        // Chuẩn hóa departureDate thành "dd MMM, yyyy"
        try {
            val inputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.US).apply { isLenient = true }
            val date = inputFormat.parse(departureDate) ?: throw IllegalArgumentException("Invalid date format")
            departureDate = inputFormat.format(date).lowercase()
            println("Standardized departureDate: '$departureDate'")
        } catch (e: Exception) {
            println("Error formatting departureDate: ${e.message}")
            departureDate = ""
        }

        // Log dữ liệu Intent
        println("Intent data: user='${user?.username}', from='$from', to='$to', departureDate='$departureDate', returnDate='$returnDate', typeClass='$typeClass', numPassenger=$numPassenger")

        // Kiểm tra dữ liệu đầu vào
        if (user == null || from.isBlank() || to.isBlank() || departureDate.isBlank() || typeClass.isBlank() || numPassenger <= 0) {
            setContent {
                StatusTopBarColor()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.darkPurple2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Invalid search parameters: user=${user?.username}, from='$from', to='$to', date='$departureDate', class='$typeClass', passengers=$numPassenger",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Toast.makeText(this, "Error: Invalid search parameters", Toast.LENGTH_LONG).show()
            return
        }

        setContent {
            StatusTopBarColor()
            ItemListScreen(
                from = from,
                to = to,
                departureDate = departureDate,
                returnDate = returnDate,
                typeClass = typeClass,
                numPassenger = numPassenger,
                user = user!!,
                viewModel = viewModel,
                onBackClick = {
                    finish()
                }
            )
        }
    }
}