package com.example.ticketbookingapp.Activities.Auth

import com.example.ticketbookingapp.Domain.UserModel

sealed class AuthResult {
    data class Success(val user: UserModel) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}