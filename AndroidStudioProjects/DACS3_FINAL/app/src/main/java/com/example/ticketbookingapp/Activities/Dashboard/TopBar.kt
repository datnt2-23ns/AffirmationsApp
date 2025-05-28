//package com.example.ticketbookingapp.Activities.Dashboard
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstraintLayout
//import com.example.ticketbookingapp.Domain.UserModel
//import com.example.ticketbookingapp.R
//import androidx.compose.ui.tooling.preview.Preview
//
//@Composable
//fun TopBar(
//    user: UserModel,
//    title: String = stringResource(R.string.dashboard_title)
//) {
//    ConstraintLayout(
//        modifier = Modifier
//            .fillMaxWidth() // Full chiều rộng
//            .wrapContentHeight()
//            .padding(horizontal = 32.dp, vertical = 16.dp) // Padding cân đối
//    ) {
//        val (topbar, name, men, notification, titleText) = createRefs()
//        Image(
//            painter = painterResource(R.drawable.topbar),
//            contentDescription = "Biểu tượng thế giới",
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
//                .clickable { }
//                .constrainAs(topbar) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                }
//        )
//
//        Image(
//            painter = painterResource(R.drawable.men),
//            contentDescription = "Biểu tượng hồ sơ",
//            modifier = Modifier
//                .constrainAs(men) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                }
//        )
//
////        Image(
////            painter = painterResource(R.drawable.bell_icon),
////            contentDescription = "Biểu tượng thông báo",
////            modifier = Modifier
////                .constrainAs(notification) {
////                    top.linkTo(parent.top)
////                    bottom.linkTo(parent.bottom)
////                    end.linkTo(parent.end)
////                }
////        )
//
//        Text(
//            text = "Chào mừng ${user.username}!",
//            color = colorResource(R.color.darkBlue),
//            fontWeight = FontWeight.Bold,
//            fontSize = 35.sp,
//            modifier = Modifier.constrainAs(name) {
//                top.linkTo(parent.top)
//                start.linkTo(men.end, margin = 8.dp)
//                bottom.linkTo(parent.bottom)
//            }
//        )
//
//        Text(
//            text = title,
//            color = colorResource(R.color.darkBlue),
//            fontWeight = FontWeight.Bold,
//            fontSize = 25.sp,
//            modifier = Modifier
//                .constrainAs(titleText) {
//                    top.linkTo(men.bottom, margin = 8.dp)
//                    start.linkTo(parent.start)
//                    bottom.linkTo(parent.bottom)
//                }
//        )
//    }
//}
//
//@Preview
//@Composable
//fun TopBarPreview() {
//    val dummyUser = UserModel(
//        username = "dante_123",
//        password = "UserTBA@123_",
//        role = "user",
//        email = "dante_123@gmail.com",
//        fullName = "Dante",
//        dateOfBirth = "19/09/2005",
//        gender = "Nam",
//        phoneNumber = "0987654321"
//    )
//    TopBar(user = dummyUser, title = stringResource(R.string.dashboard_title))
//}


package com.example.ticketbookingapp.Activities.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.ticketbookingapp.Domain.UserModel
import com.example.ticketbookingapp.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopBar(
    user: UserModel,
    title: String = stringResource(R.string.dashboard_title)
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .windowInsetsPadding(WindowInsets.statusBars) // Hỗ trợ edge-to-edge
    ) {
        val (topbar, name, men, notification, titleText) = createRefs()
        Image(
            painter = painterResource(R.drawable.topbar),
            contentDescription = "Biểu tượng thế giới",
            contentScale = ContentScale.FillWidth, // Kéo giãn theo chiều rộng
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .clickable { }
                .constrainAs(topbar) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end) // Đảm bảo kéo dài đến cạnh phải
                }
        )

        Image(
            painter = painterResource(R.drawable.men),
            contentDescription = "Biểu tượng hồ sơ",
            modifier = Modifier
                .constrainAs(men) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, margin = 16.dp) // Margin để căn chỉnh
                }
        )

        Text(
            text = "Chào mừng ${user.username}!",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top)
                start.linkTo(men.end, margin = 8.dp)
                bottom.linkTo(parent.bottom)
            }
        )

        Text(
            text = title,
//            color = colorResource(R.color.darkBlue),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            modifier = Modifier
                .constrainAs(titleText) {
                    top.linkTo(men.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    val dummyUser = UserModel(
        username = "dante_123",
        password = "UserTBA@123_",
        role = "user",
        email = "dante_123@gmail.com",
        fullName = "Dante",
        dateOfBirth = "19/09/2005",
        gender = "Nam",
        phoneNumber = "0987654321"
    )
    TopBar(user = dummyUser, title = stringResource(R.string.dashboard_title))
}