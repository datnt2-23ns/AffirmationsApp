package com.example.ticketbookingapp.Activities.TicketDetail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.ticketbookingapp.Activities.Dashboard.DashboardActivity
import com.example.ticketbookingapp.Activities.SeatSelect.TicketDetailHeader
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.Activities.Utils.QRCodeDialog
import com.example.ticketbookingapp.Domain.BookingModel
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.Repository.MainRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TicketDetailScreen(
    flight: FlightModel,
    selectedSeats: String,
    totalPrice: Double,
    user: UserModel,
    onBackClick: () -> Unit,
    onDownloadTicketClick: () -> Unit,
    isHistoryView: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = MainRepository()
    val currentTime = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(Date())
    var showQR by remember { mutableStateOf(false) }
    var autoSaveBooking by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.lightGreyWhite))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite))
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.lightGreyWhite))
            ) {
                val (topSection, ticketDetail) = createRefs()

                TicketDetailHeader(
                    onBackClick = onBackClick,
                    modifier = Modifier.constrainAs(topSection) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                TicketDetailContent(
                    flight = flight,
                    selectedSeats = selectedSeats,
                    totalPrice = totalPrice,
                    bookingTime = if (isHistoryView) flight.bookingTime else currentTime,
                    modifier = Modifier.constrainAs(ticketDetail) {
                        top.linkTo(parent.top, margin = 110.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }

            // Hiển thị nút dựa trên chế độ
            if (!isHistoryView) {
                // Nút PAYMENT cho chế độ không phải lịch sử
                GradientButton(
                    onClick = { showQR = true },
                    text = "Thanh Toán",
                    gradientColors = listOf(
                        colorResource(R.color.lightBlue),
                        colorResource(R.color.mediumBlue)
                    )
                )
            } else {
                // Nút Hủy cho chế độ lịch sử
                GradientButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val flightId = flight.FlightId
                                if (flightId.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Lỗi: Mã chuyến bay không hợp lệ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                val deleteResult = repository.deleteBooking(
                                    username = user.username,
                                    flightId = flightId,
                                    seats = selectedSeats
                                )
                                if (deleteResult.isFailure) {
                                    Toast.makeText(
                                        context,
                                        "Lỗi khi hủy vé: ${deleteResult.exceptionOrNull()?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                Toast.makeText(context, "Hủy vé thành công!", Toast.LENGTH_LONG)
                                    .show()
                                context.startActivity(
                                    Intent(context, DashboardActivity::class.java).apply {
                                        putExtra("user", user)
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    }
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Lỗi khi hủy vé: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    text = "Hủy",
                    gradientColors = listOf(
                        colorResource(R.color.lightBlue),
                        colorResource(R.color.mediumBlue)
                    )
                )
            }

            // Dialog QR code cho chế độ không phải lịch sử
            if (!isHistoryView) {
                QRCodeDialog(
                    totalPrice = totalPrice,
                    flightId = flight.FlightId,
                    showQR = showQR,
                    onDismiss = { showQR = false },
                    generateQRCode = { text -> generateQRCode(text) }
                )

                // Tự động lưu vé sau 30s
                LaunchedEffect(showQR) {
                    if (showQR && !autoSaveBooking) {
                        delay(30000) // Chờ 30s
                        autoSaveBooking = true
                        showQR = false // Đóng dialog QR
                        coroutineScope.launch {
                            try {
                                val flightId = flight.FlightId
                                if (flightId.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Lỗi: Mã chuyến bay không hợp lệ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                val bookingExists = repository.checkBookingExists(
                                    username = user.username,
                                    flightId = flightId,
                                    seats = selectedSeats
                                )
                                if (bookingExists) {
                                    Toast.makeText(
                                        context,
                                        "Vé này đã được đặt trước đó!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                val booking = BookingModel(
                                    flightId = flightId,
                                    date = flight.Date,
                                    from = flight.From,
                                    to = flight.To,
                                    typeClass = flight.TypeClass,
                                    seats = selectedSeats,
                                    price = totalPrice,
                                    bookingDate = currentTime,
                                    airlineName = flight.AirlineName,
                                    airlineLogo = flight.AirlineLogo,
                                    arriveTime = flight.ArriveTime,
                                    fromShort = flight.FromShort,
                                    toShort = flight.ToShort,
                                    time = flight.Time,
                                    classSeat = flight.ClassSeat
                                )

                                val saveResult = repository.saveBooking(user.username, booking)
                                if (saveResult.isFailure) {
                                    Toast.makeText(
                                        context,
                                        "Lỗi khi lưu vé: ${saveResult.exceptionOrNull()?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                val updateSeatsResult =
                                    repository.updateFlightReservedSeats(flightId, selectedSeats)
                                if (updateSeatsResult.isFailure) {
                                    Toast.makeText(
                                        context,
                                        "Lỗi khi cập nhật ghế: ${updateSeatsResult.exceptionOrNull()?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                Toast.makeText(context, "Đặt vé thành công!", Toast.LENGTH_LONG)
                                    .show()
                                context.startActivity(
                                    Intent(context, DashboardActivity::class.java).apply {
                                        putExtra("user", user)
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    }
                                )
                                onDownloadTicketClick()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Lỗi khi đặt vé: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun generateQRCode(text: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

