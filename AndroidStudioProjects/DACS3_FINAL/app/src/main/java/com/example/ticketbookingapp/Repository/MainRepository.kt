package com.example.ticketbookingapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ticketbookingapp.Domain.BookingModel
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.LocationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val bookingsRef = firebaseDatabase.getReference("Bookings")
    private val flightsRef = firebaseDatabase.getReference("Flights")

    // Tải danh sách địa điểm
    fun loadLocation(): LiveData<MutableList<LocationModel>> {
        val listData = MutableLiveData<MutableList<LocationModel>>()
        val ref = firebaseDatabase.getReference("Locations")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<LocationModel>()
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        try {
                            val item = childSnapshot.getValue(LocationModel::class.java)
                            item?.let {
                                list.add(it)
                            } ?: run {
                                println("Failed to parse LocationModel: ${childSnapshot.key}")
                            }
                        } catch (e: Exception) {
                            println("Error parsing LocationModel: ${e.message}")
                        }
                    }
                    println("Locations loaded: ${list.size} items")
                    listData.value = list
                } else {
                    println("Locations node is empty or does not exist")
                    listData.value = mutableListOf()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error in loadLocation: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    // Tải danh sách chuyến bay được lọc
    fun loadFiltered(
        from: String,
        to: String,
        departureDate: String,
        typeClass: String,
        numPassenger: Int = 1
    ): LiveData<MutableList<FlightModel>> {
        val listData = MutableLiveData<MutableList<FlightModel>>()
        val ref = firebaseDatabase.getReference("Flights")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<FlightModel>()
                println("Query snapshot exists: ${snapshot.exists()}, children count: ${snapshot.childrenCount}")
                if (snapshot.exists()) {
                    snapshot.children.forEachIndexed { index, childSnapshot ->
                        try {
                            val list = childSnapshot.getValue(FlightModel::class.java)
                            if (list != null) {
                                println("Flight [$index] raw data: ${childSnapshot.value}")
                                println("Flight [$index] parsed: From='${list.From}', To='${list.To}', Date='${list.Date}', TypeClass='${list.TypeClass}', NumberSeat=${list.NumberSeat}, ReservedSeats='${list.ReservedSeats}'")
                                val normalizedFrom = list.From.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val normalizedTo = list.To.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val normalizedDate = list.Date.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val normalizedTypeClass = list.TypeClass.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val inputFrom = from.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val inputTo = to.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val inputDate = departureDate.trim().replace("\\s+".toRegex(), " ").lowercase()
                                val inputTypeClass = typeClass.trim().replace("\\s+".toRegex(), " ").lowercase()

                                if (normalizedFrom == inputFrom &&
                                    normalizedTo == inputTo &&
                                    normalizedDate == inputDate &&
                                    normalizedTypeClass == inputTypeClass
                                ) {
                                    val reservedSeats = list.ReservedSeats
                                        .split(",")
                                        .filter { it.isNotBlank() }
                                        .toSet()
                                    val availableSeats = list.NumberSeat - reservedSeats.size
                                    if (availableSeats >= numPassenger) {
                                        lists.add(list)
                                        println("Flight [$index] added: ${list.AirlineName}, Seats available=$availableSeats")
                                    } else {
                                        println("Flight [$index] excluded: Not enough seats (available=$availableSeats, required=$numPassenger)")
                                    }
                                } else {
                                    println("Flight [$index] not matched: From='$normalizedFrom' vs '$inputFrom', To='$normalizedTo' vs '$inputTo', Date='$normalizedDate' vs '$inputDate', TypeClass='$normalizedTypeClass' vs '$inputTypeClass'")
                                }
                            } else {
                                println("Failed to parse FlightModel at index $index: ${childSnapshot.key}")
                            }
                        } catch (e: Exception) {
                            println("Error parsing FlightModel at index $index: ${e.message}")
                        }
                    }
                    println("Filtered flights loaded: ${lists.size} items")
                } else {
                    println("Flights node is empty or does not exist")
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error in loadFiltered: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    // Kiểm tra xem đặt vé đã tồn tại chưa
    suspend fun checkBookingExists(username: String, flightId: String, seats: String): Boolean = suspendCoroutine { continuation ->
        bookingsRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var exists = false
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    if (booking != null && booking.flightId == flightId && booking.seats == seats) {
                        exists = true
                        break
                    }
                }
                continuation.resume(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    // Lưu thông tin đặt vé mới
    suspend fun saveBooking(username: String, booking: BookingModel): Result<Unit> {
        return try {
            val newBookingRef = bookingsRef.child(username).push()
            newBookingRef.setValue(booking).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cập nhật danh sách ghế đã đặt trong Flights
    suspend fun updateFlightReservedSeats(flightId: String, newSeats: String): Result<Unit> {
        return try {
            val flightSnapshot = flightsRef.get().await()
            if (flightSnapshot.exists()) {
                for (child in flightSnapshot.children) {
                    val flight = child.getValue(FlightModel::class.java)
                    if (flight != null && flight.FlightId == flightId) {
                        val currentSeats = flight.ReservedSeats
                        val updatedSeats = if (currentSeats.isEmpty()) newSeats else "$currentSeats,$newSeats"
                        child.ref.child("reservedSeats").setValue(updatedSeats).await()
                        return Result.success(Unit)
                    }
                }
                Result.failure(Exception("Flight with ID $flightId not found"))
            } else {
                Result.failure(Exception("Flights node is empty"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Xóa thông tin đặt vé
    suspend fun deleteBooking(username: String, flightId: String, seats: String): Result<Unit> {
        return try {
            val bookingSnapshot = bookingsRef.child(username).get().await()
            if (bookingSnapshot.exists()) {
                for (child in bookingSnapshot.children) {
                    val booking = child.getValue(BookingModel::class.java)
                    if (booking != null && booking.flightId == flightId && booking.seats == seats) {
                        child.ref.removeValue().await()
                        return Result.success(Unit)
                    }
                }
                Result.failure(Exception("Booking not found for flightId: $flightId, seats: $seats"))
            } else {
                Result.failure(Exception("Bookings node for user $username is empty"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}