package com.example.ticketbookingapp.Domain

import java.io.Serializable

data class UserModel(
    val username: String = "",
    val password: String = "",
    val role: String = "user",
    val email: String = "",
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val isDemo: Boolean = true
) : Serializable