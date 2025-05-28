package com.example.ticketbookingapp.Activities.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ticketbookingapp.Activities.BookingHistory.BookingHistoryActivity
import com.example.ticketbookingapp.Activities.Profile.ProfileActivity
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R

@Composable
fun MyBottomBar(user: UserModel, currentScreen: String) {
    val context = LocalContext.current

    NavigationBar(
        modifier = Modifier.background(colorResource(R.color.lightGreyWhite)), // Nền xám trắng
        containerColor = colorResource(R.color.lightGreyWhite)
    ) {
        // Dashboard (bottom_btn1)
        NavigationBarItem(
            selected = currentScreen == "Dashboard",
            onClick = {
                if (currentScreen != "Dashboard") {
                    val intent = DashboardActivity.newIntent(context).apply {
                        putExtra("user", user)
                    }
                    context.startActivity(intent)
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bottom_btn1),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (currentScreen == "Dashboard") colorResource(R.color.lightBlue) else colorResource(R.color.darkBlue)
                )
            }
        )

        // Booking History (bottom_btn2)
        NavigationBarItem(
            selected = currentScreen == "BookingHistory",
            onClick = {
                if (currentScreen != "BookingHistory") {
                    val intent = BookingHistoryActivity.newIntent(context).apply {
                        putExtra("user", user)
                    }
                    context.startActivity(intent)
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bottom_btn3),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (currentScreen == "BookingHistory") colorResource(R.color.lightBlue) else colorResource(R.color.darkBlue)
                )
            }
        )

        // Profile (bottom_btn4)
        NavigationBarItem(
            selected = currentScreen == "Profile",
            onClick = {
                if (currentScreen != "Profile") {
                    val intent = ProfileActivity.newIntent(context).apply {
                        putExtra("user", user)
                    }
                    context.startActivity(intent)
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bottom_btn4),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (currentScreen == "Profile") colorResource(R.color.lightBlue) else colorResource(R.color.darkBlue)
                )
            }
        )
    }
}