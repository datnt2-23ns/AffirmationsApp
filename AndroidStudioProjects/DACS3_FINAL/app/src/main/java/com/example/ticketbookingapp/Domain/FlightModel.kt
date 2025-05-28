package com.example.ticketbookingapp.Domain

import com.google.firebase.database.PropertyName
import java.io.Serializable

data class FlightModel(
    @PropertyName("flightId") var FlightId: String = "",
    @PropertyName("airlineLogo") var AirlineLogo: String = "",
    @PropertyName("airlineName") var AirlineName: String = "",
    @PropertyName("arriveTime") var ArriveTime: String = "",
    @PropertyName("classSeat") var ClassSeat: String = "",
    @PropertyName("typeClass") var TypeClass: String = "",
    @PropertyName("date") var Date: String = "",
    @PropertyName("from") var From: String = "",
    @PropertyName("fromShort") var FromShort: String = "",
    @PropertyName("numberSeat") var NumberSeat: Int = 0,
    @PropertyName("price") var Price: Double = 0.0,
    @PropertyName("passenger") var Passenger: String = "",
    @PropertyName("seats") var Seats: String = "",
    @PropertyName("reservedSeats") var ReservedSeats: String = "",
    @PropertyName("time") var Time: String = "",
    @PropertyName("to") var To: String = "",
    @PropertyName("toShort") var ToShort: String = "",
    @PropertyName("bookingTime") var bookingTime: String = "",
    @PropertyName("status") var status: String = "SCHEDULED" // Thêm trường status
) : Serializable