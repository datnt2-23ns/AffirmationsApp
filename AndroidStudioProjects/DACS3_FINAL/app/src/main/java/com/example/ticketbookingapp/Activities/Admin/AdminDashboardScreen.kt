package com.example.ticketbookingapp.Activities.Admin

import android.content.Intent
import android.se.omapi.Session
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Activities.Auth.LoginActivity
import com.example.ticketbookingapp.Activities.Dashboard.TopBar
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.Utils.SessionManager

@Composable
fun AdminDashboardScreen(user: UserModel) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.lightBlue),
            colorResource(R.color.lightGreyWhite)
        )
    )

    Scaffold(
        topBar = { TopBar(user = user, title = "Điều phối nhịp bay \n             tối ưu mọi kết nối.") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite)) // nền bao quanh khung
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Bảng Quản trị",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.darkBlue),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Chào mừng, ${user.fullName}",
                                fontSize = 20.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            AdminButton(
                                text = "Quản lý chuyến bay",
                                icon = Icons.Default.FlightTakeoff,
                                gradientBrush = gradientBackground,
                                isLoading = isLoading
                            ) {
                                context.startActivity(AdminFlightManagementActivity.newIntent(context, user))
                            }

                            AdminButton(
                                text = "Quản lý đặt vé",
                                icon = Icons.Default.ReceiptLong,
                                gradientBrush = gradientBackground,
                                isLoading = isLoading
                            ) {
                                context.startActivity(AdminBookingManagementActivity.newIntent(context, user))
                            }

                            AdminButton(
                                text = "Quản lý người dùng",
                                icon = Icons.Default.People,
                                gradientBrush = gradientBackground,
                                isLoading = isLoading
                            ) {
                                context.startActivity(AdminUserManagementActivity.newIntent(context, user))
                            }

                            AdminButton(
                                text = "Đăng xuất",
                                icon = Icons.Default.Logout,
                                gradientBrush = gradientBackground,
                                isLoading = isLoading
                            ) {
                                val sessionManager = SessionManager(context)
                                sessionManager.logout()
                                context.startActivity(
                                    LoginActivity.newIntent(context).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                )
                                (context as? ComponentActivity)?.finishAffinity()
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(R.color.lightBlue)
                )
            }
        }
    }
}

@Composable
fun AdminButton(
    text: String,
    icon: ImageVector,
    gradientBrush: Brush,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = { if (!isLoading) onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush, RoundedCornerShape(28.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart // căn trái
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = when (text) {
                        "Quản lý chuyến bay" -> "Biểu tượng quản lý chuyến bay"
                        "Quản lý đặt vé" -> "Biểu tượng quản lý đặt vé"
                        "Quản lý người dùng" -> "Biểu tượng quản lý người dùng"
                        "Đăng xuất" -> "Biểu tượng đăng xuất"
                        else -> null
                    },
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    val dummyUser = UserModel(
        fullName = "Quản trị viên",
        email = "admin@example.com"
    )
    AdminDashboardScreen(user = dummyUser)
}

object AdminStatisticsActivity {
    fun newIntent(context: android.content.Context, user: UserModel): Intent {
        return Intent(context, AdminStatisticsActivity::class.java).apply {
            putExtra("user", user)
        }
    }
}