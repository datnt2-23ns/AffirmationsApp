package com.example.ticketbookingapp.Activities.Dashboard

import android.content.Context
import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DatePickerScreen(
    modifier: Modifier = Modifier,
    onDepartureSelected: (String) -> Unit, // Callback cho ngày đi
    onReturnSelected: ((String) -> Unit)? = null // Callback cho ngày về, nullable
) {
    val context = LocalContext.current
    // Định dạng ngày khớp với database (chữ thường)
    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.US) }
    val departureCalendar = remember { Calendar.getInstance() }
    // Khởi tạo departureDate với ngày hiện tại
    val initialDate = dateFormat.format(departureCalendar.time).toLowerCase(Locale.US)
    var departureDate by remember { mutableStateOf(initialDate) }

    // Gọi callback với ngày hiện tại ngay khi khởi tạo
    LaunchedEffect(Unit) {
        onDepartureSelected(initialDate)
    }

    // Chỉ hiển thị Departure Date Picker
    DatePickerItem(
        modifier = modifier,
        dateText = departureDate, // Luôn hiển thị departureDate
        onDataSelected = { selectedDate ->
            departureDate = selectedDate.toLowerCase(Locale.US)
            onDepartureSelected(departureDate)
        },
        dateFormat = dateFormat,
        calendar = departureCalendar,
        context = context
    )
}

@Composable
fun DatePickerItem(
    modifier: Modifier = Modifier,
    dateText: String,
    onDataSelected: (String) -> Unit,
    dateFormat: SimpleDateFormat,
    calendar: Calendar,
    context: Context
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 8.dp)
            .background(
                color = colorResource(R.color.lightGreyWhite), // Nền xám trắng
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                showDataPickerDialog(context, calendar, dateFormat, onDataSelected)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.calendar_ic),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
        )
        Text(
            text = dateText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            color = colorResource(R.color.darkBlue), // Văn bản darkBlue
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}

fun showDataPickerDialog(
    context: Context,
    calendar: Calendar,
    dateFormat: SimpleDateFormat,
    onDataSelected: (String) -> Unit
) {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    android.app.DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        calendar.set(selectedYear, selectedMonth, selectedDay)
        val formattedDate = dateFormat.format(calendar.time).toLowerCase(Locale.US)
        onDataSelected(formattedDate)
    }, year, month, day).show()
}