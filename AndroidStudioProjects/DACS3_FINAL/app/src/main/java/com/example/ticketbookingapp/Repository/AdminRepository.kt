package com.example.ticketbookingapp.Repository

import com.example.ticketbookingapp.Domain.BookingModel
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.ViewModel.BookingWithMetadata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AdminRepository {
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun getAllFlights(): List<FlightModel> {
        val snapshot = database.child("Flights").get().await()
        return snapshot.children.mapNotNull { it.getValue(FlightModel::class.java) }
    }

    suspend fun deleteFlight(flightId: String) {
        val snapshot = database.child("Flights").get().await()
        snapshot.children.forEachIndexed { index, dataSnapshot ->
            val flight = dataSnapshot.getValue(FlightModel::class.java)
            if (flight?.FlightId == flightId) {
                database.child("Flights").child(index.toString()).removeValue().await()
            }
        }
    }

    suspend fun getAllBookings(): List<BookingWithMetadata> {
        val usersSnapshot = database.child("Users").get().await()
        val userIds = usersSnapshot.children
            .mapNotNull { it.getValue(UserModel::class.java) }
            .filter { it.role == "user" }
            .map { it.username }

        val snapshot = database.child("Bookings").get().await()
        val bookings = mutableListOf<BookingWithMetadata>()
        snapshot.children.forEach { userSnapshot ->
            val userId = userSnapshot.key ?: return@forEach
            if (userId in userIds) {
                userSnapshot.children.forEach { bookingSnapshot ->
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    val bookingId = bookingSnapshot.key ?: return@forEach
                    if (booking != null) {
                        bookings.add(
                            BookingWithMetadata(
                                booking = booking,
                                bookingId = bookingId,
                                userId = userId
                            )
                        )
                    }
                }
            }
        }
        return bookings
    }

    suspend fun cancelBooking(userId: String, bookingId: String) {
        database.child("Bookings").child(userId).child(bookingId).removeValue().await()
    }

    suspend fun getAllUsers(): List<UserModel> {
        val snapshot = database.child("Users").get().await()
        return snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
    }

    suspend fun deleteUser(userId: String) {
        database.child("Users").child(userId).removeValue().await()
        database.child("Bookings").child(userId).removeValue().await()
    }

    suspend fun addFlight(flight: FlightModel) {
        val snapshot = database.child("Flights").get().await()
        val currentFlights = snapshot.children.mapNotNull { it.getValue(FlightModel::class.java) }
        val newFlights = currentFlights + flight
        database.child("Flights").setValue(newFlights).await()
    }

    suspend fun updateFlight(flight: FlightModel) {
        val snapshot = database.child("Flights").get().await()
        snapshot.children.forEachIndexed { index, dataSnapshot ->
            val currentFlight = dataSnapshot.getValue(FlightModel::class.java)
            if (currentFlight?.FlightId == flight.FlightId) {
                database.child("Flights").child(index.toString()).setValue(flight).await()
            }
        }
    }
}