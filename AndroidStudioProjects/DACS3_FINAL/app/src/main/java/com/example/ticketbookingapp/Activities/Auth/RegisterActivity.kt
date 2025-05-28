package com.example.ticketbookingapp.Activities.Auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.ViewModel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authViewModel = AuthViewModel()

        setContent {
            StatusTopBarColor()

            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    startActivity(LoginActivity.newIntent(this)) // Chuyển về LoginActivity
                    finish()
                },
                onRegisterSuccess = {
                    startActivity(LoginActivity.newIntent(this)) // Chuyển về LoginActivity
                    finish()
                }
            )
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }
}