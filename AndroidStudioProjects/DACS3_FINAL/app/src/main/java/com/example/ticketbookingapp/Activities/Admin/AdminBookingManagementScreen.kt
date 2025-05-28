package com.example.ticketbookingapp.Activities.Admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.ViewModel.AdminViewModel
import com.example.ticketbookingapp.ViewModel.BookingWithMetadata

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingManagementScreen(
    adminViewModel: AdminViewModel,
    user: UserModel,
    onBackToDashboard: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val bookings by adminViewModel.bookings.collectAsState()
    val flights by adminViewModel.flights.collectAsState()
    val context = LocalContext.current
    val TAG = "AdminBookingManagementScreen"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý đặt vé",
                        color = colorResource(R.color.darkBlue),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackToDashboard() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = colorResource(R.color.darkBlue)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.lightGreyWhite)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { bookingWithMetadata ->
                        val flight = flights.find { it.FlightId == bookingWithMetadata.booking.flightId }
                        BookingItem(bookingWithMetadata = bookingWithMetadata, flight = flight)
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(R.color.lightBlue)
                )
            }
        }
    }
}

@Composable
fun BookingItem(bookingWithMetadata: BookingWithMetadata, flight: FlightModel?) {
    val booking = bookingWithMetadata.booking
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Người dùng: ${bookingWithMetadata.userId}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Hãng bay: ${flight?.AirlineName ?: "N/A"}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Chuyến bay: ${booking.from} (${flight?.FromShort ?: "N/A"}) -> ${booking.to} (${flight?.ToShort ?: "N/A"})",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ngày bay: ${booking.date}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Giờ bay: ${flight?.Time ?: "N/A"}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Thời gian bay: ${flight?.ArriveTime ?: "N/A"}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Số ghế: ${booking.seats}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Giá vé: ${String.format("%.2f", booking.price)}" + " VNĐ",
                    color = colorResource(R.color.lightBlue),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Hạng ghế: ${booking.typeClass}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ngày đặt vé: ${booking.bookingDate}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Trạng thái: ${
                        when (flight?.status) {
                            "SCHEDULED" -> "Sắp tới"
                            "COMPLETED" -> "Đã hoàn thành"
                            "DELAYED" -> "Hoãn"
                            "IN_FLIGHT" -> "Đang bay"
                            else -> "N/A"
                        }
                    }",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}