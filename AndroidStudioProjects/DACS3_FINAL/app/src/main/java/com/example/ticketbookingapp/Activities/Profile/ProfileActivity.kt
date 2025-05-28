package com.example.ticketbookingapp.Activities.Profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.ViewModel.AuthViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authViewModel = AuthViewModel()

        // Lấy UserModel từ Intent
        val user = intent.getSerializableExtra("user") as? UserModel
        if (user == null) {
            Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            StatusTopBarColor()
            ProfileScreen(
                user = user,
                authViewModel = authViewModel,
                onUpdateSuccess = {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    // Có thể quay lại Dashboard nếu cần
                    // val intent = DashboardActivity.newIntent(this).apply {
                    //     putExtra("user", user)
                    // }
                    // startActivity(intent)
                    // finish()
                },
                onUpdateFailed = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}