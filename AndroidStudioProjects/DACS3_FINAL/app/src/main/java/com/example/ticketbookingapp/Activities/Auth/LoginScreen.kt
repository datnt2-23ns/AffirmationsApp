package com.example.ticketbookingapp.Activities.Auth

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.ViewModel.AuthViewModel
import com.example.ticketbookingapp.Utils.LoginHistoryManager

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (UserModel) -> Unit,
    onLoginFailed: (String) -> Unit
) {
    val context = LocalContext.current
    val loginHistoryManager = remember { LoginHistoryManager(context) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf<String?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val TAG = "LoginScreen"

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
                text = "Đăng nhập",
                fontSize = 32.sp,
                color = colorResource(R.color.darkBlue), // Văn bản darkBlue
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it.trim() },
                label = { Text("Tên đăng nhập hoặc Email") },
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

            OutlinedTextField(
                value = password,
                onValueChange = { password = it.trim() },
                label = { Text("Mật khẩu") },
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
                    focusedBorderColor = colorResource(R.color.lightBlue), // Viền lightBlue
                    unfocusedBorderColor = colorResource(R.color.lightGrey)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Quên mật khẩu?",
                color = colorResource(R.color.lightBlue), // Link màu lightBlue
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { showForgotPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                onClick = {
                    if (isLoading) return@GradientButton
                    if (identifier.isEmpty() || password.isEmpty()) {
                        errorMessage = "Vui lòng điền đầy đủ các trường"
                        onLoginFailed("Vui lòng điền đầy đủ các trường")
                        return@GradientButton
                    }
                    Log.d(TAG, "Đang thử đăng nhập với mã định danh: $identifier, độ dài mật khẩu: ${password.length}")
                    isLoading = true
                    try {
                        authViewModel.login(identifier, password).observe(lifecycleOwner) { result ->
                            isLoading = false
                            when (result) {
                                is AuthResult.Success -> {
                                    Log.d(TAG, "Đăng nhập thành công cho người dùng: ${result.user.username}")
                                    loginHistoryManager.addLoginAttempt(identifier, true)
                                    onLoginSuccess(result.user)
                                }
                                is AuthResult.Failure -> {
                                    Log.d(TAG, "Đăng nhập thất bại: ${result.message}")
                                    errorMessage = when {
                                        result.message == "Sai mật khẩu" -> "Mật khẩu không đúng. Vui lòng thử lại hoặc đặt lại mật khẩu."
                                        result.message == "Tên người dùng hoặc email không tồn tại" -> "Tên người dùng hoặc email không tồn tại."
                                        result.message.contains("Lỗi mạng") -> "Lỗi mạng. Vui lòng kiểm tra kết nối của bạn."
                                        result.message.contains("Email không được đăng ký trong Firebase Authentication") ->
                                            "Email chưa được đăng ký. Vui lòng kiểm tra lại."
                                        else -> result.message
                                    }
                                    onLoginFailed(errorMessage ?: "Đăng nhập thất bại")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Lỗi trong quá trình đăng nhập: ${e.message}")
                        isLoading = false
                        errorMessage = "Lỗi đăng nhập: ${e.message}"
                        onLoginFailed("Lỗi đăng nhập. Vui lòng thử lại.")
                    }
                },
                text = "Đăng nhập",
                padding = 0,
                gradientColors = listOf(
                    colorResource(R.color.lightBlue), // Gradient từ lightBlue
                    colorResource(R.color.mediumBlue) // đến mediumBlue
                )
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 14.sp
                )
                Text(
                    text = "Đăng ký",
                    color = colorResource(R.color.lightBlue), // Link màu lightBlue
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(R.color.lightBlue) // Progress màu lightBlue
            )
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            Dialog(onDismissRequest = { showForgotPasswordDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White) // Nền trắng
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Đặt lại mật khẩu",
                            fontSize = 20.sp,
                            color = colorResource(R.color.darkBlue)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it.trim() },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colorResource(R.color.darkBlue),
                                unfocusedTextColor = colorResource(R.color.darkBlue),
                                focusedLabelColor = colorResource(R.color.darkBlue),
                                unfocusedLabelColor = colorResource(R.color.darkBlue),
                                focusedBorderColor = colorResource(R.color.lightBlue), // Viền lightBlue
                                unfocusedBorderColor = colorResource(R.color.lightGrey)
                            )
                        )

                        resetMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                color = if (it.contains("Lỗi") || it.contains("không được hỗ trợ")) Color.Red else colorResource(R.color.green),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { showForgotPasswordDialog = false }) {
                                Text(
                                    text = "Hủy",
                                    color = colorResource(R.color.darkBlue)
                                )
                            }
                            GradientButton(
                                onClick = {
                                    if (resetEmail.isEmpty()) {
                                        resetMessage = "Vui lòng nhập email"
                                        return@GradientButton
                                    }
                                    authViewModel.sendPasswordResetEmail(resetEmail)
                                        .observe(lifecycleOwner) { message ->
                                            resetMessage = message
                                        }
                                },
                                text = "Gửi",
                                padding = 8,
                                gradientColors = listOf(
                                    colorResource(R.color.lightBlue),
                                    colorResource(R.color.mediumBlue)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}