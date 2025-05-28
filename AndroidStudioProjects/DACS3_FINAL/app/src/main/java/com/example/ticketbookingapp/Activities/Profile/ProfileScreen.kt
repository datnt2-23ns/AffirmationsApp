package com.example.ticketbookingapp.Activities.Profile

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Activities.Dashboard.MyBottomBar
import com.example.ticketbookingapp.Activities.Dashboard.BlueTitle
import com.example.ticketbookingapp.Activities.Dashboard.TopBar
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.Activities.Splash.SplashActivity
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.Utils.SessionManager
import com.example.ticketbookingapp.ViewModel.AuthViewModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ProfileScreen(
    user: UserModel,
    authViewModel: AuthViewModel,
    onUpdateSuccess: () -> Unit,
    onUpdateFailed: (String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var fullName by remember { mutableStateOf(user.fullName) }
    var email by remember { mutableStateOf(user.email) }
    var dateOfBirth by remember { mutableStateOf(user.dateOfBirth) }
    var gender by remember { mutableStateOf(user.gender) }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber) }

    var isEditing by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isChangingPassword by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { MyBottomBar(user = user, currentScreen = "Profile") },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite)) // Nền xám trắng
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Hồ sơ cá nhân",
                    fontSize = 25.sp, // Tăng kích thước chữ lên 25sp
                    fontWeight = FontWeight.Bold, // Chữ đậm
                    color = colorResource(R.color.darkBlue),
                    textAlign = TextAlign.Center, // Căn giữa
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp) // Padding để căn chỉnh
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp) // Padding cho form
                        .background(
                            color = Color.White, // Nền trắng
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                ) {
                    BlueTitle("Thông tin cá nhân")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { if (isEditing) fullName = it },
                        label = { Text("Họ và tên", fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White, // Nền trắng
                                shape = RoundedCornerShape(8.dp)
                            ),
                        enabled = isEditing,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.lightBlue),
                            unfocusedBorderColor = colorResource(R.color.lightBlue),
                            disabledTextColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = colorResource(R.color.lightBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (isEditing) email = it },
                        label = { Text("Email", fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        enabled = isEditing,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.lightBlue),
                            unfocusedBorderColor = colorResource(R.color.lightBlue),
                            disabledTextColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = colorResource(R.color.lightBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date of Birth
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { if (isEditing) dateOfBirth = it },
                        label = { Text("Ngày sinh (dd/mm/yyyy)", fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        enabled = isEditing,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.lightBlue),
                            unfocusedBorderColor = colorResource(R.color.lightBlue),
                            disabledTextColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = colorResource(R.color.lightBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender
                    var expanded by remember { mutableStateOf(false) }
                    val genderOptions = listOf("Nam", "Nữ", "Khác")
                    Box {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            label = { Text("Giới tính", fontWeight = FontWeight.Bold) },
                            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colorResource(R.color.darkBlue),
                                unfocusedTextColor = colorResource(R.color.darkBlue),
                                focusedLabelColor = colorResource(R.color.darkBlue),
                                unfocusedLabelColor = colorResource(R.color.darkBlue),
                                focusedBorderColor = colorResource(R.color.lightBlue),
                                unfocusedBorderColor = colorResource(R.color.lightBlue),
                                disabledTextColor = colorResource(R.color.darkBlue),
                                disabledLabelColor = colorResource(R.color.darkBlue),
                                disabledBorderColor = colorResource(R.color.lightBlue)
                            )
                        )
                        if (isEditing) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                genderOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option,
                                                color = colorResource(R.color.darkBlue),
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            gender = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { expanded = true }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (isEditing) phoneNumber = it },
                        label = { Text("Số điện thoại", fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        enabled = isEditing,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.lightBlue),
                            unfocusedBorderColor = colorResource(R.color.lightBlue),
                            disabledTextColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = colorResource(R.color.lightBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Update Button
                    GradientButton(
                        onClick = {
                            if (!isEditing) {
                                isEditing = true
                            } else {
                                // Validate inputs
                                if (fullName.isEmpty()) {
                                    onUpdateFailed("Họ và tên không được để trống")
                                    return@GradientButton
                                }
                                val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                                if (!email.matches(emailRegex)) {
                                    onUpdateFailed("Định dạng email không hợp lệ")
                                    return@GradientButton
                                }
                                if (dateOfBirth.isEmpty()) {
                                    onUpdateFailed("Ngày sinh không được để trống")
                                    return@GradientButton
                                }
                                if (gender.isEmpty()) {
                                    onUpdateFailed("Giới tính không được để trống")
                                    return@GradientButton
                                }
                                val phoneRegex = Regex("^0[0-9]{9}$")
                                if (!phoneNumber.matches(phoneRegex)) {
                                    onUpdateFailed("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
                                    return@GradientButton
                                }

                                // Update user info in Firebase
                                val updatedUser = UserModel(
                                    username = user.username,
                                    password = user.password,
                                    role = user.role,
                                    email = email,
                                    fullName = fullName,
                                    dateOfBirth = dateOfBirth,
                                    gender = gender,
                                    phoneNumber = phoneNumber
                                )
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(user.username)
                                    .setValue(updatedUser)
                                    .addOnSuccessListener {
                                        isEditing = false
                                        val sessionManager = SessionManager(context)
                                        sessionManager.saveUser(updatedUser)
                                        onUpdateSuccess()
                                    }
                                    .addOnFailureListener {
                                        onUpdateFailed("Cập nhật hồ sơ thất bại")
                                    }
                            }
                        },
                        text = if (isEditing) "Lưu" else "Cập nhật hồ sơ",
                        gradientColors = listOf(
                            colorResource(R.color.lightBlue),
                            colorResource(R.color.mediumBlue)
                        )
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp) // Padding cho form
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                ) {
                    BlueTitle("Thông tin tài khoản")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username
                    OutlinedTextField(
                        value = user.username,
                        onValueChange = {},
                        label = { Text("Tên người dùng", fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.lightBlue),
                            unfocusedBorderColor = colorResource(R.color.lightBlue),
                            disabledTextColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = colorResource(R.color.lightBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = "******",
                        onValueChange = {},
                        label = { Text("Mật khẩu", fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.lightBlue),
                            unfocusedBorderColor = colorResource(R.color.lightBlue),
                            disabledTextColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = colorResource(R.color.lightBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Change Password Button
                    GradientButton(
                        onClick = { isChangingPassword = true },
                        text = "Đổi mật khẩu",
                        gradientColors = listOf(
                            colorResource(R.color.lightBlue),
                            colorResource(R.color.mediumBlue)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout Button
                    GradientButton(
                        onClick = { showLogoutDialog = true },
                        text = "Đăng xuất",
                        gradientColors = listOf(
                            colorResource(R.color.lightBlue),
                            colorResource(R.color.mediumBlue)
                        )
                    )
                }
            }

            // Change Password Dialog
            if (isChangingPassword) {
                item {
                    AlertDialog(
                        onDismissRequest = { isChangingPassword = false },
                        title = {
                            Text(
                                "Đổi mật khẩu",
                                color = colorResource(R.color.darkBlue),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .background(
                                        color = Color.White, // Nền trắng
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    label = { Text("Mật khẩu mới", fontWeight = FontWeight.Bold) },
                                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.White, // Nền trắng
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = colorResource(R.color.darkBlue),
                                        unfocusedTextColor = colorResource(R.color.darkBlue),
                                        focusedLabelColor = colorResource(R.color.darkBlue),
                                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                                        focusedBorderColor = colorResource(R.color.lightBlue),
                                        unfocusedBorderColor = colorResource(R.color.lightBlue)
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = {
                                        Text(
                                            "Xác nhận mật khẩu",
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Normal),
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.White, // Nền trắng
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = colorResource(R.color.darkBlue),
                                        unfocusedTextColor = colorResource(R.color.darkBlue),
                                        focusedLabelColor = colorResource(R.color.darkBlue),
                                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                                        focusedBorderColor = colorResource(R.color.lightBlue),
                                        unfocusedBorderColor = colorResource(R.color.lightBlue)
                                    )
                                )
                            }
                        },
                        confirmButton = {
                            GradientButton(
                                onClick = {
                                    val passwordRegex =
                                        Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{6,}$")
                                    if (!newPassword.matches(passwordRegex)) {
                                        onUpdateFailed("Mật khẩu phải chứa chữ hoa, số và ký tự đặc biệt")
                                        return@GradientButton
                                    }
                                    if (newPassword != confirmPassword) {
                                        onUpdateFailed("Mật khẩu không khớp")
                                        return@GradientButton
                                    }

                                    // Update password in Firebase
                                    val updatedUser = UserModel(
                                        username = user.username,
                                        password = newPassword,
                                        role = user.role,
                                        email = user.email,
                                        fullName = user.fullName,
                                        dateOfBirth = user.dateOfBirth,
                                        gender = gender,
                                        phoneNumber = user.phoneNumber
                                    )
                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(user.username)
                                        .setValue(updatedUser)
                                        .addOnSuccessListener {
                                            val sessionManager = SessionManager(context)
                                            sessionManager.saveUser(updatedUser)
                                            isChangingPassword = false
                                            newPassword = ""
                                            confirmPassword = ""
                                            onUpdateSuccess()
                                        }
                                        .addOnFailureListener {
                                            onUpdateFailed("Đổi mật khẩu thất bại")
                                        }
                                },
                                text = "Lưu",
                                gradientColors = listOf(
                                    colorResource(R.color.lightBlue),
                                    colorResource(R.color.mediumBlue)
                                )
                            )
                        },
                        dismissButton = {
                            TextButton(onClick = { isChangingPassword = false }) {
                                Text(
                                    "Hủy",
                                    color = colorResource(R.color.darkBlue),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        containerColor = Color.White // Nền trắng
                    )
                }
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                item {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = {
                            Text(
                                "Đăng xuất",
                                color = colorResource(R.color.darkBlue),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                "Bạn có chắc chắn muốn đăng xuất?",
                                color = colorResource(R.color.darkBlue),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        confirmButton = {
                            GradientButton(
                                onClick = {
                                    sessionManager.logout()
                                    // Navigate to SplashActivity
                                    val intent = Intent(context, SplashActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    // Finish current activity
                                    (context as? ProfileActivity)?.finish()
                                },
                                text = "Có",
                                gradientColors = listOf(
                                    colorResource(R.color.lightBlue),
                                    colorResource(R.color.mediumBlue)
                                )
                            )
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text(
                                    "Không",
                                    color = colorResource(R.color.darkBlue),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        containerColor = Color.White // Nền trắng
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}