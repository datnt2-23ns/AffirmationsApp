package com.example.ticketbookingapp.Activities.Utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QRCodeDialog(
    totalPrice: Double,
    flightId: String,
    showQR: Boolean,
    onDismiss: () -> Unit,
    generateQRCode: (String) -> Bitmap?
) {
    if (showQR) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            title = {
                Text(
                    text = "Quét mã QR để thanh toán",
                    style = TextStyle(fontSize = 20.sp)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val qrBitmap = generateQRCode("Thanh toán: $totalPrice cho chuyến bay $flightId")
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Mã QR",
                            modifier = Modifier
                                .size(200.dp)
                                .aspectRatio(1f)
                        )
                    } ?: Text(
                        text = "Không thể tạo mã QR",
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Vé của bạn sẽ được lưu tự động sau khi quét.",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun QRCodeDialogPreview() {
    var showQR by mutableStateOf(true)
    QRCodeDialog(
        totalPrice = 75.25,
        flightId = "FL123",
        showQR = showQR,
        onDismiss = { showQR = false },
        generateQRCode = { data ->
            try {
                val writer = com.google.zxing.qrcode.QRCodeWriter()
                val bitMatrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }
                bitmap
            } catch (e: Exception) {
                null
            }
        }
    )
}