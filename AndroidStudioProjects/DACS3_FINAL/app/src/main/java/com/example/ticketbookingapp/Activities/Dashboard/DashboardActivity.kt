//package com.example.ticketbookingapp.Activities.Dashboard
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.ticketbookingapp.Activities.SearchResult.SearchResultActivity
//import com.example.ticketbookingapp.Activities.Splash.GradientButton
//import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
//import com.example.ticketbookingapp.Domain.LocationModel
//import com.example.ticketbookingapp.Domain.UserModel
//import com.example.ticketbookingapp.R
//import com.example.ticketbookingapp.ViewModel.MainViewModel
//
//class DashboardActivity : AppCompatActivity() {
//    private val TAG = "DashboardActivity"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        // Lấy UserModel từ Intent
//        val user = intent.getSerializableExtra("user") as? UserModel
//        if (user == null) {
//            Log.e(TAG, "Không tìm thấy dữ liệu người dùng trong Intent")
//            Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//
//        Log.d(TAG, "Nhận được người dùng: ${user.username}")
//
//        setContent {
//            StatusTopBarColor()
//            MainScreen(user = user)
//        }
//    }
//
//    companion object {
//        fun newIntent(context: Context): Intent {
//            return Intent(context, DashboardActivity::class.java)
//        }
//    }
//}
//
//@Composable
//fun BlueTitle(text: String, modifier: Modifier = Modifier) {
//    Text(
//        text = text,
//        fontWeight = FontWeight.SemiBold,
//        color = colorResource(R.color.darkBlue),
//        fontSize = 16.sp,
//        modifier = modifier
//    )
//}
//
//@Composable
//fun MainScreen(user: UserModel) {
//    val locations = remember { mutableStateListOf<LocationModel>() }
//    val viewModel = MainViewModel()
//    var showLocationLoading by remember { mutableStateOf(true) }
//    var from by remember { mutableStateOf("") }
//    var to by remember { mutableStateOf("") }
//    var typeClass by remember { mutableStateOf("") }
//    var adultPassenger by remember { mutableStateOf("0") }
//    var childPassenger by remember { mutableStateOf("0") }
//    var departureDate by remember { mutableStateOf("") }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val TAG = "MainScreen"
//
//    LaunchedEffect(Unit) {
//        try {
//            viewModel.loadLocations().observe(lifecycleOwner) { result ->
//                Log.d(TAG, "Nhận được danh sách địa điểm: ${result?.size ?: 0}")
//                if (result != null) {
//                    locations.clear()
//                    locations.addAll(result)
//                    showLocationLoading = false
//                } else {
//                    errorMessage = "Không tải được danh sách địa điểm"
//                    showLocationLoading = false
//                }
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Lỗi tải danh sách địa điểm: ${e.message}")
//            errorMessage = "Lỗi tải danh sách địa điểm: ${e.message}"
//            showLocationLoading = false
//        }
//    }
//
//    Scaffold(
//        bottomBar = { MyBottomBar(user = user, currentScreen = "Dashboard") },
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(color = colorResource(R.color.lightGreyWhite)) // Nền lightGreyWhite
//                .padding(paddingValues = paddingValues)
//        ) {
//            item {
//                TopBar(user = user)
//            }
//
//            item {
//                Box(modifier = Modifier.fillMaxWidth()) {
//                    AutoSlider(
//                    )
//                }
//            }
//
//            item {
//                Column(
//                    modifier = Modifier
//                        .padding(horizontal = 32.dp, vertical = 16.dp) // Padding giống Code B
//                        .background(
//                            Color.White, // Nền trắng
//                            shape = RoundedCornerShape(20.dp)
//                        )
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp, horizontal = 24.dp)
//                ) {
//                    // From selection
//                    BlueTitle("Điểm đi")
//                    val locationNames = locations.map { it.Name }
//                    DropDownList(
//                        items = locationNames,
//                        loadingIcon = painterResource(R.drawable.from_ic),
//                        hint = "Chọn điểm đi",
//                        showLocationLoading = showLocationLoading,
//                        onItemSelected = { selectedItem ->
//                            from = selectedItem
//                        },
//                        modifier = Modifier.fillMaxWidth() // Thêm fillMaxWidth
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // To selection
//                    BlueTitle("Điểm đến")
//                    DropDownList(
//                        items = locationNames,
//                        loadingIcon = painterResource(R.drawable.to_ic),
//                        hint = "Chọn điểm đến",
//                        showLocationLoading = showLocationLoading,
//                        onItemSelected = { selectedItem ->
//                            to = selectedItem
//                        },
//                        modifier = Modifier.fillMaxWidth() // Thêm fillMaxWidth
//                    )
//
//                    // Passenger counter
//                    Spacer(modifier = Modifier.height(16.dp))
//                    BlueTitle("Hành khách")
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        PassengerCounter(
//                            title = "Ng.lớn",
//                            modifier = Modifier.weight(1f),
//                            onItemSelected = { value ->
//                                val newValue = value.toIntOrNull() ?: 0
//                                adultPassenger = if (newValue >= 0) newValue.toString() else "0"
//                            }
//                        )
//                        Spacer(modifier = Modifier.width(16.dp))
//                        PassengerCounter(
//                            title = "Trẻ em",
//                            modifier = Modifier.weight(1f),
//                            onItemSelected = { value ->
//                                val newValue = value.toIntOrNull() ?: 0
//                                childPassenger = if (newValue >= 0) newValue.toString() else "0"
//                            }
//                        )
//                    }
//
//                    // Departure date và Class trên cùng một hàng
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        Column(modifier = Modifier.weight(1f)) {
//                            BlueTitle("Ngày khởi hành")
//                            Spacer(modifier = Modifier.height(10.dp))
//                            DatePickerScreen(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(60.dp),
//                                onDepartureSelected = { date ->
//                                    departureDate = date
//                                },
//                                onReturnSelected = null
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(16.dp))
//                        Column(modifier = Modifier.weight(1f)) {
//                            BlueTitle("Hạng ghế")
//                            Spacer(modifier = Modifier.height(10.dp))
////                            val classItems = listOf("Business", "First", "Economy")
//                            val classItems = listOf("Thương gia", "Hạng nhất", "Phổ thông")
//                            DropDownList(
//                                items = classItems,
//                                loadingIcon = painterResource(R.drawable.seat_black_ic),
//                                hint = "Chọn hạng ghế",
//                                showLocationLoading = showLocationLoading,
//                                onItemSelected = { selectedItem ->
//                                    typeClass = selectedItem
//                                },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(60.dp)
//                            )
//                        }
//                    }
//
//                    // Search button
//                    Spacer(modifier = Modifier.height(16.dp))
//                    GradientButton(
//                        onClick = {
//                            try {
//                                if (from.isEmpty() || to.isEmpty() || departureDate.isEmpty() || typeClass.isEmpty()) {
//                                    errorMessage = "Vui lòng điền đầy đủ các trường bắt buộc"
//                                    return@GradientButton
//                                }
//                                val totalPassengers = (adultPassenger.toIntOrNull() ?: 0) +
//                                        (childPassenger.toIntOrNull() ?: 0)
//                                if (totalPassengers <= 0) {
//                                    errorMessage = "Cần ít nhất một hành khách"
//                                    return@GradientButton
//                                }
//                                val intent = Intent(context, SearchResultActivity::class.java).apply {
//                                    putExtra("from", from)
//                                    putExtra("to", to)
//                                    putExtra("departureDate", departureDate)
//                                    putExtra("typeClass", typeClass)
//                                    putExtra("numPassenger", totalPassengers.toString())
//                                    putExtra("user", user) // Thêm UserModel
//                                }
//                                context.startActivity(intent)
//                            } catch (e: Exception) {
//                                Log.e(TAG, "Lỗi khởi chạy SearchResultActivity: ${e.message}")
//                                errorMessage = "Lỗi: Không thể tìm kiếm chuyến bay"
//                            }
//                        },
//                        text = "Tìm kiếm",
//                        padding = 0,
//                        gradientColors = listOf(
//                            colorResource(R.color.lightBlue), // Gradient từ lightBlue
//                            colorResource(R.color.mediumBlue) // đến mediumBlue
//                        )
//                    )
//
//                    // Error message
//                    errorMessage?.let { message ->
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = message,
//                            color = Color.Red,
//                            fontSize = 14.sp,
//                            modifier = Modifier.padding(horizontal = 16.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

package com.example.ticketbookingapp.Activities.Dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.Activities.SearchResult.SearchResultActivity
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.Activities.Splash.StatusTopBarColor
import com.example.ticketbookingapp.Domain.LocationModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.ViewModel.MainViewModel

class DashboardActivity : AppCompatActivity() {
    private val TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lấy UserModel từ Intent
        val user = intent.getSerializableExtra("user") as? UserModel
        if (user == null) {
            Log.e(TAG, "Không tìm thấy dữ liệu người dùng trong Intent")
            Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d(TAG, "Nhận được người dùng: ${user.username}")

        setContent {
            StatusTopBarColor()
            MainScreen(user = user)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }
    }
}

@Composable
fun BlueTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        color = colorResource(R.color.darkBlue),
        fontSize = 16.sp,
        modifier = modifier
    )
}

@Composable
fun MainScreen(user: UserModel) {
    val locations = remember { mutableStateListOf<LocationModel>() }
    val viewModel = MainViewModel()
    var showLocationLoading by remember { mutableStateOf(true) }
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var typeClass by remember { mutableStateOf("") }
    var adultPassenger by remember { mutableStateOf("0") }
    var childPassenger by remember { mutableStateOf("0") }
    var departureDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val TAG = "MainScreen"

    LaunchedEffect(Unit) {
        try {
            viewModel.loadLocations().observe(lifecycleOwner) { result ->
                Log.d(TAG, "Nhận được danh sách địa điểm: ${result?.size ?: 0}")
                if (result != null) {
                    locations.clear()
                    locations.addAll(result)
                    showLocationLoading = false
                } else {
                    errorMessage = "Không tải được danh sách địa điểm"
                    showLocationLoading = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi tải danh sách địa điểm: ${e.message}")
            errorMessage = "Lỗi tải danh sách địa điểm: ${e.message}"
            showLocationLoading = false
        }
    }

    Scaffold(
        bottomBar = { MyBottomBar(user = user, currentScreen = "Dashboard") },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite))
                .padding(paddingValues)
        ) {
            TopBar(user = user) // Di chuyển TopBar ra khỏi LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.lightGreyWhite))
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AutoSlider()
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp) // Thêm padding để căn giữa
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        Text(
                            text = "Tìm kiếm chuyến bay",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.darkBlue),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                            .background(Color.White, shape = RoundedCornerShape(20.dp))
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 24.dp)
                    ) {
                        BlueTitle("Điểm đi")
                        val locationNames = locations.map { it.Name }
                        DropDownList(
                            items = locationNames,
                            loadingIcon = painterResource(R.drawable.from_ic),
                            hint = "Chọn điểm đi",
                            showLocationLoading = showLocationLoading,
                            onItemSelected = { selectedItem ->
                                from = selectedItem
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BlueTitle("Điểm đến")
                        DropDownList(
                            items = locationNames,
                            loadingIcon = painterResource(R.drawable.to_ic),
                            hint = "Chọn điểm đến",
                            showLocationLoading = showLocationLoading,
                            onItemSelected = { selectedItem ->
                                to = selectedItem
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        BlueTitle("Hành khách")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PassengerCounter(
                                title = "Ng.lớn",
                                modifier = Modifier.weight(1f),
                                onItemSelected = { value ->
                                    val newValue = value.toIntOrNull() ?: 0
                                    adultPassenger = if (newValue >= 0) newValue.toString() else "0"
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            PassengerCounter(
                                title = "Trẻ em",
                                modifier = Modifier.weight(1f),
                                onItemSelected = { value ->
                                    val newValue = value.toIntOrNull() ?: 0
                                    childPassenger = if (newValue >= 0) newValue.toString() else "0"
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                BlueTitle("Ngày khởi hành")
                                Spacer(modifier = Modifier.height(10.dp))
                                DatePickerScreen(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    onDepartureSelected = { date ->
                                        departureDate = date
                                    },
                                    onReturnSelected = null
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                BlueTitle("Hạng ghế")
                                Spacer(modifier = Modifier.height(10.dp))
                                val classItems = listOf("Thương gia", "Hạng nhất", "Phổ thông")
                                DropDownList(
                                    items = classItems,
                                    loadingIcon = painterResource(R.drawable.seat_black_ic),
                                    hint = "Chọn hạng ghế",
                                    showLocationLoading = showLocationLoading,
                                    onItemSelected = { selectedItem ->
                                        typeClass = selectedItem
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        GradientButton(
                            onClick = {
                                try {
                                    if (from.isEmpty() || to.isEmpty() || departureDate.isEmpty() || typeClass.isEmpty()) {
                                        errorMessage = "Vui lòng điền đầy đủ các trường bắt buộc"
                                        return@GradientButton
                                    }
                                    val totalPassengers = (adultPassenger.toIntOrNull() ?: 0) +
                                            (childPassenger.toIntOrNull() ?: 0)
                                    if (totalPassengers <= 0) {
                                        errorMessage = "Cần ít nhất một hành khách"
                                        return@GradientButton
                                    }
                                    val intent = Intent(context, SearchResultActivity::class.java).apply {
                                        putExtra("from", from)
                                        putExtra("to", to)
                                        putExtra("departureDate", departureDate)
                                        putExtra("typeClass", typeClass)
                                        putExtra("numPassenger", totalPassengers.toString())
                                        putExtra("user", user)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Lỗi khởi chạy SearchResultActivity: ${e.message}")
                                    errorMessage = "Lỗi: Không thể tìm kiếm chuyến bay"
                                }
                            },
                            text = "Tìm kiếm",
                            padding = 0,
                            gradientColors = listOf(
                                colorResource(R.color.lightBlue),
                                colorResource(R.color.mediumBlue)
                            )
                        )

                        errorMessage?.let { message ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = message,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}