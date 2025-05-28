package com.example.ticketbookingapp.Activities.SeatSelect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.ticketbookingapp.R

@Composable
fun TopSection(
    modifier: Modifier,
    onBackClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.lightGreyWhite)) // Nền xám trắng
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
    ) {
        val (backBtn, headerTitle, worldImg) = createRefs()
        Image(
            painter = painterResource(R.drawable.back),
            contentDescription = null,
            modifier = Modifier
                .clickable { onBackClick() }
                .constrainAs(backBtn) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = "Chọn ghế ngồi",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colorResource(R.color.darkBlue), // Màu darkBlue
            modifier = Modifier
                .padding(start = 8.dp)
                .constrainAs(headerTitle) {
                    start.linkTo(backBtn.end, margin = 8.dp)
                    top.linkTo(backBtn.top)
                    bottom.linkTo(backBtn.bottom)
                }
        )
        Image(
            painter = painterResource(R.drawable.world),
            colorFilter = ColorFilter.tint(colorResource(R.color.lightBlue)),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(worldImg) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )
    }
}