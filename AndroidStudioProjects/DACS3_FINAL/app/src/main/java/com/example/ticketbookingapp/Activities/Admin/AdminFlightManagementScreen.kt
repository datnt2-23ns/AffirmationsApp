package com.example.ticketbookingapp.Activities.Admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ticketbookingapp.Activities.Splash.GradientButton
import com.example.ticketbookingapp.Domain.FlightModel
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import com.example.ticketbookingapp.ViewModel.AdminViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlightManagementScreen(
    adminViewModel: AdminViewModel,
    user: UserModel,
    onBackToDashboard: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var showAddFlightDialog by remember { mutableStateOf(false) }
    var showEditFlightDialog by remember { mutableStateOf<FlightModel?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<FlightModel?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val flights by adminViewModel.flights.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý chuyến bay",
                        color = colorResource(R.color.darkBlue),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackToDashboard() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = colorResource(R.color.darkBlue)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.lightGreyWhite)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.lightGreyWhite))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flights) { flight ->
                        FlightItem(
                            flight = flight,
                            onEdit = { showEditFlightDialog = flight },
                            onDelete = { showDeleteConfirmDialog = flight }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                GradientButton(
                    onClick = { showAddFlightDialog = true },
                    text = "Thêm chuyến bay",
                    padding = 0,
                    gradientColors = listOf(
                        colorResource(R.color.lightBlue),
                        colorResource(R.color.mediumBlue)
                    )
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(R.color.lightBlue)
                )
            }

            if (showAddFlightDialog) {
                AddFlightDialog(
                    onDismiss = { showAddFlightDialog = false },
                    onAddFlight = { newFlight ->
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                adminViewModel.addFlight(newFlight)
                                Toast.makeText(context, "Chuyến bay đã được thêm thành công", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                                showAddFlightDialog = false
                            }
                        }
                    }
                )
            }

            showEditFlightDialog?.let { flight ->
                EditFlightDialog(
                    flight = flight,
                    onDismiss = { showEditFlightDialog = null },
                    onUpdateFlight = { updatedFlight ->
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                adminViewModel.updateFlight(updatedFlight)
                                Toast.makeText(context, "Chuyến bay đã được cập nhật thành công", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                                showEditFlightDialog = null
                            }
                        }
                    }
                )
            }

            showDeleteConfirmDialog?.let { flight ->
                DeleteConfirmDialog(
                    flight = flight,
                    onDismiss = { showDeleteConfirmDialog = null },
                    onConfirm = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                adminViewModel.deleteFlight(flight.FlightId)
                                Toast.makeText(context, "Chuyến bay đã được xóa thành công", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                                showDeleteConfirmDialog = null
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FlightItem(flight: FlightModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${flight.AirlineName} (${flight.FlightId})",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${flight.From} (${flight.FromShort}) -> ${flight.To} (${flight.ToShort})",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ngày: ${flight.Date}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Giờ: ${flight.Time}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Thời gian chuyến bay: ${flight.ArriveTime}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Số ghế: ${flight.NumberSeat}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Giá: ${String.format("%.2f", flight.Price)}" + " VNĐ",
                    color = colorResource(R.color.lightBlue),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Hạng ghế: ${flight.ClassSeat}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Hạng vé: ${flight.TypeClass}",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Trạng thái: ${
                        when (flight.status) {
                            "SCHEDULED" -> "Sắp tới"
                            "COMPLETED" -> "Đã hoàn thành"
                            "DELAYED" -> "Hoãn"
                            "IN_FLIGHT" -> "Đang bay"
                            else -> "Không xác định"
                        }
                    }",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.lightBlue),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Sửa",
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.lightBlue),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Xóa",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmDialog(
    flight: FlightModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Xác nhận xóa chuyến bay",
                    fontSize = 22.sp,
                    color = colorResource(R.color.darkBlue),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bạn có chắc chắn muốn xóa chuyến bay ${flight.FlightId} (${flight.AirlineName}) từ ${flight.From} đến ${flight.To} không?",
                    color = colorResource(R.color.darkBlue),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.darkBlue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Hủy",
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.lightBlue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Xóa",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightDialog(onDismiss: () -> Unit, onAddFlight: (FlightModel) -> Unit) {
    var flightId by remember { mutableStateOf("") }
    var airlineName by remember { mutableStateOf("") }
    var airlineLogo by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var fromShort by remember { mutableStateOf("") }
    var toShort by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var arriveTime by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var numberSeat by remember { mutableStateOf("") }
    var classSeat by remember { mutableStateOf("") }
    var typeClass by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var statusExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var validateTrigger by remember { mutableStateOf(false) }

    val validLocations = listOf(
        "Tân Sơn Nhất", "Nội Bài", "Đà Nẵng", "Phú Quốc", "Cát Bi",
        "Cà Mau", "Vân Đồn", "Đồng Hới", "Thọ Xuân", "Rạch Giá", "Điện Biên Phủ"
    )
    val validTypeClasses = listOf("Phổ thông", "Thương gia", "Hạng nhất")
    val validClassSeats = listOf("Hạng phổ thông", "Hạng thương gia", "Hạng nhất")
    val statusOptions = listOf(
        "SCHEDULED" to "Sắp tới",
        "COMPLETED" to "Đã hoàn thành",
        "DELAYED" to "Hoãn",
        "IN_FLIGHT" to "Đang bay"
    )
    val context = LocalContext.current

    LaunchedEffect(validateTrigger) {
        if (validateTrigger) {
            when {
                flightId.isBlank() -> {
                    errorMessage = "Mã chuyến bay không được để trống"
                    Toast.makeText(context, "Mã chuyến bay không được để trống", Toast.LENGTH_SHORT).show()
                }
                airlineName.isBlank() -> {
                    errorMessage = "Tên hãng hàng không không được để trống"
                    Toast.makeText(context, "Tên hãng hàng không không được để trống", Toast.LENGTH_SHORT).show()
                }
                airlineLogo.isBlank() -> {
                    errorMessage = "Logo hãng hàng không không được để trống"
                    Toast.makeText(context, "Logo hãng hàng không không được để trống", Toast.LENGTH_SHORT).show()
                }
                from !in validLocations -> {
                    errorMessage = "Điểm khởi hành không hợp lệ"
                    Toast.makeText(context, "Điểm khởi hành không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                to !in validLocations -> {
                    errorMessage = "Điểm đến không hợp lệ"
                    Toast.makeText(context, "Điểm đến không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                from == to -> {
                    errorMessage = "Điểm khởi hành và điểm đến phải khác nhau"
                    Toast.makeText(context, "Điểm khởi hành và điểm đến phải khác nhau", Toast.LENGTH_SHORT).show()
                }
                fromShort.isBlank() -> {
                    errorMessage = "Mã điểm khởi hành không được để trống"
                    Toast.makeText(context, "Mã điểm khởi hành không được để trống", Toast.LENGTH_SHORT).show()
                }
                toShort.isBlank() -> {
                    errorMessage = "Mã điểm đến không được để trống"
                    Toast.makeText(context, "Mã điểm đến không được để trống", Toast.LENGTH_SHORT).show()
                }
                date.isBlank() || !isValidDate(date) -> {
                    errorMessage = "Ngày không hợp lệ (định dạng: dd mmm, yyyy)"
                    Toast.makeText(context, "Ngày không hợp lệ (định dạng: dd mmm, yyyy)", Toast.LENGTH_SHORT).show()
                }
                time.isBlank() || !isValidTime(time) -> {
                    errorMessage = "Giờ không hợp lệ (định dạng: HH:mm)"
                    Toast.makeText(context, "Giờ không hợp lệ (định dạng: HH:mm)", Toast.LENGTH_SHORT).show()
                }
                arriveTime.isBlank() || !isValidArriveTime(arriveTime) -> {
                    errorMessage = "Thời gian chuyến bay không hợp lệ (định dạng: Xh Ym)"
                    Toast.makeText(context, "Thời gian chuyến bay không hợp lệ (định dạng: Xh Ym)", Toast.LENGTH_SHORT).show()
                }
                price.isBlank() || price.toDoubleOrNull()?.let { it <= 0 } ?: true -> {
                    errorMessage = "Giá không hợp lệ"
                    Toast.makeText(context, "Giá không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                numberSeat.isBlank() || numberSeat.toIntOrNull()?.let { it <= 0 } ?: true -> {
                    errorMessage = "Số lượng ghế không hợp lệ"
                    Toast.makeText(context, "Số lượng ghế không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                classSeat !in validClassSeats -> {
                    errorMessage = "Hạng ghế không hợp lệ (Hạng phổ thông, Hạng thương gia, Hạng nhất)"
                    Toast.makeText(context, "Hạng ghế không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                typeClass !in validTypeClasses -> {
                    errorMessage = "Hạng vé không hợp lệ (Phổ thông, Thương gia, Hạng nhất)"
                    Toast.makeText(context, "Hạng vé không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                status.isBlank() -> {
                    errorMessage = "Trạng thái không được để trống"
                    Toast.makeText(context, "Trạng thái không được để trống", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    errorMessage = null
                    val newFlight = FlightModel(
                        FlightId = flightId,
                        AirlineLogo = airlineLogo,
                        AirlineName = airlineName,
                        ArriveTime = arriveTime,
                        ClassSeat = classSeat,
                        TypeClass = typeClass,
                        Date = date,
                        From = from,
                        FromShort = fromShort,
                        To = to,
                        ToShort = toShort,
                        NumberSeat = numberSeat.toInt(),
                        Price = price.toDouble(),
                        ReservedSeats = "",
                        status = status,
                        Time = time
                    )
                    onAddFlight(newFlight)
                }
            }
            validateTrigger = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Thêm chuyến bay mới",
                    fontSize = 22.sp,
                    color = colorResource(R.color.darkBlue),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = flightId,
                    onValueChange = { value -> flightId = value },
                    label = { Text("Mã chuyến bay", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = airlineName,
                    onValueChange = { value -> airlineName = value },
                    label = { Text("Tên hãng hàng không", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = airlineLogo,
                    onValueChange = { value -> airlineLogo = value },
                    label = { Text("URL logo hãng hàng không", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = from,
                    onValueChange = { value -> from = value },
                    label = { Text("Điểm khởi hành", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fromShort,
                    onValueChange = { value -> fromShort = value },
                    label = { Text("Mã điểm khởi hành (ví dụ: HAN)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = to,
                    onValueChange = { value -> to = value },
                    label = { Text("Điểm đến", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = toShort,
                    onValueChange = { value -> toShort = value },
                    label = { Text("Mã điểm đến (ví dụ: SGN)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { value -> date = value },
                    label = { Text("Ngày (dd mmm, yyyy)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { value -> time = value },
                    label = { Text("Giờ (HH:mm)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = arriveTime,
                    onValueChange = { value -> arriveTime = value },
                    label = { Text("Thời gian chuyến bay (Xh Ym)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { value -> price = value },
                    label = { Text("Giá", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = numberSeat,
                    onValueChange = { value -> numberSeat = value },
                    label = { Text("Số lượng ghế", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = classSeat,
                    onValueChange = { value -> classSeat = value },
                    label = { Text("Hạng ghế (Hạng phổ thông, Hạng thương gia, Hạng nhất)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = typeClass,
                    onValueChange = { value -> typeClass = value },
                    label = { Text("Hạng vé (Phổ thông, Thương gia, Hạng nhất)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = statusOptions.find { it.first == status }?.second ?: "",
                        onValueChange = {},
                        label = { Text("Trạng thái", color = colorResource(R.color.darkBlue)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { statusExpanded = true }
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            disabledTextColor = Color.Gray,
                            errorTextColor = Color.Red,
                            cursorColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.darkBlue),
                            unfocusedBorderColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = Color.Gray,
                            errorBorderColor = Color.Red,
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = Color.Gray,
                            errorLabelColor = Color.Red,
                            containerColor = Color.White
                        )
                    )
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.second, fontSize = 16.sp) },
                                onClick = {
                                    status = option.first
                                    statusExpanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.darkBlue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Hủy",
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { validateTrigger = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.lightBlue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Thêm",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlightDialog(flight: FlightModel, onDismiss: () -> Unit, onUpdateFlight: (FlightModel) -> Unit) {
    var flightId by remember { mutableStateOf(flight.FlightId) }
    var airlineName by remember { mutableStateOf(flight.AirlineName) }
    var airlineLogo by remember { mutableStateOf(flight.AirlineLogo) }
    var from by remember { mutableStateOf(flight.From) }
    var to by remember { mutableStateOf(flight.To) }
    var fromShort by remember { mutableStateOf(flight.FromShort) }
    var toShort by remember { mutableStateOf(flight.ToShort) }
    var date by remember { mutableStateOf(flight.Date) }
    var time by remember { mutableStateOf(flight.Time) }
    var arriveTime by remember { mutableStateOf(flight.ArriveTime) }
    var price by remember { mutableStateOf(flight.Price.toString()) }
    var numberSeat by remember { mutableStateOf(flight.NumberSeat.toString()) }
    var classSeat by remember { mutableStateOf(flight.ClassSeat) }
    var typeClass by remember { mutableStateOf(flight.TypeClass) }
    var status by remember { mutableStateOf(flight.status) }
    var statusExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var validateTrigger by remember { mutableStateOf(false) }

    val validLocations = listOf(
        "Tân Sơn Nhất", "Nội Bài", "Đà Nẵng", "Phú Quốc", "Cát Bi",
        "Cà Mau", "Vân Đồn", "Đồng Hới", "Thọ Xuân", "Rạch Giá", "Điện Biên Phủ"
    )
    val validTypeClasses = listOf("Phổ thông", "Thương gia", "Hạng nhất")
    val validClassSeats = listOf("Hạng phổ thông", "Hạng thương gia", "Hạng nhất")
    val statusOptions = listOf(
        "SCHEDULED" to "Sắp tới",
        "COMPLETED" to "Đã hoàn thành",
        "DELAYED" to "Hoãn",
        "IN_FLIGHT" to "Đang bay"
    )
    val context = LocalContext.current

    LaunchedEffect(validateTrigger) {
        if (validateTrigger) {
            when {
                flightId.isBlank() -> {
                    errorMessage = "Mã chuyến bay không được để trống"
                    Toast.makeText(context, "Mã chuyến bay không được để trống", Toast.LENGTH_SHORT).show()
                }
                airlineName.isBlank() -> {
                    errorMessage = "Tên hãng hàng không không được để trống"
                    Toast.makeText(context, "Tên hãng hàng không không được để trống", Toast.LENGTH_SHORT).show()
                }
                airlineLogo.isBlank() -> {
                    errorMessage = "Logo hãng hàng không không được để trống"
                    Toast.makeText(context, "Logo hãng hàng không không được để trống", Toast.LENGTH_SHORT).show()
                }
                from !in validLocations -> {
                    errorMessage = "Điểm khởi hành không hợp lệ"
                    Toast.makeText(context, "Điểm khởi hành không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                to !in validLocations -> {
                    errorMessage = "Điểm đến không hợp lệ"
                    Toast.makeText(context, "Điểm đến không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                from == to -> {
                    errorMessage = "Điểm khởi hành và điểm đến phải khác nhau"
                    Toast.makeText(context, "Điểm khởi hành và điểm đến phải khác nhau", Toast.LENGTH_SHORT).show()
                }
                fromShort.isBlank() -> {
                    errorMessage = "Mã điểm khởi hành không được để trống"
                    Toast.makeText(context, "Mã điểm khởi hành không được để trống", Toast.LENGTH_SHORT).show()
                }
                toShort.isBlank() -> {
                    errorMessage = "Mã điểm đến không được để trống"
                    Toast.makeText(context, "Mã điểm đến không được để trống", Toast.LENGTH_SHORT).show()
                }
                date.isBlank() || !isValidDate(date) -> {
                    errorMessage = "Ngày không hợp lệ (định dạng: dd mmm, yyyy)"
                    Toast.makeText(context, "Ngày không hợp lệ (định dạng: dd mmm, yyyy)", Toast.LENGTH_SHORT).show()
                }
                time.isBlank() || !isValidTime(time) -> {
                    errorMessage = "Giờ không hợp lệ (định dạng: HH:mm)"
                    Toast.makeText(context, "Giờ không hợp lệ (định dạng: HH:mm)", Toast.LENGTH_SHORT).show()
                }
                arriveTime.isBlank() || !isValidArriveTime(arriveTime) -> {
                    errorMessage = "Thời gian chuyến bay không hợp lệ (định dạng: Xh Ym)"
                    Toast.makeText(context, "Thời gian chuyến bay không hợp lệ (định dạng: Xh Ym)", Toast.LENGTH_SHORT).show()
                }
                price.isBlank() || price.toDoubleOrNull()?.let { it <= 0 } ?: true -> {
                    errorMessage = "Giá không hợp lệ"
                    Toast.makeText(context, "Giá không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                numberSeat.isBlank() || numberSeat.toIntOrNull()?.let { it <= 0 } ?: true -> {
                    errorMessage = "Số lượng ghế không hợp lệ"
                    Toast.makeText(context, "Số lượng ghế không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                classSeat !in validClassSeats -> {
                    errorMessage = "Hạng ghế không hợp lệ (Hạng phổ thông, Hạng thương gia, Hạng nhất)"
                    Toast.makeText(context, "Hạng ghế không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                typeClass !in validTypeClasses -> {
                    errorMessage = "Hạng vé không hợp lệ (Phổ thông, Thương gia, Hạng nhất)"
                    Toast.makeText(context, "Hạng vé không hợp lệ", Toast.LENGTH_SHORT).show()
                }
                status.isBlank() -> {
                    errorMessage = "Trạng thái không được để trống"
                    Toast.makeText(context, "Trạng thái không được để trống", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    errorMessage = null
                    val updatedFlight = FlightModel(
                        FlightId = flightId,
                        AirlineLogo = airlineLogo,
                        AirlineName = airlineName,
                        ArriveTime = arriveTime,
                        ClassSeat = classSeat,
                        TypeClass = typeClass,
                        Date = date,
                        From = from,
                        FromShort = fromShort,
                        To = to,
                        ToShort = toShort,
                        NumberSeat = numberSeat.toInt(),
                        Price = price.toDouble(),
                        ReservedSeats = flight.ReservedSeats,
                        Passenger = flight.Passenger,
                        Seats = flight.Seats,
                        bookingTime = flight.bookingTime,
                        Time = time, // Thêm trường Time để đảm bảo giá trị được lưu
                        status = status
                    )
                    println("Updating flight with Time: $time") // Log để debug
                    onUpdateFlight(updatedFlight)
                }
            }
            validateTrigger = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chỉnh sửa chuyến bay",
                    fontSize = 22.sp,
                    color = colorResource(R.color.darkBlue),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = flightId,
                    onValueChange = { value -> flightId = value },
                    label = { Text("Mã chuyến bay", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = airlineName,
                    onValueChange = { value -> airlineName = value },
                    label = { Text("Tên hãng hàng không", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = airlineLogo,
                    onValueChange = { value -> airlineLogo = value },
                    label = { Text("URL logo hãng hàng không", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = from,
                    onValueChange = { value -> from = value },
                    label = { Text("Điểm khởi hành", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fromShort,
                    onValueChange = { value -> fromShort = value },
                    label = { Text("Mã điểm khởi hành (ví dụ: HAN)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = to,
                    onValueChange = { value -> to = value },
                    label = { Text("Điểm đến", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = toShort,
                    onValueChange = { value -> toShort = value },
                    label = { Text("Mã điểm đến (ví dụ: SGN)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { value -> date = value },
                    label = { Text("Ngày (dd mmm, yyyy)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { value -> time = value },
                    label = { Text("Giờ (HH:mm)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = arriveTime,
                    onValueChange = { value -> arriveTime = value },
                    label = { Text("Thời gian chuyến bay (Xh Ym)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { value -> price = value },
                    label = { Text("Giá", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = numberSeat,
                    onValueChange = { value -> numberSeat = value },
                    label = { Text("Số lượng ghế", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = classSeat,
                    onValueChange = { value -> classSeat = value },
                    label = { Text("Hạng ghế (Hạng phổ thông, Hạng thương gia, Hạng nhất)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = typeClass,
                    onValueChange = { value -> typeClass = value },
                    label = { Text("Hạng vé (Phổ thông, Thương gia, Hạng nhất)", color = colorResource(R.color.darkBlue)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = colorResource(R.color.darkBlue),
                        unfocusedTextColor = colorResource(R.color.darkBlue),
                        disabledTextColor = Color.Gray,
                        errorTextColor = Color.Red,
                        cursorColor = colorResource(R.color.darkBlue),
                        focusedBorderColor = colorResource(R.color.darkBlue),
                        unfocusedBorderColor = colorResource(R.color.darkBlue),
                        disabledBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = colorResource(R.color.darkBlue),
                        unfocusedLabelColor = colorResource(R.color.darkBlue),
                        disabledLabelColor = Color.Gray,
                        errorLabelColor = Color.Red,
                        containerColor = Color.White
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = statusOptions.find { it.first == status }?.second ?: "",
                        onValueChange = {},
                        label = { Text("Trạng thái", color = colorResource(R.color.darkBlue)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { statusExpanded = true }
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = colorResource(R.color.darkBlue),
                            unfocusedTextColor = colorResource(R.color.darkBlue),
                            disabledTextColor = Color.Gray,
                            errorTextColor = Color.Red,
                            cursorColor = colorResource(R.color.darkBlue),
                            focusedBorderColor = colorResource(R.color.darkBlue),
                            unfocusedBorderColor = colorResource(R.color.darkBlue),
                            disabledBorderColor = Color.Gray,
                            errorBorderColor = Color.Red,
                            focusedLabelColor = colorResource(R.color.darkBlue),
                            unfocusedLabelColor = colorResource(R.color.darkBlue),
                            disabledLabelColor = Color.Gray,
                            errorLabelColor = Color.Red,
                            containerColor = Color.White
                        )
                    )
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.second, fontSize = 16.sp) },
                                onClick = {
                                    status = option.first
                                    statusExpanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.darkBlue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Hủy",
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { validateTrigger = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.lightBlue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Cập nhật",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

private fun isValidDate(date: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
        sdf.isLenient = false
        sdf.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}

private fun isValidTime(time: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        sdf.isLenient = false
        sdf.parse(time)
        true
    } catch (e: Exception) {
        false
    }
}

private fun isValidArriveTime(arriveTime: String): Boolean {
    return arriveTime.matches(Regex("\\d+h \\d+m"))
}