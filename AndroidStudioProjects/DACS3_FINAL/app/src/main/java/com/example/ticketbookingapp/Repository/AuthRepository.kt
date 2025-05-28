package com.example.ticketbookingapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ticketbookingapp.Activities.Auth.AuthResult
import com.example.ticketbookingapp.Domain.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

class AuthRepository {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("Users")
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "AuthRepository"

    fun login(identifier: String, password: String): LiveData<AuthResult> {
        val result = MutableLiveData<AuthResult>()
        Log.d(TAG, "Starting login query for identifier: $identifier")

        // Kiểm tra identifier trong Realtime Database
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Received snapshot with ${snapshot.childrenCount} users")
                var userFound = false
                var userEmail: String? = null
                var user: UserModel? = null

                for (userSnapshot in snapshot.children) {
                    val currentUser = userSnapshot.getValue(UserModel::class.java)
                    if (currentUser == null) {
                        Log.e(TAG, "Failed to parse user data for snapshot: ${userSnapshot.key}")
                        continue
                    }
                    Log.d(TAG, "Checking user: ${currentUser.username}, email: ${currentUser.email}")
                    if (currentUser.username == identifier || currentUser.email == identifier) {
                        userFound = true
                        userEmail = currentUser.email
                        user = currentUser
                        Log.d(TAG, "User found: ${currentUser.username}, email: $userEmail, isDemo: ${currentUser.isDemo}")
                        break
                    }
                }

                if (!userFound || userEmail == null || user == null) {
                    Log.d(TAG, "No user found with identifier: $identifier")
                    result.value = AuthResult.Failure("Tên người dùng hoặc email không tồn tại")
                    return
                }

                // Kiểm tra xem đây là tài khoản demo hay thực
                if (user.isDemo) {
                    // Tài khoản demo: Xác thực qua Realtime Database
                    Log.d(TAG, "Demo account detected for email: $userEmail")
                    if (user.password == password) {
                        Log.d(TAG, "Demo account password matched for user: ${user.username}")
                        result.value = AuthResult.Success(user)
                    } else {
                        Log.d(TAG, "Demo account password mismatch for user: ${user.username}")
                        result.value = AuthResult.Failure("Sai mật khẩu")
                    }
                } else {
                    // Tài khoản thực: Xác thực qua Firebase Authentication
                    Log.d(TAG, "Attempting Firebase Authentication with email: $userEmail")
                    auth.signInWithEmailAndPassword(userEmail, password)
                        .addOnSuccessListener {
                            Log.d(TAG, "Firebase Authentication successful for email: $userEmail")
                            // Cập nhật mật khẩu trong Realtime Database nếu cần
                            updatePasswordInDatabase(user, password)
                            Log.d(TAG, "User authenticated and password updated: ${user.username}")
                            result.value = AuthResult.Success(user)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Firebase Authentication failed: ${e.message}", e)
                            result.value = AuthResult.Failure(
                                when {
                                    e.message?.contains("password is invalid") == true -> "Sai mật khẩu"
                                    e.message?.contains("no user record") == true -> "Email không được đăng ký trong Firebase Authentication"
                                    else -> "Lỗi xác thực: ${e.message}"
                                }
                            )
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Login query cancelled: ${error.message}")
                result.value = AuthResult.Failure("Lỗi mạng: ${error.message}")
            }
        })

        return result
    }

    fun register(username: String, password: String, email: String, role: String = "user", isDemo: Boolean = false): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        usersRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Username $username already exists")
                    result.value = false
                } else {
                    usersRef.orderByChild("email").equalTo(email)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(emailSnapshot: DataSnapshot) {
                                if (emailSnapshot.exists()) {
                                    Log.d(TAG, "Email $email already exists")
                                    result.value = false
                                } else {
                                    // Nếu là tài khoản demo, chỉ lưu vào Realtime Database
                                    if (isDemo) {
                                        val user = UserModel(
                                            username = username,
                                            password = password,
                                            role = role,
                                            email = email,
                                            phoneNumber = "",
                                            isDemo = true
                                        )
                                        usersRef.child(username).setValue(user)
                                            .addOnSuccessListener {
                                                Log.d(TAG, "Demo user $username registered successfully")
                                                result.value = true
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Failed to save demo user to database: ${e.message}")
                                                result.value = false
                                            }
                                    } else {
                                        // Tài khoản thực: Đăng ký với Firebase Authentication
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnSuccessListener {
                                                val user = UserModel(
                                                    username = username,
                                                    password = password,
                                                    role = role,
                                                    email = email,
                                                    phoneNumber = "",
                                                    isDemo = false
                                                )
                                                usersRef.child(username).setValue(user)
                                                    .addOnSuccessListener {
                                                        Log.d(TAG, "User $username registered successfully")
                                                        result.value = true
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.e(TAG, "Failed to save user to database: ${e.message}")
                                                        result.value = false
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Failed to register user in Firebase Auth: ${e.message}")
                                                result.value = false
                                            }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Register query cancelled: ${error.message}")
                                result.value = false
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Username check cancelled: ${error.message}")
                result.value = false
            }
        })
        return result
    }

    fun sendPasswordResetEmail(email: String): LiveData<String> {
        val result = MutableLiveData<String>()
        usersRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        val user = userSnapshot.getValue(UserModel::class.java)
                        if (user == null) {
                            Log.e(TAG, "Failed to parse user data for email: $email")
                            result.value = "Lỗi: Dữ liệu người dùng không hợp lệ"
                            return
                        }
                        // Chỉ gửi email đặt lại mật khẩu cho tài khoản thực
                        if (user.isDemo) {
                            Log.d(TAG, "Cannot send password reset for demo email: $email")
                            result.value = "Tài khoản demo không hỗ trợ đặt lại mật khẩu."
                        } else {
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Password reset email sent to $email")
                                    result.value = "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn."
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to send password reset email: ${e.message}")
                                    result.value = "Lỗi: ${e.message}"
                                }
                        }
                    } else {
                        Log.d(TAG, "Email $email not found in database")
                        result.value = "Email không tồn tại trong hệ thống."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Email check cancelled: ${error.message}")
                    result.value = "Lỗi mạng: ${error.message}"
                }
            })
        return result
    }

    private fun updatePasswordInDatabase(user: UserModel, newPassword: String) {
        if (!user.isDemo && user.password != newPassword) {
            usersRef.child(user.username).child("password").setValue(newPassword)
                .addOnSuccessListener {
                    Log.d(TAG, "Password updated in database for user: ${user.username}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to update password in database: ${e.message}")
                }
        }
    }
}