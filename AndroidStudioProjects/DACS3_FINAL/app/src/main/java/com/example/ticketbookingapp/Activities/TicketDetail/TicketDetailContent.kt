package com.example.ticketbookingapp.Activities.TicketDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.R

@Composable
fun TicketDetailContent(
    flight: FlightModel,
    selectedSeats: String,
    totalPrice: Double,
    bookingTime: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .background(
                color = Color.White, // Nền trắng
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        // Phần logo và thông tin chuyến bay
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            val (logo, arrivalTxt, lineImg, fromTxt, fromShortTxt, toTxt, toShortTxt) = createRefs()

            if (flight.AirlineLogo.isNotBlank()) {
                AsyncImage(
                    model = flight.AirlineLogo,
                    contentDescription = "Logo hãng bay",
                    modifier = Modifier
                        .size(200.dp, 50.dp)
                        .constrainAs(logo) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    contentScale = ContentScale.Fit,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            println("Lỗi tải logo hãng bay: ${state.result.throwable.message}")
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp, 50.dp)
                        .constrainAs(logo) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }

            Text(
                text = flight.ArriveTime.takeIf { it.isNotBlank() } ?: "N/A",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.darkBlue), // Màu darkBlue
                modifier = Modifier.constrainAs(arrivalTxt) {
                    top.linkTo(logo.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Image(
                painter = painterResource(R.drawable.line_airple_blue),
                contentDescription = "Biểu tượng đường bay",
                modifier = Modifier
                    .constrainAs(lineImg) {
                        top.linkTo(arrivalTxt.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = "Từ",
                fontSize = 14.sp,
                color = colorResource(R.color.darkBlue), // Màu darkBlue
                modifier = Modifier.constrainAs(fromTxt) {
                    top.linkTo(arrivalTxt.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(lineImg.start)
                }
            )

            Text(
                text = flight.FromShort.takeIf { it.isNotBlank() } ?: "N/A",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.darkBlue), // Màu darkBlue
                modifier = Modifier.constrainAs(fromShortTxt) {
                    top.linkTo(fromTxt.bottom, margin = 8.dp)
                    start.linkTo(fromTxt.start)
                    end.linkTo(fromTxt.end)
                    bottom.linkTo(lineImg.bottom)
                }
            )

            Text(
                text = "Đến",
                fontSize = 14.sp,
                color = colorResource(R.color.darkBlue), // Màu darkBlue
                modifier = Modifier.constrainAs(toTxt) {
                    top.linkTo(arrivalTxt.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(lineImg.end)
                }
            )

            Text(
                text = flight.ToShort.takeIf { it.isNotBlank() } ?: "N/A",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.darkBlue), // Màu darkBlue
                modifier = Modifier.constrainAs(toShortTxt) {
                    top.linkTo(toTxt.bottom, margin = 8.dp)
                    start.linkTo(toTxt.start)
                    end.linkTo(toTxt.end)
                    bottom.linkTo(lineImg.bottom)
                }
            )
        }

        // Phần thông tin chi tiết
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                Text(
                    text = "Điểm đi",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = flight.From.takeIf { it.isNotBlank() } ?: "N/A",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ngày bay",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = flight.Date.takeIf { it.isNotBlank() } ?: "N/A",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                Text(
                    text = "Điểm đến",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = flight.To.takeIf { it.isNotBlank() } ?: "N/A",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Giờ bay",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = flight.Time.takeIf { it.isNotBlank() } ?: "N/A",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.dash_line),
            contentDescription = "Đường phân cách",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        // Phần thông tin bổ sung và giá
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                Text(
                    text = "Hạng ghế",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = flight.ClassSeat.takeIf { it.isNotBlank() } ?: "N/A",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Số ghế",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedSeats.ifEmpty { "Chưa chọn ghế" },
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Thời gian đặt vé",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = bookingTime,
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                Text(
                    text = "Hãng bay",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = flight.AirlineName.takeIf { it.isNotBlank() } ?: "N/A",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Giá vé",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.2f", totalPrice)}" + " VNĐ",
                    color = colorResource(R.color.lightBlue), // Màu lightBlue
                    fontWeight = FontWeight.Normal
                )
            }

            Image(
                painter = painterResource(R.drawable.qrcode),
                contentDescription = "Mã QR vé",
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 8.dp),
            )
        }

        Image(
            painter = painterResource(R.drawable.dash_line),
            contentDescription = "Đường phân cách",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Image(
            painter = painterResource(R.drawable.barcode),
            contentDescription = "Mã vạch vé",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}