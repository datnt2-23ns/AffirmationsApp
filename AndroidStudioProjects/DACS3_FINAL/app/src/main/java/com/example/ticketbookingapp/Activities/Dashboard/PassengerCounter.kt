package com.example.ticketbookingapp.Activities.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.R

@Composable
fun PassengerCounter(
    title: String,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit
) {
    var passengerCounter by remember { mutableStateOf(0) } // Khởi tạo với 0

    Box(
        modifier = modifier
            .height(60.dp)
            .padding(top = 8.dp)
            .background(
                color = colorResource(R.color.lightGreyWhite), // Nền xám trắng
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.passenger_ic),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Minus button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (passengerCounter > 0) { // Thay đổi điều kiện
                            passengerCounter--
                            onItemSelected(passengerCounter.toString())
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-",
                    color = colorResource(R.color.darkBlue), // Văn bản màu darkBlue
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Passenger count
            Text(
                text = "$passengerCounter $title",
                color = colorResource(R.color.darkBlue), // Văn bản màu darkBlue
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Plus button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        passengerCounter++
                        onItemSelected(passengerCounter.toString())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = colorResource(R.color.darkBlue), // Văn bản màu darkBlue
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}