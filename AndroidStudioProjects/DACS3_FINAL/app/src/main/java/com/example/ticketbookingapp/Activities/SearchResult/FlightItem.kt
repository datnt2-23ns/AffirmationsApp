package com.example.ticketbookingapp.Activities.SearchResult

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.ticketbookingapp.Activities.SeatSelect.SeatSelectActivity
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R

@Composable
fun FlightItem(
    item: FlightModel,
    index: Int,
    numPassenger: Int,
    user: UserModel // Thêm tham số user
) {
    val context = LocalContext.current

    // Gán flightId nếu trống
    val flight = item.copy().apply {
        if (FlightId.isEmpty()) {
            FlightId = "flight_${item.AirlineName}_${item.Date.replace(" ", "_")}_${item.Time.replace(":", "")}"
        }
    }

    println("Hiển thị chuyến bay: ${flight.AirlineName}, Từ=${flight.From}, Đến=${flight.To}, Ngày=${flight.Date}, Hạng ghế=${flight.TypeClass}")

    ConstraintLayout(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, SeatSelectActivity::class.java).apply {
                    putExtra("flight", flight)
                    putExtra("user", user) // Truyền user vào Intent
                    putExtra("numPassenger", numPassenger.toString())
                }
                context.startActivity(intent)
            }
            .background(
                color = colorResource(R.color.white),
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        val (
            logo, timeTxt, airplaneIcon, dashLine,
            priceTxt, seatIcon, classTxt,
            fromTxt, fromShortTxt, toTxt, toShortTxt
        ) = createRefs()

        if (flight.AirlineLogo.isNotBlank()) {
            AsyncImage(
                model = flight.AirlineLogo,
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
            text = flight.ArriveTime.takeIf { it.isNotBlank() } ?: "N/A",
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
            text = "${String.format("%.2f", flight.Price)}" + " VNĐ",
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
            text = flight.TypeClass.takeIf { it.isNotBlank() } ?: "N/A",
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
            text = flight.From.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(fromTxt) {
                    top.linkTo(timeTxt.bottom)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = flight.FromShort.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(fromShortTxt) {
                    top.linkTo(fromTxt.bottom)
                    start.linkTo(fromTxt.start)
                    end.linkTo(fromTxt.end)
                }
        )

        Text(
            text = flight.To.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(end = 16.dp)
                .constrainAs(toTxt) {
                    top.linkTo(timeTxt.bottom)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = flight.ToShort.takeIf { it.isNotBlank() } ?: "N/A",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
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