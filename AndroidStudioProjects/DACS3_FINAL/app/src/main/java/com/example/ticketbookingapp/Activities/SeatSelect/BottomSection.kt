package com.example.ticketbookingapp.Activities.SeatSelect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.R

@Composable
fun BottomSection(
    seatCount: Int,
    selectedSeats: String,
    totalPrice: Double,
    onConfirmClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.White) // Nền trắng
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(text = "Còn trống", color = colorResource(R.color.green))
            LegendItem(text = "Đã chọn", color = colorResource(R.color.orange))
            LegendItem(text = "Không khả dụng", color = colorResource(R.color.grey))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$seatCount Ghế đã chọn",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = if (selectedSeats.isBlank()) "-" else selectedSeats,
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "${String.format("%.2f", totalPrice)}" + " VNĐ",
                color = colorResource(R.color.lightBlue), // Màu lightBlue
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp
            )
        }

        GradientButton(
            onClick = onConfirmClick,
            text = "Xác nhận ghế",
            gradientColors = listOf(
                colorResource(R.color.lightBlue),
                colorResource(R.color.mediumBlue)
            )
        )
    }
}