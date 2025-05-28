package com.example.ticketbookingapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketbookingapp.Domain.BookingModel
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.Repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BookingWithMetadata(
    val booking: BookingModel,
    val bookingId: String,
    val userId: String
)

class AdminViewModel : ViewModel() {
    private val adminRepository = AdminRepository()

    private val _flights = MutableStateFlow<List<FlightModel>>(emptyList())
    val flights: StateFlow<List<FlightModel>> = _flights

    private val _bookings = MutableStateFlow<List<BookingWithMetadata>>(emptyList())
    val bookings: StateFlow<List<BookingWithMetadata>> = _bookings

    private val _users = MutableStateFlow<List<UserModel>>(emptyList())
    val users: StateFlow<List<UserModel>> = _users

    init {
        loadFlights()
        loadBookings()
        loadUsers()
    }

    private fun loadFlights() {
        viewModelScope.launch {
            try {
                _flights.value = adminRepository.getAllFlights()
                println("Loaded flights: ${_flights.value.map { it.FlightId to it.Time }}") // Log để debug
            } catch (e: Exception) {
                println("Error loading flights: ${e.message}")
            }
        }
    }

    private fun loadBookings() {
        viewModelScope.launch {
            try {
                _bookings.value = adminRepository.getAllBookings().filter { it.userId != "admin_123" }
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            try {
                _users.value = adminRepository.getAllUsers().filter { it.role == "user" }
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    fun deleteFlight(flightId: String) {
        viewModelScope.launch {
            try {
                adminRepository.deleteFlight(flightId)
                _flights.value = _flights.value.filter { it.FlightId != flightId }
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    fun cancelBooking(userId: String, bookingId: String) {
        viewModelScope.launch {
            try {
                adminRepository.cancelBooking(userId, bookingId)
                _bookings.value = _bookings.value.filter { it.bookingId != bookingId || it.userId != userId }
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                adminRepository.deleteUser(userId)
                _users.value = _users.value.filter { it.username != userId }
                _bookings.value = _bookings.value.filter { it.userId != userId }
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    fun addFlight(flight: FlightModel) {
        viewModelScope.launch {
            try {
                adminRepository.addFlight(flight)
                _flights.value = _flights.value + flight
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    fun updateFlight(flight: FlightModel) {
        viewModelScope.launch {
            try {
                println("Sending flight to repository with Time: ${flight.Time}") // Log để debug
                adminRepository.updateFlight(flight)
                loadFlights() // Làm mới danh sách từ Firebase
            } catch (e: Exception) {
                println("Error updating flight: ${e.message}")
                throw e // Ném lỗi để hiển thị Toast trong giao diện
            }
        }
    }
}