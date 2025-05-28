//package com.example.ticketbookingapp.Activities.Dashboard
//
//import android.content.Intent
//import android.net.Uri
//import android.widget.Toast
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.ticketbookingapp.R
//
//data class PopularDestination(
//    val imageRes: Int,
//    val webUrl: String,
//    val name: String
//)
//
//@Composable
//fun AutoSlider(modifier: Modifier = Modifier) {
//    Column(
//        modifier = modifier
//            .background(colorResource(R.color.lightGreyWhite))
//            .padding(top = 20.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .clip(RoundedCornerShape(12.dp))
//                .background(Color.White)
//                .width(330.dp)
//                .align(Alignment.CenterHorizontally)
//        ) {
//            Text(
//                text = "Các điểm đến thu hút nhất",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier
//                    .padding(horizontal = 16.dp, vertical = 8.dp)
//                    .fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//        }
//
//        val destinations = listOf(
//            PopularDestination(R.drawable.dn2, "https://vietnam.travel/places-to-go/central-vietnam/da-nang", ""),
//            PopularDestination(R.drawable.nt2, "https://vietnam.travel/things-to-do/7-reasons-visit-nha-trang", ""),
//            PopularDestination(R.drawable.vt2, "https://vietnam.travel/things-to-do/essential-vung-tau-guide", ""),
//            PopularDestination(R.drawable.pq2, "https://vietnam.travel/places-to-go/southern-vietnam/phu-quoc", ""),
//            PopularDestination(R.drawable.hcm2, "https://vietnam.travel/places-to-go/southern-vietnam/ho-chi-minh-city", ""),
//            PopularDestination(R.drawable.hn2, "https://vietnam.travel/places-to-go/northern-vietnam/ha-noi", "")
//        )
//
//        val itemsPerPage = 3 // Số lượng hình ảnh trên mỗi trang
//        val pageCount = (destinations.size + itemsPerPage - 1) / itemsPerPage // Tính số trang
//        val pagerState = rememberPagerState(pageCount = { pageCount }, initialPage = 0)
//
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier
//                .height(200.dp)
//                .fillMaxWidth()
//                .background(colorResource(R.color.lightGreyWhite)),
//            pageSpacing = 16.dp
//        ) { page ->
//            Row(
//                modifier = Modifier
//                    .height(180.dp)
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(-8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                val startIndex = page * itemsPerPage
//                for (i in 0 until itemsPerPage) {
//                    val index = startIndex + i
//                    if (index < destinations.size) {
//                        DestinationItem(destinations[index], Modifier.weight(1f))
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DestinationItem(destination: PopularDestination, modifier: Modifier = Modifier) {
//    val context = LocalContext.current // Lấy Context từ Compose
//
//    Column(
//        modifier = modifier
//            .height(180.dp)
//            .width(120.dp)
//            .padding(14.dp)
//            .background(colorResource(R.color.white), shape = RoundedCornerShape(16.dp)),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(id = destination.imageRes),
//            contentDescription = destination.name,
//            modifier = Modifier
//                .height(140.dp)
//                .width(120.dp)
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(16.dp))
//                .clickable {
//                    // Xử lý sự kiện nhấn để mở URL
//                    try {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.webUrl))
//                        context.startActivity(intent)
//                    } catch (e: Exception) {
//                        Toast.makeText(context, "Không thể mở URL: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//                },
//            contentScale = ContentScale.Crop
//        )
//        Text(
//            text = destination.name,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(top = 4.dp)
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun AutoSliderPreview() {
//    AutoSlider()
//}

package com.example.ticketbookingapp.Activities.Dashboard

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketbookingapp.R

data class PopularDestination(
    val imageRes: Int,
    val webUrl: String,
    val name: String
)

@Composable
fun AutoSlider(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(colorResource(R.color.lightGreyWhite))
            .padding(top = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .width(330.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Các điểm đến thu hút nhất",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.darkBlue),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        val destinations = listOf(
            PopularDestination(R.drawable.dn2, "https://vietnam.travel/places-to-go/central-vietnam/da-nang", "Đà Nẵng"),
            PopularDestination(R.drawable.nt2, "https://vietnam.travel/things-to-do/7-reasons-visit-nha-trang", "Nha Trang"),
            PopularDestination(R.drawable.vt2, "https://vietnam.travel/things-to-do/essential-vung-tau-guide", "Vũng Tàu"),
            PopularDestination(R.drawable.pq2, "https://vietnam.travel/places-to-go/southern-vietnam/phu-quoc", "Phú Quốc"),
            PopularDestination(R.drawable.hcm2, "https://vietnam.travel/places-to-go/southern-vietnam/ho-chi-minh-city", "TP. Hồ Chí Minh"),
            PopularDestination(R.drawable.hn2, "https://vietnam.travel/places-to-go/northern-vietnam/ha-noi", "Hà Nội")
        )

        val itemsPerPage = 3 // Số lượng hình ảnh trên mỗi trang
        val pageCount = (destinations.size + itemsPerPage - 1) / itemsPerPage // Tính số trang
        val pagerState = rememberPagerState(pageCount = { pageCount }, initialPage = 0)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(colorResource(R.color.lightGreyWhite)),
            pageSpacing = 16.dp
        ) { page ->
            Row(
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(-8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val startIndex = page * itemsPerPage
                for (i in 0 until itemsPerPage) {
                    val index = startIndex + i
                    if (index < destinations.size) {
                        DestinationItem(destinations[index], Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationItem(destination: PopularDestination, modifier: Modifier = Modifier) {
    val context = LocalContext.current // Lấy Context từ Compose

    Column(
        modifier = modifier
            .height(180.dp)
            .width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Phân bố đều nội dung
    ) {
        Image(
            painter = painterResource(id = destination.imageRes),
            contentDescription = destination.name,
            modifier = Modifier
                .height(160.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.webUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Không thể mở URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
            contentScale = ContentScale.Crop
        )
        Text(
            text = destination.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AutoSliderPreview() {
    AutoSlider()
}