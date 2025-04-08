//package com.example.newstripepayment.view
//
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.newstripepayment.R
//
//@Composable
//fun PaymentSuccessScreen(
//    amountPaid: Double,
//    onDone: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Icon(
//            painter = painterResource(id = R.drawable.ic_check_circle),
//            contentDescription = "Success",
//            tint = Color.Green,
//            modifier = Modifier.size(100.dp)
//        )
//
//        Text(
//            text = "Payment Successful!",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(top = 16.dp)
//        )
//
//        Text(
//            text = "Amount Paid: ${"%.2f".format(amountPaid)} €",
//            fontSize = 20.sp,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//
//        Button(
//            onClick = onDone,
//            modifier = Modifier.padding(top = 24.dp)
//        ) {
//            Text("Done")
//        }
//    }
//}