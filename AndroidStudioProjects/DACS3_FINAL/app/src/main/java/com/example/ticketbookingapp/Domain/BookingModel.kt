package com.example.ticketbookingapp.Domain

import java.io.Serializable

data class BookingModel(
    val flightId: String = "",
    val date: String = "",
    val from: String = "",
    val to: String = "",
    val typeClass: String = "",
    val seats: String = "",
    val price: Double = 0.0,
    val bookingDate: String = "",
    val airlineName: String = "",
    val airlineLogo: String = "",
    val arriveTime: String = "",
    val fromShort: String = "",
    val toShort: String = "",
    val time: String = "",
    val classSeat: String = "",
    val status: String = "SCHEDULED" // Thêm trường status, mặc định là SCHEDULED
) : Serializable