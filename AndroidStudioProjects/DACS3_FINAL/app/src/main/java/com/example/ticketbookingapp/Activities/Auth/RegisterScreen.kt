package com.example.ticketbookingapp.Activities.Auth

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.ViewModel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Nền trắng
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Đăng ký",
                fontSize = 32.sp,
                color = colorResource(R.color.darkBlue), // Văn bản darkBlue
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it.trim() },
                label = { Text("Tên người dùng") },
                placeholder = { Text("Nhập tên người dùng") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White, // Nền trắng
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.darkBlue),
                    unfocusedTextColor = colorResource(R.color.darkBlue),
                    focusedLabelColor = colorResource(R.color.darkBlue),
                    unfocusedLabelColor = colorResource(R.color.darkBlue),
                    focusedBorderColor = colorResource(R.color.lightBlue), // Viền lightBlue
                    unfocusedBorderColor = colorResource(R.color.lightGrey) // Viền lightGrey
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Email") },
                placeholder = { Text("Nhập email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.darkBlue),
                    unfocusedTextColor = colorResource(R.color.darkBlue),
                    focusedLabelColor = colorResource(R.color.darkBlue),
                    unfocusedLabelColor = colorResource(R.color.darkBlue),
                    focusedBorderColor = colorResource(R.color.lightBlue),
                    unfocusedBorderColor = colorResource(R.color.lightGrey)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it.trim() },
                label = { Text("Mật khẩu") },
                placeholder = { Text("Nhập mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.darkBlue),
                    unfocusedTextColor = colorResource(R.color.darkBlue),
                    focusedLabelColor = colorResource(R.color.darkBlue),
                    unfocusedLabelColor = colorResource(R.color.darkBlue),
                    focusedBorderColor = colorResource(R.color.lightBlue),
                    unfocusedBorderColor = colorResource(R.color.lightGrey)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it.trim() },
                label = { Text("Xác nhận mật khẩu") },
                placeholder = { Text("Nhập lại mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.darkBlue),
                    unfocusedTextColor = colorResource(R.color.darkBlue),
                    focusedLabelColor = colorResource(R.color.darkBlue),
                    unfocusedLabelColor = colorResource(R.color.darkBlue),
                    focusedBorderColor = colorResource(R.color.lightBlue),
                    unfocusedBorderColor = colorResource(R.color.lightGrey)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Register button
            GradientButton(
                onClick = {
                    if (isLoading) return@GradientButton
                    if (username.length < 6) {
                        errorMessage = "Tên người dùng phải có ít nhất 6 ký tự"
                        return@GradientButton
                    }

                    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                    if (!email.matches(emailRegex)) {
                        errorMessage = "Định dạng email không hợp lệ"
                        return@GradientButton
                    }

                    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{6,}$")
                    if (!password.matches(passwordRegex)) {
                        errorMessage = "Mật khẩu phải chứa chữ hoa, số và ký tự đặc biệt"
                        return@GradientButton
                    }

                    if (password != confirmPassword) {
                        errorMessage = "Mật khẩu không khớp"
                        return@GradientButton
                    }

                    isLoading = true
                    authViewModel.register(username, password, email).observeForever { success ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        } else {
                            errorMessage = "Tên người dùng hoặc email đã tồn tại"
                        }
                    }
                },
                text = "Đăng ký",
                padding = 0,
                gradientColors = listOf(
                    colorResource(R.color.lightBlue), // Gradient từ lightBlue
                    colorResource(R.color.mediumBlue) // đến mediumBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigate to Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Đã có tài khoản? ",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 14.sp
                )
                Text(
                    text = "Đăng nhập",
                    color = colorResource(R.color.lightBlue), // Link màu lightBlue
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        if (!isLoading) onNavigateToLogin()
                    }
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(R.color.lightBlue) // Progress màu lightBlue
            )
        }
    }
}