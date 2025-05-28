package com.example.ticketbookingapp.Activities.BookingHistory

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.ticketbookingapp.Activities.Dashboard.MyBottomBar
import com.example.ticketbookingapp.Activities.Dashboard.BlueTitle
import com.example.ticketbookingapp.Activities.Dashboard.TopBar
import com.example.ticketbookingapp.Activities.TicketDetail.TicketDetailActivity
import com.example.ticketbookingapp.Domain.BookingModel
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingHistoryScreen(user: UserModel) {
    val upcomingBookings = remember { mutableStateListOf<BookingModel>() }
    val pastBookings = remember { mutableStateListOf<BookingModel>() }
    val delayedBookings = remember { mutableStateListOf<BookingModel>() }
    val inFlightBookings = remember { mutableStateListOf<BookingModel>() }
    val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.US).apply {
        isLenient = true
    }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    val currentDateTime = Calendar.getInstance().time

    // Hàm chuyển đổi arriveTime (e.g., "2h 45m") thành số phút
    fun parseFlightDuration(arriveTime: String): Long {
        val parts = arriveTime.split("h", "m").map { it.trim().toIntOrNull() ?: 0 }
        val hours = parts.getOrNull(0) ?: 0
        val minutes = parts.getOrNull(1) ?: 0
        return (hours * 60 + minutes).toLong()
    }

    // Hàm tính arrivalTime từ departureTime và arriveTime
    fun calculateArrivalTime(booking: BookingModel): Date? {
        return try {
            val formattedDate = booking.date.replaceFirstChar { it.uppercaseChar() }
            val dateStr = dateFormat.parse(formattedDate) ?: return null
            val timeStr = timeFormat.parse(booking.time) ?: return null
            val calendar = Calendar.getInstance().apply {
                time = dateStr
                set(Calendar.HOUR_OF_DAY, timeStr.hours)
                set(Calendar.MINUTE, timeStr.minutes)
            }
            calendar.add(Calendar.MINUTE, parseFlightDuration(booking.arriveTime).toInt())
            calendar.time
        } catch (e: Exception) {
            println("Lỗi tính arrivalTime: ${e.message}")
            null
        }
    }

    LaunchedEffect(Unit) {
        val bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings").child(user.username)
        bookingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                upcomingBookings.clear()
                pastBookings.clear()
                delayedBookings.clear()
                inFlightBookings.clear()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    if (booking != null) {
                        try {
                            val formattedDate = booking.date.replaceFirstChar { it.uppercaseChar() }
                            val bookingDate = dateFormat.parse(formattedDate)
                            if (bookingDate == null) {
                                println("Ngày không hợp lệ: ${booking.date}")
                                continue
                            }
                            val departureTime = Calendar.getInstance().apply {
                                time = bookingDate
                                val timeParts = timeFormat.parse(booking.time)
                                if (timeParts == null) {
                                    println("Thời gian không hợp lệ: ${booking.time}")
                                    return@apply
                                }
                                set(Calendar.HOUR_OF_DAY, timeParts.hours)
                                set(Calendar.MINUTE, timeParts.minutes)
                            }.time
                            val arrivalTime = calculateArrivalTime(booking)
                            if (arrivalTime == null) {
                                println("Không thể tính arrivalTime cho: ${booking.date}, ${booking.time}")
                                continue
                            }

                            when {
                                booking.status == "DELAYED" -> delayedBookings.add(booking)
                                booking.status == "IN_FLIGHT" && departureTime <= currentDateTime && arrivalTime >= currentDateTime -> inFlightBookings.add(booking)
                                booking.status == "COMPLETED" || arrivalTime < currentDateTime -> pastBookings.add(booking)
                                booking.status == "SCHEDULED" && departureTime > currentDateTime -> upcomingBookings.add(booking)
                            }
                        } catch (e: Exception) {
                            println("Lỗi phân tích dữ liệu: ${booking.date}, ${e.message}")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Lỗi Firebase: ${error.message}")
            }
        })
    }

    // State cho tab hiện tại
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Vé sắp tới", "Vé đã sử dụng", "Vé hoãn", "Vé đang bay")

    Scaffold(
        bottomBar = { MyBottomBar(user = user, currentScreen = "BookingHistory") },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite))
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "Lịch sử đặt vé",
                        fontSize = 25.sp, // Tăng kích thước chữ
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.darkBlue),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            item {
                // TabRow để chọn giữa các mục
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> { // Vé sắp tới
                            BlueTitle("Vé sắp tới")
                            Spacer(modifier = Modifier.height(16.dp))
                            if (upcomingBookings.isEmpty()) {
                                Text(
                                    text = "Không có vé sắp tới",
                                    color = colorResource(R.color.darkBlue),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                upcomingBookings.forEach { booking ->
                                    BookingItem(booking, user)
                                    Divider(
                                        color = colorResource(R.color.lightGreyWhite),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        1 -> { // Vé đã sử dụng
                            BlueTitle("Vé đã sử dụng")
                            Spacer(modifier = Modifier.height(16.dp))
                            if (pastBookings.isEmpty()) {
                                Text(
                                    text = "Không có vé đã sử dụng",
                                    color = colorResource(R.color.darkBlue),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                pastBookings.forEach { booking ->
                                    BookingItem(booking, user)
                                    Divider(
                                        color = colorResource(R.color.lightGreyWhite),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        2 -> { // Vé hoãn
                            BlueTitle("Vé hoãn")
                            Spacer(modifier = Modifier.height(16.dp))
                            if (delayedBookings.isEmpty()) {
                                Text(
                                    text = "Không có vé hoãn",
                                    color = colorResource(R.color.darkBlue),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                delayedBookings.forEach { booking ->
                                    BookingItem(booking, user)
                                    Divider(
                                        color = colorResource(R.color.lightGreyWhite),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        3 -> { // Vé đang bay
                            BlueTitle("Vé đang bay")
                            Spacer(modifier = Modifier.height(16.dp))
                            if (inFlightBookings.isEmpty()) {
                                Text(
                                    text = "Không có vé đang bay",
                                    color = colorResource(R.color.darkBlue),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                inFlightBookings.forEach { booking ->
                                    BookingItem(booking, user)
                                    Divider(
                                        color = colorResource(R.color.lightGreyWhite),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BookingItem(
    booking: BookingModel,
    user: UserModel
) {
    val context = LocalContext.current

    // Tạo FlightModel từ BookingModel để truyền sang TicketDetailActivity
    val flight = FlightModel(
        FlightId = booking.flightId,
        AirlineName = booking.airlineName,
        AirlineLogo = booking.airlineLogo,
        ArriveTime = booking.arriveTime,
        ClassSeat = booking.classSeat,
        TypeClass = booking.typeClass,
        Date = booking.date,
        From = booking.from,
        FromShort = booking.fromShort,
        Price = booking.price,
        To = booking.to,
        ToShort = booking.toShort,
        Time = booking.time,
        bookingTime = booking.bookingDate,
        status = booking.status // Truyền status từ BookingModel
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val intent = Intent(context, TicketDetailActivity::class.java).apply {
                    putExtra("flight", flight)
                    putExtra("user", user)
                    putExtra("selectedSeats", booking.seats)
                    putExtra("totalPrice", booking.price)
                    putExtra("bookingTime", booking.bookingDate)
                    putExtra("isHistoryView", true)
                }
                context.startActivity(intent)
            }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        val (
            logo, timeTxt, airplaneIcon, dashLine,
            priceTxt, seatIcon, classTxt,
            fromTxt, fromShortTxt, toTxt, toShortTxt
        ) = createRefs()

        if (booking.airlineLogo.isNotBlank()) {
            AsyncImage(
                model = booking.airlineLogo,
                contentDescription = "Logo hãng bay",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(width = 120.dp, height = 40.dp)
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        println("Lỗi tải logo: ${state.result.throwable.message}")
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 40.dp)
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            )
        }

        Text(
            text = booking.arriveTime.takeIf { it.isNotBlank() } ?: "N/A",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = colorResource(R.color.darkBlue),
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(timeTxt) {
                    start.linkTo(parent.start)
                    top.linkTo(logo.bottom)
                    end.linkTo(parent.end)
                }
        )

        Image(
            painter = painterResource(R.drawable.line_airple_blue),
            contentDescription = "Biểu tượng đường bay",
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(airplaneIcon) {
                    start.linkTo(parent.start)
                    top.linkTo(timeTxt.bottom)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(R.drawable.dash_line),
            contentDescription = "Đường phân cách",
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(dashLine) {
                    start.linkTo(parent.start)
                    top.linkTo(airplaneIcon.bottom)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = "${String.format("%.2f", booking.price)}" + " VNĐ",
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp,
            color = colorResource(R.color.lightBlue),
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(priceTxt) {
                    top.linkTo(dashLine.bottom)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, margin = 16.dp)
                }
        )

        Image(
            painter = painterResource(R.drawable.seat_black_ic),
            contentDescription = "Biểu tượng ghế ngồi",
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(seatIcon) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(dashLine.bottom)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = booking.typeClass.takeIf { it.isNotBlank() } ?: "N/A",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = colorResource(R.color.darkBlue),
            modifier = Modifier
                .constrainAs(classTxt) {
                    start.linkTo(seatIcon.end, margin = 4.dp)
                    top.linkTo(seatIcon.top)
                    bottom.linkTo(seatIcon.bottom)
                }
        )

        Text(
            text = booking.from.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 14.sp,
            color = colorResource(R.color.darkBlue),
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(fromTxt) {
                    top.linkTo(timeTxt.bottom)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = booking.fromShort.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.darkBlue),
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(fromShortTxt) {
                    top.linkTo(fromTxt.bottom)
                    start.linkTo(fromTxt.start)
                    end.linkTo(fromTxt.end)
                }
        )

        Text(
            text = booking.to.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 14.sp,
            color = colorResource(R.color.darkBlue),
            modifier = Modifier
                .padding(end = 16.dp)
                .constrainAs(toTxt) {
                    top.linkTo(timeTxt.bottom)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = booking.toShort.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.darkBlue),
            modifier = Modifier
                .padding(end = 16.dp)
                .constrainAs(toShortTxt) {
                    top.linkTo(toTxt.bottom)
                    start.linkTo(toTxt.start)
                    end.linkTo(toTxt.end)
                }
        )
    }
}