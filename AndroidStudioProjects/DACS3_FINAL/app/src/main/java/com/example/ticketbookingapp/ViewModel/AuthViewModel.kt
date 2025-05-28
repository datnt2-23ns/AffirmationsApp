package com.example.ticketbookingapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ticketbookingapp.Activities.Auth.AuthResult
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.Repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    fun login(identifier: String, password: String): LiveData<AuthResult> {
        return authRepository.login(identifier, password)
    }

    fun register(username: String, password: String, email: String, role: String = "user", isDemo: Boolean = false): LiveData<Boolean> {
        return authRepository.register(username, password, email, role, isDemo)
    }

    fun sendPasswordResetEmail(email: String): LiveData<String> {
        return authRepository.sendPasswordResetEmail(email)
    }
}