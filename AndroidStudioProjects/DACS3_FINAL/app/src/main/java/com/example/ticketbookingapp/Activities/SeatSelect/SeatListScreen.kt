package com.example.ticketbookingapp.Activities.SeatSelect

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.R

enum class SeatStatus {
    AVAILABLE,
    SELECTED,
    UNAVAILABLE,
    EMPTY
}

data class Seat(
    var status: SeatStatus,
    var name: String
)

@Composable
fun SeatListScreen(
    flight: FlightModel,
    numPassenger: Int,
    onBackClick: () -> Unit,
    onConfirm: (FlightModel, String, Double) -> Unit
) {
    val context = LocalContext.current
    val seatList = remember { mutableStateListOf<Seat>() }
    val selectedSeatNames = remember { mutableStateListOf<String>() }
    var seatCount by remember { mutableStateOf(0) }
    var totalPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        seatList.clear()
        val seats = generateSeatList(flight)
        Log.d(
            "SeatListScreen",
            "Tạo danh sách ghế: ${seats.size}, Ghế đã đặt: ${flight.ReservedSeats}"
        )
        seatList.addAll(seats)
        seatCount = selectedSeatNames.size
        totalPrice = seatCount * flight.Price
    }

    fun updatePriceAndCount() {
        seatCount = selectedSeatNames.size
        totalPrice = seatCount * flight.Price
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.lightGreyWhite)) // Nền xám trắng
    ) {
        val (topSection, middSection, bottomSection) = createRefs()

        TopSection(
            modifier = Modifier
                .constrainAs(topSection) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onBackClick = onBackClick
        )

        ConstraintLayout(
            modifier = Modifier
                .padding(top = 80.dp)
                .constrainAs(middSection) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            val (airplane, seatGrid) = createRefs()
            Image(
                painter = painterResource(R.drawable.airple_seat),
                contentDescription = "Bố cục máy bay",
                modifier = Modifier.constrainAs(airplane) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            if (seatList.isEmpty()) {
                Text(
                    text = "Không có ghế trống cho hạng Thương gia",
                    color = colorResource(R.color.darkBlue), // Màu darkBlue
                    modifier = Modifier
                        .padding(top = 240.dp)
                        .constrainAs(seatGrid) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .padding(top = 200.dp)
                        .padding(horizontal = 64.dp)
                        .constrainAs(seatGrid) {
                            top.linkTo(parent.top)
                            start.linkTo(airplane.start)
                            end.linkTo(airplane.end)
                        }
                ) {
                    items(seatList.size) { index ->
                        val seat = seatList[index]
                        SeatItem(
                            seat = seat,
                            onSeatClick = {
                                when (seat.status) {
                                    SeatStatus.AVAILABLE -> {
                                        if (selectedSeatNames.size < numPassenger) {
                                            seat.status = SeatStatus.SELECTED
                                            selectedSeatNames.add(seat.name)
                                            updatePriceAndCount()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Bạn chỉ có thể chọn $numPassenger ghế",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    SeatStatus.SELECTED -> {
                                        seat.status = SeatStatus.AVAILABLE
                                        selectedSeatNames.remove(seat.name)
                                        updatePriceAndCount()
                                    }

                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
        }

        BottomSection(
            seatCount = seatCount,
            selectedSeats = selectedSeatNames.joinToString(","),
            totalPrice = totalPrice,
            onConfirmClick = {
                if (seatCount == numPassenger) {
                    onConfirm(flight, selectedSeatNames.joinToString(","), totalPrice)
                } else {
                    Toast.makeText(
                        context,
                        "Vui lòng chọn đúng $numPassenger ghế",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.constrainAs(bottomSection) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

fun generateSeatList(flight: FlightModel): List<Seat> {
    val seatList = mutableListOf<Seat>()
    val businessSeatCount = minOf(flight.NumberSeat, 20)
    val rows = (businessSeatCount + 5) / 6
    val reservedSeats = flight.ReservedSeats?.split(",")?.toSet() ?: emptySet()

    Log.d(
        "SeatListScreen",
        "Số ghế: ${flight.NumberSeat}, Số ghế thương gia: $businessSeatCount, Số hàng: $rows"
    )

    if (businessSeatCount <= 0) {
        Log.d("SeatListScreen", "Không có ghế trống do số ghế <= 0")
        return emptyList()
    }

    val seatAlphabetMap = mapOf(
        0 to "A",
        1 to "B",
        2 to "C",
        4 to "D",
        5 to "E",
        6 to "F"
    )

    for (row in 1..rows) {
        for (col in 0 until 7) {
            if (col == 3) {
                seatList.add(Seat(SeatStatus.EMPTY, "$row"))
            } else {
                val seatName = "${seatAlphabetMap[col]}$row"
                val seatStatus = if (reservedSeats.contains(seatName)) {
                    SeatStatus.UNAVAILABLE
                } else {
                    SeatStatus.AVAILABLE
                }
                seatList.add(Seat(seatStatus, seatName))
            }
        }
    }

    val availableSeats = seatList.count { it.status == SeatStatus.AVAILABLE }
    Log.d("SeatListScreen", "Tổng số ghế: ${seatList.size}, Ghế trống: $availableSeats")

    return seatList
}